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
package com.adobe.cq.email.core.components.it.seljup.tests.image;

import java.util.concurrent.TimeoutException;

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

import com.adobe.cq.email.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.email.core.components.it.seljup.util.components.image.ImageEditDialog;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.image.BaseImage;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.image.v1.Image;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.WebDriverRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImageIT extends AuthorBaseUITest {
    private static String assetFilter = "[name='assetfilter_image_path']";
    private static String assetDirectory = "/content/dam/core-email-components/sample-assets";
    private static String assetPath = assetDirectory + "/mountain-range.jpg";
    private static String altText = "Mountain Range";

    private String proxyPath;
    private String testPage;
    private String cmpPath;
    private PageEditorPage editorPage;
    private BaseImage image;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        image = new Image();
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("test", "Test Page Title", rootPage, "/conf/core-email-components-examples/settings/wcm" +
                "/templates/email-template").getSlingPath();
        // create a proxy component
        String imageRT = "core/email/components/image/v1/image";
        proxyPath = Commons.createProxyComponent(adminClient, imageRT, Commons.proxyPath, null, null);
        // add the core form container component
        cmpPath = Commons.addComponent(adminClient, proxyPath, testPage + REL_PARENT_COMP_PATH, "image", null);
        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
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
    void setFixedWidth() throws InterruptedException, TimeoutException {
        Commons.openSidePanel();
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        Actions act = new Actions(webDriver);
        loadImage();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        openMetadataTab(webDriver, act);
        WebElement element = webDriver.findElement(By.cssSelector("[name='./fixedWidth']"));
        assertTrue(element.isDisplayed());
        assertTrue(element.isEnabled());
        click(act, element);
        element.clear();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        element.sendKeys("2500");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        WebElement img = webDriver.findElement(By.tagName("img"));
        assertNotNull(img.getAttribute("width"));
        assertTrue(isAbsolute(img.getAttribute("src")));
        assertEquals("width: 2500px;", img.getAttribute("style"));
    }

    @Test
    @DisplayName("Test: Set scale image to available width")
    void setScaleImageToAvailableWidth() throws InterruptedException, TimeoutException {
        Commons.openSidePanel();
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        Actions act = new Actions(webDriver);
        loadImage();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        openMetadataTab(webDriver, act);
        WebElement element = webDriver.findElement(By.cssSelector("[name='./scaleToFullWidth']"));
        assertTrue(element.isDisplayed());
        assertTrue(element.isEnabled());
        click(act, element);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        WebElement img = webDriver.findElement(By.tagName("img"));
        assertTrue(isAbsolute(img.getAttribute("src")));
        assertEquals("width: 100%;", img.getAttribute("style"));
    }

    private void loadImage() throws InterruptedException, TimeoutException {
        com.adobe.cq.wcm.core.components.it.seljup.util.components.image.ImageEditDialog editDialog = image.getEditDialog();
        editDialog.setAssetFilter(assetDirectory);
        com.adobe.cq.email.core.components.it.seljup.util.Commons.openEditDialog(editorPage, cmpPath);
        editDialog.checkImageFromPageImage();
        editDialog.uploadImageFromSidePanel(assetPath);
    }

    private void setAssetFilter() {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + assetFilter);
        autoCompleteField.sendKeys(assetDirectory);
        autoCompleteField.suggestions().selectByValue(assetDirectory);
    }

    private void openMetadataTab(WebDriver webDriver, Actions act) throws InterruptedException {
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        click(act, webDriver.findElement(By.xpath("//coral-tab-label[.='Metadata']")));
    }

    private void click(Actions act, WebElement element) throws InterruptedException {
        act.moveToElement(element).click().perform();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

    private boolean isAbsolute(String url) {
        return StringUtils.isNotEmpty(url) && url.startsWith("http");
    }

}
