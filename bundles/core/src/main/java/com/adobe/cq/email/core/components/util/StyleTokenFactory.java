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

public class StyleTokenFactory {

    private StyleTokenFactory() {
        // to avoid instantiation
    }

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

    public static String getInlinableProperties(StyleToken styleToken) {
        String allProperties = getAllProperties(styleToken);
        if (StringUtils.isEmpty(allProperties)) {
            return allProperties;
        }
        return allProperties.replaceAll("\"", "'");
    }

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
        styleToken.getSplittedSelectors().add(splittedSelector.trim());
    }
}
