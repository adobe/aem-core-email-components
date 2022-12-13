/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.email.core.components.it.seljup.tests.segmentation;

import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.adobe.cq.email.core.components.it.seljup.util.components.segmentation.SegmentationEditDialog;
import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pagewidgets.cq.InsertComponentDialog;
import com.adobe.cq.email.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.email.core.components.it.seljup.util.commons.SegmentationEditor;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.commons.PanelSelector;
import com.adobe.cq.email.core.components.it.seljup.util.components.segmentation.v1.Segmentation;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.text.TextEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.WebDriverRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SegmentationIT extends AuthorBaseUITest {
    private String proxyPath;
    private String testPage;
    private PageEditorPage editorPage;
    private String cmpPath;
    private Segmentation segmentation;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("test", "Test Page Title", rootPage,
                "/conf/core-email-components-examples/settings/wcm" + "/templates/email-template").getSlingPath();
        editorPage = new PageEditorPage(testPage);
        // create a proxy component
        String segmentationRT = "core/email/components/segmentation/v1/segmentation";
        proxyPath = Commons.createProxyComponent(adminClient, segmentationRT, Commons.proxyPath, null, null);
        // add the core form container component
        cmpPath = Commons.addComponent(adminClient, proxyPath, testPage + REL_PARENT_COMP_PATH, "segmentation", null);
        // open the page in the editor
        EditorPage editorPage = new PageEditorPage(testPage);
        editorPage.open();
        segmentation = new Segmentation();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,
                HttpStatus.SC_OK);
        // delete the proxy component created
        Commons.deleteProxyComponent(adminClient, proxyPath);
    }

    private void createItems() throws InterruptedException, TimeoutException {
        //1.
        SegmentationEditDialog editDialog = segmentation.openEditDialog(cmpPath);
        editDialog.openItemsTab();
        //2.
        SegmentationEditor segmentationEditor = editDialog.getChildrenEditor();
        segmentationEditor.clickAddButton();
        InsertComponentDialog insertComponentDialog = editDialog.getInsertComponentDialog();
        insertComponentDialog.selectComponent("/apps/core-email-components-examples/components/text");
        segmentationEditor.setCustomCondition(segmentationEditor.getSelectConditions().last(), "Adults only", "recipient.age >= 18");
        segmentationEditor.clickAddButton();
        insertComponentDialog.selectComponent("/apps/core-email-components-examples/components/text");
        segmentationEditor.setCustomCondition(segmentationEditor.getSelectConditions().last(), "Kids only", "recipient.age < 18");
        //3.
        Commons.saveConfigureDialog();
        //4.
        Commons.openEditableToolbar(cmpPath);
        PanelSelector panelSelector = new PanelSelector();
        Commons.openPanelSelect();
        String firstItemPath = panelSelector.getItems().first().getAttribute("data-id");
        String secondItemPath = panelSelector.getItems().last().getAttribute("data-id");
        com.adobe.cq.email.core.components.it.seljup.util.Commons.openEditDialog(editorPage, firstItemPath);
        TextEditDialog textEditDialog = new TextEditDialog();
        textEditDialog.setText("<p>Adults only</p>");
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        segmentation.clickTab(1);
        Commons.switchToDefaultContext();
        editorPage.enterEditMode();
        com.adobe.cq.email.core.components.it.seljup.util.Commons.openEditDialog(editorPage, secondItemPath);
        textEditDialog = new TextEditDialog();
        textEditDialog.setText("<p>Kids only</p>");
        Commons.saveConfigureDialog();
    }

    @Test
    @DisplayName("Test: Create segmentation")
    void createSegmentation() throws InterruptedException, TimeoutException {
        createItems();
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        viewAsPublished(webDriver);
        WebElement firstOpeningAccMarkup = webDriver.findElement(By.xpath("//td[.='<% if (recipient.age >= 18) { %>']"));
        assertNotNull(firstOpeningAccMarkup);
        assertTrue(firstOpeningAccMarkup.isDisplayed());
        WebElement firstSegment = webDriver.findElement(By.xpath("//p[.='Adults only']"));
        assertNotNull(firstSegment);
        assertTrue(firstSegment.isDisplayed());
        WebElement firstClosingAccMarkup = webDriver.findElement(By.xpath("//td[.='<% } else if (recipient.age < 18) { %>']"));
        assertNotNull(firstClosingAccMarkup);
        assertTrue(firstClosingAccMarkup.isDisplayed());
        WebElement secondSegment = webDriver.findElement(By.xpath("//p[.='Kids only']"));
        assertNotNull(secondSegment);
        assertTrue(secondSegment.isDisplayed());
        WebElement secondClosingAccMarkup = webDriver.findElement(By.xpath("//td[.='<% } %>']"));
        assertNotNull(secondClosingAccMarkup);
        assertTrue(secondClosingAccMarkup.isDisplayed());
    }

    @Test
    void switchFromCustomToDefaultSegment() throws InterruptedException, TimeoutException {
        createItems();
        SegmentationEditDialog editDialog = segmentation.openEditDialog(cmpPath);
        editDialog.openItemsTab();
        SegmentationEditor segmentationEditor = editDialog.getChildrenEditor();
        segmentationEditor.setDefaultCondition(segmentationEditor.getSelectConditions().last());
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(segmentation.isTabPanelActive(1),"Default segment should be active");
        assertTrue(segmentation.getTabItems().stream().anyMatch(element -> element.getText().equals("Default")));
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        viewAsPublished(webDriver);
        WebElement firstOpeningAccMarkup = webDriver.findElement(By.xpath("//td[.='<% if (recipient.age >= 18) { %>']"));
        assertNotNull(firstOpeningAccMarkup);
        assertTrue(firstOpeningAccMarkup.isDisplayed());
        WebElement firstSegment = webDriver.findElement(By.xpath("//p[.='Adults only']"));
        assertNotNull(firstSegment);
        assertTrue(firstSegment.isDisplayed());
        WebElement firstClosingAccMarkup = webDriver.findElement(By.xpath("//td[.='<% } else { %>']"));
        assertNotNull(firstClosingAccMarkup);
        assertTrue(firstClosingAccMarkup.isDisplayed());
        WebElement secondSegment = webDriver.findElement(By.xpath("//p[.='Kids only']"));
        assertNotNull(secondSegment);
        assertTrue(secondSegment.isDisplayed());
        WebElement secondClosingAccMarkup = webDriver.findElement(By.xpath("//td[.='<% } %>']"));
        assertNotNull(secondClosingAccMarkup);
        assertTrue(secondClosingAccMarkup.isDisplayed());
    }

    private void viewAsPublished(WebDriver webDriver) throws InterruptedException {
        webDriver.get(webDriver.getCurrentUrl().replace("/editor.html", "") + "?wcmmode=disabled");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }
}

