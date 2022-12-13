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

package com.adobe.cq.email.core.components.it.seljup.tests.text;

import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pagewidgets.cq.InlineEditor;
import com.adobe.cq.testing.selenium.pagewidgets.cq.RichTextToolbar;
import com.adobe.cq.testing.selenium.utils.ElementUtils;
import com.adobe.cq.email.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.text.TextEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TextIT extends AuthorBaseUITest {

    private final String textRT = "core/email/components/text/v1/text";
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
        proxyPath = Commons.createProxyComponent(adminClient, textRT, Commons.proxyPath, null, null);
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
    @DisplayName("Test: Check if personalization plugin button exists in inline editor (Text component)")
    public void testCheckInlineEditor() throws TimeoutException {
        InlineEditor inlineEditor = com.adobe.cq.email.core.components.it.seljup.util.Commons.openInlineEditor(this.editorPage, this.compPath);
        RichTextToolbar rte = inlineEditor.getRichTextToolbar();
        SelenideElement personalizationPluginBtn = get(rte.element(), "personalizationplugin#insertvariable");
        assertTrue(personalizationPluginBtn.exists());
        assertTrue(personalizationPluginBtn.isDisplayed());
    }

    @Test
    @DisplayName("Test: Check if personalization plugin button exists in fullscreen editor (Text component)")
    public void testCheckFullScreenEditor() throws TimeoutException, InterruptedException {
        InlineEditor inlineEditor = com.adobe.cq.email.core.components.it.seljup.util.Commons.openInlineEditor(this.editorPage,
                this.compPath);
        RichTextToolbar rte = inlineEditor.getRichTextToolbar();
        SelenideElement fullscreenBtn = get(rte.element(), "fullscreen#start");
        ElementUtils.clickableClick(fullscreenBtn);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        SelenideElement personalizationPluginBtn = get(rte.element(), "personalizationplugin#insertvariable");
        assertTrue(personalizationPluginBtn.exists());
        assertTrue(personalizationPluginBtn.isDisplayed());
    }

    @Test
    @DisplayName("Test: Check if personalization plugin button exists in configure dialog (Text component)")
    public void testCheckConfigureDialog() throws TimeoutException, InterruptedException {
        com.adobe.cq.email.core.components.it.seljup.util.Commons.openEditDialog(this.editorPage, this.compPath);
        Selenide.$("[data-editor-type=\"text\"]").click();
        TextEditDialog editDialog = new TextEditDialog();
        SelenideElement personalizationPluginBtn = get(editDialog.element(), "personalizationplugin#insertvariable");
        assertTrue(personalizationPluginBtn.exists());
        assertTrue(personalizationPluginBtn.isDisplayed());
    }

    @Test
    @DisplayName("Test: Check if personalization plugin button exists in fullscreen configure dialog (Text component)")
    public void testCheckConfigureDialogFullScreen() throws TimeoutException, InterruptedException {
        com.adobe.cq.email.core.components.it.seljup.util.Commons.openEditDialog(this.editorPage, this.compPath);
        Selenide.$("[data-editor-type=\"text\"]").click();
        Selenide.$("[icon=\"fullScreen\"]").click();
        TextEditDialog editDialog = new TextEditDialog();
        SelenideElement personalizationPluginBtn = get(editDialog.element(), "personalizationplugin#insertvariable");
        assertTrue(personalizationPluginBtn.exists());
        assertFalse(personalizationPluginBtn.isDisplayed());
    }

    private SelenideElement get(SelenideElement element, String dataAction) {
        return element.find("[data-action='" + dataAction + "']");
    }
}
