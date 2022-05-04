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
package com.adobe.cq.wcm.core.components.it.seljup.tests.image;

import org.apache.commons.lang3.StringUtils;
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

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImageIT extends AuthorBaseUITest {
    private String proxyPath;
    private String testPage;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("test", "Test Page Title", rootPage, "/conf/core-email-components-examples/settings/wcm" +
                "/templates/email-template").getSlingPath();
        // create a proxy component
        String imageRT = "core/email/components/image";
        proxyPath = Commons.createProxyComponent(adminClient, imageRT, Commons.proxyPath, null, null);
        // add the core form container component
        Commons.addComponent(adminClient, proxyPath, testPage + Commons.relParentCompPath, "image", null);
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
    @DisplayName("Test: Set fixed width")
    void setFixedWidth() throws InterruptedException {
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        Actions act = new Actions(webDriver);
        openEditDialog(webDriver, act);
        openMetadataTab(webDriver, act);
        WebElement element = webDriver.findElement(By.cssSelector("[name='./fixedWidth']"));
        assertTrue(element.isDisplayed());
        assertTrue(element.isEnabled());
        click(act, element);
        element.clear();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        element.sendKeys("2500");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        confirmEditDialog(webDriver, act);
        viewAsPublished(webDriver);
        WebElement img = webDriver.findElement(By.tagName("img"));
        assertNotNull(img.getAttribute("width"));
        assertTrue(isAbsolute(img.getAttribute("src")));
        assertEquals("width: 2500px;", img.getAttribute("style"));
    }

    @Test
    @DisplayName("Test: Set scale image to available width")
    void setScaleImageToAvailableWidth() throws InterruptedException {
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        Actions act = new Actions(webDriver);
        openEditDialog(webDriver, act);
        openMetadataTab(webDriver, act);
        WebElement element = webDriver.findElement(By.cssSelector("[name='./scaleToFullWidth']"));
        assertTrue(element.isDisplayed());
        assertTrue(element.isEnabled());
        click(act, element);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        confirmEditDialog(webDriver, act);
        viewAsPublished(webDriver);
        WebElement img = webDriver.findElement(By.tagName("img"));
        assertTrue(isAbsolute(img.getAttribute("src")));
        assertEquals("width: 100%;", img.getAttribute("style"));
    }

    private void openEditDialog(WebDriver webDriver, Actions act) throws InterruptedException {
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        click(act, webDriver.findElement(By.id("sidepanel-toggle-button")));
        click(act, webDriver.findElement(By.cssSelector("[title=\"Content Tree\"]")));
        click(act, webDriver.findElement(By.xpath("//span[.='image']")));
        click(act, webDriver.findElement(By.cssSelector("[data-action='CONFIGURE']")));
        loadImage(webDriver, act);
    }

    private void loadImage(WebDriver webDriver, Actions act) throws InterruptedException {
        click(act, webDriver.findElement(By.cssSelector("[title=\"Assets\"]")));
        click(act, webDriver.findElement(By.cssSelector("[name=\"./imageFromPageImage\"]")));
        $("coral-card.cq-draggable[data-path=\"/content/dam/core-components-examples/library/sample-assets/mountain-range.jpg\"]").dragAndDropTo(
                "coral-fileupload[name='./file']");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

    private void openMetadataTab(WebDriver webDriver, Actions act) throws InterruptedException {
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        click(act, webDriver.findElement(By.xpath("//coral-tab-label[.='Metadata']")));
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

    private boolean isAbsolute(String url) {
        return StringUtils.isNotEmpty(url) && url.startsWith("http");
    }

}
