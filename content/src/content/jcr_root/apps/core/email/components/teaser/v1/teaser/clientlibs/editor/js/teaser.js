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
(function($) {
    "use strict";

    var selectors = {
        "cta": {
            "self": "[data-granite-coral-multifield-name='./actions']",
            "item": {
                "self": "coral-multifield-item",
                "link": "[data-cmp-teaser-v1-dialog-edit-hook='link'] input",
                "campaign": "[data-cmp-teaser-v1-dialog-edit-hook='adobeCampaign']"
            }
        }
    };

    var bindAcMetadataPlugin = function(input, button) {
        if ($(button).data("aCMetadataPicker") === undefined) {
            new CUI.ACMetadataPicker({
                element: button,
                targetinput: input
            });
        }
    };

    var initAcMetadataPlugin = function(multifield) {
        var items = Array.from(multifield.querySelectorAll(selectors.cta.item.self));
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            var input = item.querySelector(selectors.cta.item.link);
            var campaignButton = item.querySelector(selectors.cta.item.campaign);
            bindAcMetadataPlugin(input, campaignButton);
        }
    };

    $(document).on("foundation-contentloaded", function(event) {
        Coral.commons.nextFrame(function() {
            var actionMultifields = Array.from(event.target.querySelectorAll(selectors.cta.self));
            for (var i = 0; i < actionMultifields.length; i++) {
                initAcMetadataPlugin(actionMultifields[i]);
            }
        });
    });

    $(document).on("coral-collection:add coral-collection:change coral-multifield:itemorder", selectors.cta.self, function(event) {
        Coral.commons.nextFrame(function() {
            initAcMetadataPlugin(this);
        }.bind(this));
    });

})(jQuery);
