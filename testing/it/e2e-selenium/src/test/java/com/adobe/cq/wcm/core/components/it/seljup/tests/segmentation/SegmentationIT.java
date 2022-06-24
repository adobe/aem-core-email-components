package com.adobe.cq.wcm.core.components.it.seljup.tests.segmentation;

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

import java.util.Objects;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.WebDriverRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SegmentationIT extends AuthorBaseUITest {
    private String proxyPath;
    private String testPage;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("test", "Test Page Title", rootPage,
                "/conf/core-email-components-examples/settings/wcm" + "/templates/email-template").getSlingPath();
        // create a proxy component
        String segmentationRT = "core/email/components/segmentation/v1/segmentation";
        proxyPath = Commons.createProxyComponent(adminClient, segmentationRT, Commons.proxyPath, null, null);
        // add the core form container component
        String segmentation = Commons.addComponent(adminClient, proxyPath, testPage + REL_PARENT_COMP_PATH, "segmentation", null);
        // open the page in the editor
        EditorPage editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,
                HttpStatus.SC_OK);
        // delete the proxy component created
        Commons.deleteProxyComponent(adminClient, proxyPath);
    }

    @Test
    @DisplayName("Test: Create segmentation")
    void createSegmentation() throws InterruptedException {
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        Actions act = new Actions(webDriver);
        addSegmentationItems(webDriver, act);
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

    private void addSegmentationItems(WebDriver webDriver, Actions act) throws InterruptedException {
        openEditDialog(webDriver, act);
        click(act, webDriver.findElement(By.cssSelector("[data-cmp-hook-segmenteditor='add']")));
        click(act, webDriver.findElement(By.xpath("//coral-selectlist-item[.='Rich Text Component']")));
        click(act, webDriver.findElement(By.xpath("//span[.='Select Condition']")));
        click(act, webDriver.findElement(By.cssSelector("[value='custom']")));
        WebElement itemTitle = webDriver.findElements(By.xpath("//input[@data-cmp-hook-segmenteditor='itemTitle']")).stream()
                .filter(WebElement::isDisplayed).findFirst().orElse(null);
        assertNotNull(itemTitle);
        click(act, itemTitle);
        itemTitle.sendKeys("first");
        WebElement itemCondition = webDriver.findElements(By.xpath("//input[@data-cmp-hook-segmenteditor='itemCondition']")).stream()
                .filter(WebElement::isDisplayed).findFirst().orElse(null);
        assertNotNull(itemCondition);
        click(act, itemCondition);
        itemCondition.sendKeys("recipient.age >= 18");
        click(act, webDriver.findElement(By.cssSelector("[data-cmp-hook-segmenteditor='add']")));
        click(act, webDriver.findElement(By.xpath("//coral-selectlist-item[.='Rich Text Component']")));
        click(act, webDriver.findElement(By.xpath("//span[.='Select Condition']")));
        WebElement defaultElement =
                webDriver.findElements(By.xpath("//coral-selectlist-item[@value='default']")).stream().filter(WebElement::isDisplayed)
                        .findFirst().orElse(null);
        assertNotNull(defaultElement);
        click(act, defaultElement);
        confirmEditDialog(webDriver, act);
        setValueInTextComponents(webDriver, act);
    }

    private void setValueInTextComponents(WebDriver webDriver, Actions act) throws InterruptedException {
        WebElement firstTextComponent =
                webDriver.findElements(By.xpath("//span[@class='editor-ContentTree-itemTitle' and " + ".='Rich Text Component']")).stream()
                        .findFirst().orElse(null);
        assertNotNull(firstTextComponent);
        setValueInTextComponent(webDriver, act, "Adults only", firstTextComponent);
        refresh(webDriver);
        openContentTree(webDriver, act);
        WebElement secondTextComponent =
                webDriver.findElements(By.xpath("//span[@class='editor-ContentTree-itemTitle' and " + ".='Rich Text Component']")).stream()
                        .findFirst().orElse(null);
        assertNotNull(secondTextComponent);
        setValueInTextComponent(webDriver, act, "Kids only", secondTextComponent);
    }

    private void setValueInTextComponent(WebDriver webDriver, Actions act, String text, WebElement richTextComponent)
            throws InterruptedException {
        click(act, richTextComponent);
        WebElement configureButton =
                webDriver.findElement(By.xpath("//button[@is='coral-button' and @title='Configure' and not(@hidden)]"));
        click(act, configureButton);
        WebElement richTextArea = webDriver.findElement(By.xpath("//div[@data-editor-type='text']"));
        click(act, richTextArea);
        richTextArea.sendKeys(text);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        click(act, webDriver.findElement(By.xpath("//button[@is='coral-button' and @title='Done']")));
    }

    private void openEditDialog(WebDriver webDriver, Actions act) throws InterruptedException {
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        WebElement contentTree = webDriver.findElement(By.cssSelector("[title=\"Content Tree\"]"));
        if (Objects.isNull(contentTree) || !contentTree.isDisplayed()) {
            click(act, webDriver.findElement(By.id("sidepanel-toggle-button")));
        }
        click(act, contentTree);
        click(act, webDriver.findElement(By.xpath("//span[.='segmentation']")));
        click(act, webDriver.findElement(By.cssSelector("[data-action='CONFIGURE']")));
    }

    private void openContentTree(WebDriver webDriver, Actions act) throws InterruptedException {
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        WebElement contentTreeButton = webDriver.findElement(By.cssSelector("[title=\"Content Tree\"]"));
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        if (!contentTreeButton.isDisplayed()) {
            click(act, webDriver.findElement(By.id("sidepanel-toggle-button")));
        }
        click(act, contentTreeButton);
    }

    private void refresh(WebDriver webDriver) throws InterruptedException {
        webDriver.navigate().refresh();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        ;
    }

    private void confirmEditDialog(WebDriver webDriver, Actions act) throws InterruptedException {
        click(act, webDriver.findElement(By.cssSelector("[title='Done']")));
    }

    private void viewAsPublished(WebDriver webDriver) throws InterruptedException {
        webDriver.get(webDriver.getCurrentUrl().replace("/editor.html", "") + "?wcmmode=disabled");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

    private void click(Actions act, WebElement element) throws InterruptedException {
        act.moveToElement(element).click().perform();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

}

