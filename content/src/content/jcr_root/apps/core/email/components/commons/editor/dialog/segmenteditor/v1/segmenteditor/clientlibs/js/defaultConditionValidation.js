/*******************************************************************************
 * Copyright 2022 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
(function($, window) {
    "use strict";

    var DEFAULT_VALUE = "default";
    var SELECTORS = {
        conditions: ".cmp-segmenteditor__item-condition"
    };

    /* Adapting window object to foundation-registry */
    var registry = $(window).adaptTo("foundation-registry");

    /* Validator for condition DropDown: Only one default condition is allowed */
    registry.register("foundation.validation.validator", {
        selector: "[data-validation=default-condition]",
        validate: function(el) {
            var $el = $(el);
            var elValue = $el.val();
            var defaultConditions = getDefaultConditions($el);

            if (elValue === DEFAULT_VALUE) {
                if (defaultConditions.length > 1) {
                    return "Only one default segment is allowed in a segmentation.";
                }
            } else {
                defaultConditions.forEach(function(item) {
                    handleValidation($(item));
                });
            }
        }
    });

    function getDefaultConditions($el) {
        var defaultConditions = [];
        var $form = $el.closest("form");
        var $conditionsEl = $(SELECTORS.conditions, $form);
        $conditionsEl.each(function(i, item) {
            if ($(item).val() === DEFAULT_VALUE) {
                defaultConditions.push(item);
            }
        });
        return defaultConditions;
    }

    function handleValidation(el) {
        var api = el.adaptTo("foundation-validation");
        if (api) {
            api.checkValidity();
            api.updateUI();
        }
    }
})($, window);
