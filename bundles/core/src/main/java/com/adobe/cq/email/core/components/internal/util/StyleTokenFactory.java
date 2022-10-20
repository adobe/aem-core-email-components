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

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return create(selector, Collections.emptySet());
    }

    public static StyleToken create(String selector, Set<String> skipCheck) {
        StyleToken styleToken = new StyleToken();
        styleToken.setSelector(selector);
        styleToken.setForceUsage(forceUsage(skipCheck, styleToken));
        styleToken.setMediaQuery(selector.contains("@"));
        styleToken.setPseudoSelector(!styleToken.isMediaQuery() && selector.contains(":"));
        if (StringUtils.isNotEmpty(selector)) {
            if (selector.contains(",")) {
                for (String splitted : selector.split(",")) {
                    if (styleToken.isPseudoSelector()) {
                        splitted = StringUtils.substringBefore(splitted, ":");
                    }
                    if (StringUtils.isEmpty(splitted)) {
                        continue;
                    }
                    addJsoupSelector(styleToken, splitted);
                }
            } else {
                if (styleToken.isPseudoSelector()) {
                    selector = StringUtils.substringBefore(selector, ":");
                }
                addJsoupSelector(styleToken, selector);
            }
        }
        return styleToken;
    }

    private static boolean forceUsage (Set<String> skipCheck, StyleToken styleToken) {
        return skipCheck.stream().anyMatch(w -> Optional.ofNullable(styleToken.getSelector()).orElse("").matches(w));
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
    @Nullable
    public static String toCss(StyleToken styleToken) {
        String css = null;
        String styleTokenCss  = toCss(styleToken, new StringBuilder()).toString();
        if (StringUtils.isNotEmpty(styleTokenCss)) {
            css = styleTokenCss.trim() + "\n";
        }
        return css;
    }

    private static StringBuilder toCss(StyleToken styleToken, @NotNull StringBuilder builder) {
        if (Objects.nonNull(styleToken)) {
            builder.append(styleToken.getSelector()).append(" {");
            for (StyleToken childToken: styleToken.getChildTokens()) {
                toCss(childToken, builder.append(" "));
            }
            String properties = getAllProperties(styleToken);
            if (StringUtils.isNotEmpty(properties)) {
                builder.append(" ").append(properties);
            }
            builder.append(" }");
        }
        return builder;
    }

    private static void addJsoupSelector(StyleToken styleToken, String splittedSelector) {
        if (Objects.isNull(styleToken)) {
            return;
        }
        if (StringUtils.isEmpty(splittedSelector)) {
            return;
        }
        styleToken.getJsoupSelectors().add(splittedSelector.trim());
    }
}
