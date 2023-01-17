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
package com.adobe.cq.email.core.components.it.seljup.util.components.segmentation.v1;

import com.adobe.cq.email.core.components.it.seljup.util.components.segmentation.SegmentationEditDialog;
import com.adobe.cq.testing.selenium.pagewidgets.Helpers;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.tabs.v1.Tabs;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.Selectors;

import static com.codeborne.selenide.Selenide.$;

public class Segmentation extends Tabs {

    public Segmentation() {
        super();
    }

    public SegmentationEditDialog openEditDialog(String dataPath) {
        Commons.openEditableToolbar(dataPath);
        $(Selectors.SELECTOR_CONFIG_BUTTON).click();
        Helpers.waitForElementAnimationFinished($(Selectors.SELECTOR_CONFIG_DIALOG));
        return new SegmentationEditDialog();
    }
}
