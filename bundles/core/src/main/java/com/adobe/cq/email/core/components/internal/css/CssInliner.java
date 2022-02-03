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
package com.adobe.cq.email.core.components.internal.css;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CssInliner {
    private static final String STYLE_ELM = "style";
    private static final String STYLE_ATTR = "style";
    private static final Logger LOG = LoggerFactory.getLogger(CssInliner.class);

    public Result process(String html) {
        try {
            Result result = new Result();
            Document document = Jsoup.parse(html);
            result.setInlineStyle(extractInlineStyle(document));
            result.setSelectorMap(generateSelectorMap(document, result.getInlineStyle()));
            result.setOutputHtml(getOutputHtml(document, result.getSelectorMap()));
            return result;
        } catch (Throwable e) {
            throw new CssInlinerException(String.format("Error processing HTML: %s", e.getMessage()), e);
        }
    }

    private String extractInlineStyle(Document document) {
        Elements elements = document.select(STYLE_ELM);
        StringBuilder stringBuilder = new StringBuilder();
        for (Element element : elements) {
            stringBuilder.append(element.getAllElements().get(0).data().replaceAll("\\s+", " ").trim());
        }
        return stringBuilder.toString();
    }

    private String getOutputHtml(Document document, Map<String, String> selectorMap) {
        for (Map.Entry<String, String> entry : selectorMap.entrySet()) {
            String selector = entry.getKey();
            Elements selectedElements = document.select(selector);
            for (Element selectedElement : selectedElements) {
                String styleAttribute = selectedElement.attr(STYLE_ATTR);
                if (StringUtils.isNotEmpty(styleAttribute)) {
                    selectedElement.attr(STYLE_ATTR, concatenateProperties(styleAttribute, selectorMap.get(selector)));
                } else {
                    selectedElement.attr(STYLE_ATTR, entry.getValue());
                }
            }
        }
        return document.outerHtml();
    }

    private HashMap<String, String> generateSelectorMap(Document document, String inlineStyle) {
        HashMap<String, String> map = new HashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(inlineStyle, "{}");
        while (tokenizer.countTokens() > 1) {
            String selector = tokenizer.nextToken().trim();
            String properties = tokenizer.nextToken().replaceAll("\"", "'").replaceAll("[ ](?=[ ])|[^-_,:;'%#!A-Za-z0-9 ]+", "").trim();
            if (StringUtils.contains(selector, ",")) {
                StringTokenizer selectorTokens = new StringTokenizer(selector, ",");
                while (selectorTokens.hasMoreTokens()) {
                    addSelectorProperties(document, selectorTokens.nextToken(), properties, map);
                }
            } else {
                addSelectorProperties(document, selector, properties, map);
            }
        }
        return map;
    }

    private void addSelectorProperties(Document document, String selector, String properties, Map<String, String> selectorMap) {
        try {
            document.select(selector);
            if (selectorMap.containsKey(selector)) {
                selectorMap.put(selector, concatenateProperties(selectorMap.get(selector), properties));
            } else {
                selectorMap.put(selector, properties);
            }
        } catch (Selector.SelectorParseException e) {
            LOG.debug(e.getMessage());
        }
    }

    private String concatenateProperties(String oldProperties, String properties) {
        oldProperties = oldProperties.trim();
        if (!StringUtils.endsWith(oldProperties, ";")) {
            oldProperties += ";";
        }
        return oldProperties + properties;
    }

    public static class Result {
        private String inlineStyle;
        private Map<String, String> selectorMap;
        private String outputHtml;

        public String getInlineStyle() {
            return inlineStyle;
        }

        public void setInlineStyle(String inlineStyle) {
            this.inlineStyle = inlineStyle;
        }

        public Map<String, String> getSelectorMap() {
            return selectorMap;
        }

        public void setSelectorMap(Map<String, String> selectorMap) {
            this.selectorMap = selectorMap;
        }

        public String getOutputHtml() {
            return outputHtml;
        }

        public void setOutputHtml(String outputHtml) {
            this.outputHtml = outputHtml;
        }
    }

    public static class CssInlinerException extends RuntimeException {

        public CssInlinerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
