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

package com.adobe.cq.email.core.components.it.seljup.tests.teaser;

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
import org.openqa.selenium.interactions.Actions;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.email.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.WebDriverRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TeaserIT extends AuthorBaseUITest {

    private final String teaserRT = "core/email/components/teaser/v1/teaser";
    private String proxyPath;
    private String testPage;
    private String compPath;
    private EditorPage editorPage;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("test", "Test Page Title", rootPage, "/conf/core-email-components-examples/settings/wcm" +
                "/templates/email-template").getSlingPath();
        // create a proxy component
        proxyPath = Commons.createProxyComponent(adminClient, teaserRT, Commons.proxyPath, null, null);
        // add the core form container component
        compPath = Commons.addComponent(adminClient, proxyPath, testPage + REL_PARENT_COMP_PATH, "text", null);
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
    @DisplayName("Test: Check if personalization plugin buttons exist in configure dialog (Teaser component)")
    public void testCheckConfigureDialog() throws TimeoutException, InterruptedException {
        com.adobe.cq.email.core.components.it.seljup.util.Commons.openEditDialog(this.editorPage, this.compPath);
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        Actions act = new Actions(webDriver);
        WebElement textTab = webDriver.findElement(By.xpath("//coral-tab-label[text()='Text']"));
        click(act, textTab);
        WebElement textPersonalizationButton = webDriver.findElement(By.cssSelector("[data-targetinput=\"input[name='./jcr:title']\"]"));
        assertNotNull(textPersonalizationButton);
        assertTrue(textPersonalizationButton.isDisplayed());
        WebElement descriptionTextArea = webDriver.findElement(By.xpath("//div[@name='./jcr:description']"));
        click(act, descriptionTextArea);
        WebElement descriptionPersonalizationButton =
                webDriver.findElement(By.xpath("//button[@data-action='personalizationplugin#insertvariable']"));
        assertNotNull(descriptionPersonalizationButton);
        assertTrue(descriptionPersonalizationButton.isEnabled());
    }

    @Test
    @DisplayName("Test: Check if personalization plugin buttons exist in fullscreen configure dialog (Teaser component)")
    public void testCheckConfigureDialogFullScreen() throws TimeoutException, InterruptedException {
        com.adobe.cq.email.core.components.it.seljup.util.Commons.openEditDialog(this.editorPage, this.compPath);
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        Actions act = new Actions(webDriver);
        WebElement fullscreenButton = webDriver.findElement(By.cssSelector("[title='Toggle Fullscreen']"));
        click(act, fullscreenButton);
        WebElement textTab = webDriver.findElement(By.xpath("//coral-tab-label[text()='Text']"));
        click(act, textTab);
        WebElement textPersonalizationButton = webDriver.findElement(By.cssSelector("[data-targetinput=\"input[name='./jcr:title']\"]"));
        assertNotNull(textPersonalizationButton);
        assertTrue(textPersonalizationButton.isDisplayed());
        WebElement descriptionTextArea = webDriver.findElement(By.xpath("//div[@name='./jcr:description']"));
        click(act, descriptionTextArea);
        WebElement descriptionPersonalizationButton =
                webDriver.findElement(By.xpath("//button[@data-action='personalizationplugin#insertvariable']"));
        assertNotNull(descriptionPersonalizationButton);
        assertTrue(descriptionPersonalizationButton.isEnabled());
    }

    private void click(Actions act, WebElement element) throws InterruptedException {
        act.moveToElement(element).click().perform();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

}
