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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.components.pojo.HtmlInlinerConfiguration;
import com.adobe.cq.email.core.components.pojo.StyleToken;

/**
 * Utility class that creates HTML attributes based on {@link HtmlInlinerConfiguration}
 */
public class HtmlAttributeInliner {
    private static final Logger LOG = LoggerFactory.getLogger(HtmlAttributeInliner.class.getName());


    /**
     * Processes the {@link Element} and the related {@link StyleToken} and, if there are CSS selectors that are matched by the current
     * {@link HtmlInlinerConfiguration}, it will update the {@link Element} with the specific attribute
     *
     * @param element                   the {@link Element}
     * @param styleToken                the related {@link StyleToken}
     * @param htmlInlinerConfigurations the {@link HtmlInlinerConfiguration}s for the current {@link Element}
     */
    public static void process(Element element, StyleToken styleToken, List<HtmlInlinerConfiguration> htmlInlinerConfigurations) {
        if (Objects.isNull(element) || Objects.isNull(styleToken) || styleToken.getProperties().isEmpty() ||
                Objects.isNull(htmlInlinerConfigurations) || htmlInlinerConfigurations.isEmpty()) {
            return;
        }
        for (HtmlInlinerConfiguration htmlInlinerConfiguration : htmlInlinerConfigurations) {
            try {
                if (!htmlInlinerConfiguration.isValid()) {
                    continue;
                }
                Pattern pattern = Pattern.compile(htmlInlinerConfiguration.getCssPropertyOutputRegEx());
                List<Pair<String, String>> cssProperties =
                        styleToken.getProperties().stream().map(HtmlAttributeInliner::convert).filter(Objects::nonNull)
                                .collect(Collectors.toList());
                Map.Entry<String, String> cssProperty =
                        cssProperties.stream().filter(p -> Objects.nonNull(p) && Objects.nonNull(p.getKey()) &&
                                        p.getKey().matches(htmlInlinerConfiguration.getCssPropertyRegEx()))
                                .findFirst().orElse(null);
                if (Objects.isNull(cssProperty)) {
                    return;
                }
                Matcher matcher = pattern.matcher(cssProperty.getValue());
                String value = null;
                if (matcher.find()) {
                    value = matcher.group();
                }
                if (Objects.isNull(value)) {
                    return;
                }
                String attr = element.attr(htmlInlinerConfiguration.getHtmlAttributeName());
                if (StringUtils.isEmpty(attr) || htmlInlinerConfiguration.isOverrideIfAlreadyExisting()) {
                    element.attr(htmlInlinerConfiguration.getHtmlAttributeName(), value);
                }
            } catch (Throwable e) {
                LOG.warn("Error processing HTML element " + element + " for StyleToken " + styleToken + " and HtmlInlinerConfiguration " +
                        htmlInlinerConfiguration + ": " + e.getMessage(), e);
            }
        }
    }

    private static Pair<String, String> convert(String cssProperty) {
        if (StringUtils.isEmpty(cssProperty)) {
            return null;
        }
        String[] propertyAndValue = cssProperty.split(":");
        if (propertyAndValue.length != 2) {
            return null;
        }
        return Pair.of(propertyAndValue[0].trim(), propertyAndValue[1].trim());
    }

}
