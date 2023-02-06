/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
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
package com.adobe.cq.email.core.components.it.seljup.tests.page;

import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.adobe.cq.email.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.email.core.components.it.seljup.util.components.page.EmailTab;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pageobject.cq.sites.PropertiesPage;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.WebDriverRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PageIT extends AuthorBaseUITest {

    private String testPage;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        testPage = authorClient.createPage("test", "Test Page Title", rootPage,
                "/conf/core-email-components-examples/settings/wcm" + "/templates/email-template").getSlingPath();
    }

    @Test
    void testEmailPageProperties() throws InterruptedException {
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        EmailTab emailTab = propertiesPage.clickTab("email", EmailTab.class);
        emailTab.preHeader().setValue("My PreHeader");
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        PageEditorPage editorPage = new PageEditorPage(testPage);
        editorPage.open();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.xpath("//body/div[1]"));
        assertEquals("My PreHeader", element.getAttribute("innerText"));
        assertFalse(element.isDisplayed());
        propertiesPage.open();
        emailTab = propertiesPage.clickTab("email", EmailTab.class);
        emailTab.preHeader().setValue("");
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        editorPage.open();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        element = webDriver.findElement(By.xpath("//body/div[1]"));
        assertTrue(element.isDisplayed());
    }
}
