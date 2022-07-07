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

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.email.core.components.pojo.StyleToken;

/**
 * {@link StyleToken} factory class
 */
public class StyleTokenFactory {

    private StyleTokenFactory() {
        // to avoid instantiation
    }

    /**
     * Creates a {@link StyleToken} from the CSS selector
     *
     * @param selector the CSS selector
     * @return the {@link StyleToken}
     */
    public static StyleToken create(String selector) {
        StyleToken styleToken = new StyleToken();
        styleToken.setSelector(selector);
        if (StringUtils.isNotEmpty(selector) && selector.contains(",")) {
            for (String splitted : selector.split(",")) {
                if (StringUtils.isEmpty(splitted)) {
                    continue;
                }
                addSplittedSelector(styleToken, splitted);
            }
        } else {
            addSplittedSelector(styleToken, selector);
        }
        return styleToken;
    }

    /**
     * Extracts all the CSS properties from the {@link StyleToken}
     *
     * @param styleToken the {@link StyleToken}
     * @return the CSS properties
     */
    public static String getAllProperties(StyleToken styleToken) {
        if (Objects.isNull(styleToken)) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String property : styleToken.getProperties()) {
            stringBuilder.append(property);
            if (!stringBuilder.toString().trim().endsWith("}")) {
                stringBuilder.append("; ");
            }
        }
        return stringBuilder.toString().trim();
    }

    /**
     * Extracts the inlinable CSS properties from the {@link StyleToken}
     *
     * @param styleToken the {@link StyleToken}
     * @return the inlinable CSS properties
     */
    public static String getInlinableProperties(StyleToken styleToken) {
        String allProperties = getAllProperties(styleToken);
        if (StringUtils.isEmpty(allProperties)) {
            return allProperties;
        }
        return allProperties.replaceAll("\"", "'");
    }

    /**
     * Extracts the inlinable CSS properties from the {@link StyleToken} if there is no nesting: otherwise, it will return null
     *
     * @param styleToken the {@link StyleToken}
     * @return the inlinable CSS properties
     */
    public static String getInlinablePropertiesIgnoringNesting(StyleToken styleToken) {
        String inlinableProperties = getInlinableProperties(styleToken);
        if (StringUtils.isEmpty(inlinableProperties)) {
            return null;
        }
        if (inlinableProperties.contains("{") || inlinableProperties.contains("}")) {
            return null;
        }
        return inlinableProperties;
    }

    /**
     * Add one or more (semi-colon separated) CSS properties to the {@link StyleToken}
     *
     * @param styleToken the {@link StyleToken}
     * @param properties the CSS properties
     */
    public static void addProperties(StyleToken styleToken, String properties) {
        if (Objects.isNull(styleToken)) {
            return;
        }
        if (StringUtils.isEmpty(properties)) {
            return;
        }
        for (String splitted : properties.split(";")) {
            String formatted = splitted.replaceAll("\\s+", " ").trim();
            if (formatted.isEmpty()) {
                continue;
            }
            if (!styleToken.getProperties().contains(formatted)) {
                styleToken.getProperties().add(formatted);
            }
        }
    }

    /**
     * Converts the {@link StyleToken} to a CSS valid representation
     *
     * @param styleToken the {@link StyleToken}
     * @return a valid CSS representation
     */
    public static String toCss(StyleToken styleToken) {
        if (Objects.isNull(styleToken)) {
            return null;
        }
        return styleToken.getSelector() + " {\n" + getAllProperties(styleToken) + "\n}\n";
    }

    private static void addSplittedSelector(StyleToken styleToken, String splittedSelector) {
        if (Objects.isNull(styleToken)) {
            return;
        }
        if (StringUtils.isEmpty(splittedSelector)) {
            return;
        }
        styleToken.getSplitSelectors().add(splittedSelector.trim());
    }
}
