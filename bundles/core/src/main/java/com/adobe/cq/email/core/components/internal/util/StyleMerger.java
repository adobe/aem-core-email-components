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
package com.adobe.cq.email.core.components.internal.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class that merges the current style of an HTML element with the one that came from the style tag
 */
public class StyleMerger {
    /**
     * "!important" selector used to identify if a CSS rule is important
     */
    public static final String IMPORTANT_RULE = "!important";
    private static final String EMPTY_TOKEN_SELECTOR = "";

    private StyleMerger() {
        // to avoid instantiation
    }

    /**
     * Merges the current {@link StyleToken} of an HTML element with another {@link StyleToken}
     *
     * @param elementStyleToken the current HTML element style
     * @param styleToken        another {@link StyleToken}
     * @return the merged {@link StyleToken} of the HTML element
     */
    public static StyleToken merge(StyleToken elementStyleToken, StyleToken styleToken) {
        if (Objects.isNull(styleToken)) {
            styleToken = StyleTokenFactory.create(EMPTY_TOKEN_SELECTOR);
        }
        if (Objects.isNull(elementStyleToken)) {
            elementStyleToken = StyleTokenFactory.create(EMPTY_TOKEN_SELECTOR);
        }
        StyleToken merged = StyleTokenFactory.create(elementStyleToken.getSelector());
        merged.setSpecificity(StyleSpecificityFactory.getSpecificity(elementStyleToken.getSelector()));
        Map<String, StyleProperty> stylePropertiesByName = new LinkedHashMap<>();
        processStyleTokens(stylePropertiesByName, elementStyleToken, styleToken);
        for (Map.Entry<String, StyleProperty> entry : stylePropertiesByName.entrySet()) {
            StyleTokenFactory.addProperties(merged, entry.getValue().getFullProperty());
        }
        return merged;
    }

    private static void processStyleTokens(Map<String, StyleProperty> stylePropertiesByName, StyleToken elementStyleToken,
                                           StyleToken styleToken) {
        processStyleToken(stylePropertiesByName, styleToken);
        processStyleToken(stylePropertiesByName, elementStyleToken);
    }

    private static void processStyleToken(Map<String, StyleProperty> styleProperties, StyleToken styleToken) {
        for (int i = 0; i < styleToken.getProperties().size(); i++) {
            String property = styleToken.getProperties().get(i);
            StyleProperty styleProperty = parse(property, styleToken.getSpecificity());
            String propertyName = styleProperty.getName();
            StyleProperty alreadyFoundStyleProperty = styleProperties.get(propertyName);
            if (Objects.isNull(alreadyFoundStyleProperty)) {
                styleProperties.put(propertyName, styleProperty);
                continue;
            }
            int specificityComparison = alreadyFoundStyleProperty.getSpecificity().compareTo(styleToken.getSpecificity());
            if (alreadyFoundStyleProperty.isImportant()) {
                if (styleProperty.isImportant() && specificityComparison < 0) {
                    styleProperties.put(propertyName, styleProperty);
                }
                continue;
            }
            if (styleProperty.isImportant()) {
                styleProperties.put(propertyName, styleProperty);
                continue;
            }
            if (specificityComparison > 0) {
                styleProperties.put(propertyName, styleProperty);
            }
        }
    }

    private static StyleProperty parse(String property, StyleSpecificity specificity) {
        String[] keyAndValue = StringUtils.split(property, ":");
        StyleProperty styleProperty = new StyleProperty();
        styleProperty.setFullProperty(property);
        styleProperty.setSpecificity(specificity);
        if (keyAndValue.length < 2) {
            return styleProperty;
        }
        styleProperty.setName(keyAndValue[0].trim());
        styleProperty.setValue(keyAndValue[1].trim());
        styleProperty.setImportant(styleProperty.getValue().contains(IMPORTANT_RULE));
        return styleProperty;
    }

}
