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
package com.adobe.cq.email.core.components.util;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.email.core.components.pojo.StyleSpecificity;

public class StyleSpecificityFactory {

    private StyleSpecificityFactory() {
        // to avoid instantiation
    }

    /**
     * This method calculates the css specificity of the given css selector
     *
     * @param selector the css selector
     * @return the specificity
     */
    public static StyleSpecificity getSpecificity(String selector) {
        selector = selector.trim();
        int id = 0;
        int classAttribute = 0;
        int elements = 0;
        String[] selectorTokens = selector.split(" ");
        for (String selectorToken : selectorTokens) {
            if (isSelectorEmpty(selectorToken)) {
                continue;
            }
            selectorToken = selectorToken.trim();
            if (isIdPresentInSelector(selectorToken)) {
                int increment = StringUtils.countMatches(selectorToken, "#");
                id = id + increment;
                elements = incrementElements(selectorToken, "#", elements);
                continue;
            }
            if (isClassSelectorPresentInSelector(selectorToken)) {
                int increment = 1;
                if (selectorToken.contains(".")) {
                    increment = StringUtils.countMatches(selectorToken, ".");
                }
                classAttribute = classAttribute + increment;
                elements = incrementElements(selectorToken, ".", elements);
                continue;
            }
            elements++;
        }
        return new StyleSpecificity(0, id, classAttribute, elements);
    }

    /**
     * This method increments count of element references in the selector
     *
     * @param selectorToken the selector
     * @param selectorType  the selector type
     * @param elements      the current elements count
     * @return the updated elements count
     */
    private static int incrementElements(String selectorToken, String selectorType, int elements) {
        if (!selectorToken.startsWith(selectorType) && selectorToken.contains(selectorType)) {
            elements++;
        }
        return elements;
    }

    /**
     * This method checks if selector is empty
     *
     * @param selectorToken the selector
     * @return true if selector is null or empty
     */
    private static boolean isSelectorEmpty(String selectorToken) {
        return null == selectorToken || selectorToken.trim().length() == 0;
    }

    /**
     * This method checks if css class is present in selector
     *
     * @param selectorToken the selector
     * @return true if css class is present
     */
    private static boolean isClassSelectorPresentInSelector(String selectorToken) {
        return selectorToken.contains("[") || selectorToken.startsWith(".") || selectorToken.contains(".") ||
                (selectorToken.contains(":") && (!selectorToken.contains("::")));
    }

    /**
     * This method returns if Id is present in selector
     *
     * @param selectorToken the selector
     * @return true if Id is present
     */
    private static boolean isIdPresentInSelector(String selectorToken) {
        return selectorToken.startsWith("#") || selectorToken.contains("#");
    }
}
