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
package com.adobe.cq.email.core.components.it.seljup.util.commons;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.commons.ChildrenEditor;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import static com.codeborne.selenide.Selenide.$;

public class SegmentationEditor extends ChildrenEditor {

    private static String addButton = "[data-cmp-hook-segmenteditor='add']";
    private static String selectItemCondition = "coral-select[data-cmp-hook-segmenteditor='itemCondition']";

    public void clickAddButton() {
        $(addButton).click();
    }

    public ElementsCollection getSelectConditions() {
        return Selenide.$$(selectItemCondition).filter(Condition.visible);
    }

    public void setCustomCondition(SelenideElement element, String segmentName, String segmentValue) throws InterruptedException {
        String name = element.getAttribute("name");
        Commons.useDialogSelect(name, "custom");
        String segmentFieldName = StringUtils.substringBefore(name, "condition") + "cq:panelTitle";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        SelenideElement segmentNameElement =
                $((WebElement) webDriver.findElement(By.cssSelector("input[type='text'][name='" + segmentFieldName + "']")));
        segmentNameElement.setValue(segmentName);
        String segmentFieldCondition = StringUtils.substringBefore(name, "condition") + "customSegmentCondition";
        SelenideElement segmentCustomConditionElement =
                $((WebElement) webDriver.findElement(By.cssSelector("input[type='text'][name='" + segmentFieldCondition + "']")));
        segmentCustomConditionElement.setValue(segmentValue);
    }

    public void setDefaultCondition(SelenideElement element) throws InterruptedException {
        String name = element.getAttribute("name");
        Commons.useDialogSelect(name, "default");
    }
}
