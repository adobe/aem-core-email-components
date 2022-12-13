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
package com.adobe.cq.email.core.components.it.seljup.util;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pagewidgets.cq.EditableToolbar;
import com.adobe.cq.testing.selenium.pagewidgets.cq.InlineEditor;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.WebDriverRunner;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.webDriverWait;

/**
 * @todo Can be removed after {@link com.adobe.cq.wcm.core.components.it.seljup.util.Commons} has been updated.
 */
public class Commons {

    public static void openEditDialog(EditorPage editorPage, String compPath) throws TimeoutException, InterruptedException {
        String component = "[data-type='Editable'][data-path='" + compPath + "']";
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        (new WebDriverWait(webDriver, Duration.ofSeconds(20L))).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        EditableToolbar editableToolbar = editorPage.openEditableToolbar(compPath);
        editableToolbar.clickConfigure();
        webDriverWait(1000);
    }

    public static InlineEditor openInlineEditor(EditorPage editorPage, String compPath) throws TimeoutException {
        String component = "[data-type='Editable'][data-path='" + compPath + "']";
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        (new WebDriverWait(webDriver, Duration.ofSeconds(20L))).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        EditableToolbar editableToolbar = editorPage.openEditableToolbar(compPath);
        return editableToolbar.clickEdit();
    }

}
