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

import java.util.List;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SegmentationIT extends AuthorBaseUITest {
    public static final String EXPECTED_FIRST_SEGMENTATION_ITEM_CONTENT = "<% if (recipient.age >= 18) { %>\nAdults only\n<% } else if (recipient.age < 18) { %>";
    public static final String EXPECTED_SECOND_SEGMENTATION_ITEM_CONTENT = "Kids only\n<% } %>";
    private String proxyPath;
    private String testPage;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("test", "Test Page Title", rootPage, "/conf/core-email-components-examples/settings/wcm" +
                "/templates/email-template").getSlingPath();
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
        addSegmentationItem(webDriver, act, "first", "recipient.age >= 18", "Adults only");
        addSegmentationItem(webDriver, act, "second", "recipient.age < 18", "Kids only");
        viewAsPublished(webDriver);
        List<WebElement> elements = webDriver.findElements(By.xpath("//div[@class='segmentation-item']"));
        assertEquals(2, elements.size());
        String firstSegmentationContent = elements.get(0).getText();
        assertEquals(EXPECTED_FIRST_SEGMENTATION_ITEM_CONTENT, firstSegmentationContent);
        String secondSegmentationContent = elements.get(1).getText();
        assertEquals(EXPECTED_SECOND_SEGMENTATION_ITEM_CONTENT, secondSegmentationContent);
    }

    private void addSegmentationItem(WebDriver webDriver, Actions act, String name, String condition, String text)
            throws InterruptedException {
        openEditDialog(webDriver, act);
        click(act, webDriver.findElement(By.cssSelector("[data-cmp-hook-childreneditor=\"add\"]")));
        click(act, webDriver.findElement(By.xpath("//coral-selectlist-item[.='Segmentation Item component']")));
        List<WebElement> elements = webDriver.findElements(By.xpath("//coral-multifield-item-content/div/input[1]"));
        WebElement element = elements.get(elements.size() - 1);
        element.sendKeys(name);
        confirmEditDialog(webDriver, act);
        refresh(webDriver);
        openContentTree(webDriver, act);
        List<WebElement> segmentationItemComponents = webDriver.findElements(By.xpath("//span[@class='editor-ContentTree-itemTitle' and " +
                ".='Segmentation Item component']"));
        click(act, segmentationItemComponents.get(segmentationItemComponents.size() - 1));
        click(act, webDriver.findElement(By.cssSelector("[data-action='CONFIGURE']")));
        WebElement conditionElement = webDriver.findElement(By.name("./condition"));
        click(act, conditionElement);
        conditionElement.sendKeys(condition);
        confirmEditDialog(webDriver, act);
        addTextComponent(webDriver, act, text);
    }

    private void addTextComponent(WebDriver webDriver, Actions act, String text) throws InterruptedException {
        List<WebElement> dragComponentElements = webDriver.findElements(By.cssSelector("[data-text=\"Please drag components here\"]"));
        click(act, dragComponentElements.get(dragComponentElements.size() - 1));
        click(act, webDriver.findElement(By.cssSelector("[data-action=\"INSERT\"]")));
        click(act, webDriver.findElement(By.xpath("//coral-selectlist-item[.='Rich Text Component']")));
        refresh(webDriver);
        openContentTree(webDriver, act);
        List<WebElement> richTextComponents = webDriver.findElements(By.xpath("//span[@class='editor-ContentTree-itemTitle' and " +
                ".='Rich Text Component']"));
        click(act, richTextComponents.get(richTextComponents.size() - 1));
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
        click(act, webDriver.findElement(By.id("sidepanel-toggle-button")));
        click(act, webDriver.findElement(By.cssSelector("[title=\"Content Tree\"]")));
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

