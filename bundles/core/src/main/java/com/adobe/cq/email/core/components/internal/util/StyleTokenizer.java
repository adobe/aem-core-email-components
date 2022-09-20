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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class that tokenize a CSS stylesheet
 */
public class StyleTokenizer {

    /**
     * Style delimiters used when parsing HTML page
     */
    public static final String STYLE_DELIMS = "{}";

    private StyleTokenizer() {
        // To avoid instantiation
    }

    /**
     * Process a CSS stylesheet and tokenizes it
     *
     * @param css the CSS stylesheet
     * @return a {@link List} of {@link StyleToken}
     */
    public static @NotNull List<StyleToken> tokenize(String css) {
        List<StyleToken> result = new ArrayList<>();
        if (StringUtils.isEmpty(css)) {
            return result;
        }
        StringTokenizer tokenizer =
                new StringTokenizer(css.replaceAll("\\s+", " ").replaceAll("/\\*[^*]*\\*+([^/*][^*]*\\*+)*/", "").trim(),
                        STYLE_DELIMS);
        StyleToken current = null;
        int nestingLevel = 0;
        StringBuilder nestedPropertiesBuilder = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            String next = tokenizer.nextToken().trim();
            if (StringUtils.isEmpty(next)) {
                continue;
            }
            if (Objects.isNull(current)) {
                current = StyleTokenFactory.create(next);
                current.setMediaQuery(current.getSelector().contains("@"));
                current.setPseudoSelector(!current.isMediaQuery() && next.contains(":"));
            } else {
                if (next.contains(";") || next.contains(":")) {
                    if (nestingLevel > 0) {
                        nestedPropertiesBuilder.append(next);
                        if (!nestedPropertiesBuilder.toString().endsWith(";")) {
                            nestedPropertiesBuilder.append(";");
                        }
                        nestedPropertiesBuilder.append(" } ");
                        nestingLevel--;
                    } else {
                        StyleTokenFactory.addProperties(current, next);
                    }
                    if (nestingLevel == 0) {
                        if (nestedPropertiesBuilder.length() > 0) {
                            StyleTokenFactory.addProperties(current, nestedPropertiesBuilder.toString());
                        }
                        current.setSpecificity(StyleSpecificityFactory.getSpecificity(current.getSelector()));
                        result.add(current);
                        current = null;
                        nestedPropertiesBuilder = new StringBuilder();
                    }
                } else {
                    nestedPropertiesBuilder.append(next).append(" { ");
                    current.setNested(true);
                    nestingLevel++;
                }
            }
        }
        return result;
    }

}
