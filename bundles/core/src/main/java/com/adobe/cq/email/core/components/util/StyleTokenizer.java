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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.email.core.components.constants.StylesInlinerConstants;
import com.adobe.cq.email.core.components.pojo.StyleToken;

/**
 * Utility class that tokenize a CSS stylesheet
 */
public class StyleTokenizer {

    private static final String REGEX = "((?<=[{}])|(?=[{}]))";

    private StyleTokenizer() {
        // To avoid instantiation
    }

    /**
     * Process a CSS stylesheet and tokenizes it
     *
     * @param css the CSS stylesheet
     * @return a {@link List} of {@link StyleToken}
     */
    public static @NotNull List<StyleToken> tokenize(String css, Set<String> skipCheck) {
        List<StyleToken> result = new ArrayList<>();
        if (StringUtils.isEmpty(css)) {
            return result;
        }
        String[] strings = css.replaceAll("\\s+", " ").replaceAll("/\\*[^*]*\\*+([^/*][^*]*\\*+)*/", "").trim().split(REGEX);
        StyleToken current = null;
        StyleToken parent = null;
        int nestingLevel = 0;
        int index = 0;
        for (String string : strings) {
            index++;
            String item = string.trim();

            if (StringUtils.isEmpty(item)) {
                continue;
            }

            if (StringUtils.equals(item, "{")) {
                nestingLevel++;
                continue;
            } else if (StringUtils.equals(item, "}")) {
                nestingLevel--;
                if (nestingLevel == 0 && Objects.nonNull(current)) {
                    current.setSpecificity(StyleSpecificityFactory.getSpecificity(current.getSelector()));
                    if (Objects.nonNull(parent)) {
                        current = parent;
                        parent = null;
                    }
                    result.add(current);
                } else if (nestingLevel > 0 && Objects.nonNull(parent)) {
                    parent.getChildTokens().add(current);
                }
                continue;
            }

            if (nestingLevel == 0) {
                current = StyleTokenFactory.create(item);
                if (skipCheck.contains(current.getSelector())) {
                    current.setForceUsage(true);
                }
                current.setMediaQuery(current.getSelector().contains("@"));
                current.setPseudoSelector(!current.isMediaQuery() && item.contains(":"));
                continue;
            }

            if (nestingLevel > 0) {
                boolean isInnerPseudo = false;
                String next = strings[index].trim();
                if (current.isMediaQuery() && item.contains(":") && StringUtils.equals(next, "{")) {
                    isInnerPseudo = true;
                }
                if ((item.contains(";") || item.contains(":")) && !isInnerPseudo) {
                    StyleTokenFactory.addProperties(current, item);
                } else {
                    if (Objects.isNull(parent)) {
                        parent = current;
                    }
                    current = StyleTokenFactory.create(item);
                    current.setPseudoSelector(isInnerPseudo);
                    if (skipCheck.contains(current.getSelector())) {
                        current.setForceUsage(true);
                    }
                }
            }
        }
        return result;
    }
}
