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
package com.adobe.cq.email.core.components.internal.services;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.components.pojo.StyleSpecificity;
import com.adobe.cq.email.core.components.pojo.StyleWithSpecificity;
import com.adobe.cq.email.core.components.services.StylesInlinerService;
import com.adobe.cq.email.core.components.util.StyleUtils;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;

import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.COMMENTS_REGEX;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.HEAD_TAG;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.IMPORTANT_RULE;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.MEDIA_QUERY_REGEX;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.NEW_LINE;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.STYLE_ATTRIBUTE;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.STYLE_DELIMS;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.STYLE_TAG;

/**
 * This is a service which converts html with css classes into html with inline styles.
 */
@Component(service = StylesInlinerService.class)
@ServiceDescription("Styles Inliner Service")
public class StylesInlinerServiceImpl implements StylesInlinerService {

    @Reference
    private RequestResponseFactory requestResponseFactory;

    @Reference
    private SlingRequestProcessor requestProcessor;

    private static final Logger LOG = LoggerFactory.getLogger(StylesInlinerServiceImpl.class.getName());

    private static final StyleSpecificity STYLE_SPECIFICITY = new StyleSpecificity(1, 0, 0, 0);

    /**
     * This method accepts the html string as the input, parses the same using Jsoup, reads the style rules
     * and adds it to the respective elements in the html. The media query styles will be retained, as these
     * cannot be inlined. The pseudo classes are ignored, as these cannot be inlined.
     *
     * @param resourceResolver      the resource resolver object
     * @param html                  the html string
     * @param hasExternalStyleSheet specifies whether styles are defined in external style sheet.
     * @return html with inline styles
     */
    public String getHtmlWithInlineStyles(ResourceResolver resourceResolver, String html, boolean hasExternalStyleSheet) {

        Document doc = Jsoup.parse(html);
        doc.outputSettings().prettyPrint(false);

        List<String> styles = StyleUtils.getStyles(doc, hasExternalStyleSheet, requestResponseFactory, requestProcessor, resourceResolver);

        LinkedHashMap<String, LinkedHashMap<String, StyleWithSpecificity>> stylesToBeApplied = new LinkedHashMap<>();

        for (String allRules : styles) {
            String rules = allRules
                    .replaceAll(NEW_LINE, "") // remove newlines
                    .replaceAll(COMMENTS_REGEX, "") // remove comments
                    .replaceAll(MEDIA_QUERY_REGEX, "") // remove media queries
                    .trim();
            StringTokenizer styleTokens = new StringTokenizer(rules, STYLE_DELIMS);
            while (styleTokens.countTokens() > 1) {
                String cssSelector = styleTokens.nextToken().trim();
                String properties = styleTokens.nextToken().trim();
                String[] cssSelectors = cssSelector.split(",");
                populateStylesToBeApplied(cssSelectors, properties, doc, stylesToBeApplied);
            }
            // retain media query style rules in style tag
            Element style = new Element(STYLE_TAG);
            doc.select(HEAD_TAG).get(0).appendChild(style);
            Matcher m = Pattern.compile(MEDIA_QUERY_REGEX)
                    .matcher(allRules);
            StringBuilder sb = new StringBuilder();
            while (m.find()) {
                String matchedValue = m.group();
                sb.append(matchedValue);
                sb.append("\n\t\t");
            }
            style.html("\n\t\t" + sb);

        }

        applyStyles(doc, stylesToBeApplied);

        return doc.outerHtml();
    }

    /**
     * This method populates the styles to be applied for each element based on the style rules and css specificity
     *
     * @param cssSelectors      the css selector
     * @param properties        the style properties
     * @param doc               the jsoup document which holds the html
     * @param stylesToBeApplied the styles to be applied
     */
    private void populateStylesToBeApplied(String[] cssSelectors, String properties, Document doc,
                                           LinkedHashMap<String, LinkedHashMap<String, StyleWithSpecificity>> stylesToBeApplied) {
        for (String cssSelector : cssSelectors) {
            String trimmedSel = cssSelector.trim();
            //Pseudo Selectors and Key frames not supported
            if (trimmedSel.contains(":") || trimmedSel.startsWith("@keyframes")) {
                continue;
            }
            try {
                Elements selectedElements = doc.select(trimmedSel);
                for (Element selectedElement : selectedElements) {
                    if (selectedElement.tagName().equals(STYLE_TAG)) {
                        LOG.error("Style tag selected by {}", trimmedSel);
                        continue;
                    }
                    LinkedHashMap<String, StyleWithSpecificity> existingStyles;
                    String uniqueElementSelector = selectedElement.cssSelector();
                    if (!stylesToBeApplied.containsKey(uniqueElementSelector)) {
                        existingStyles = getStyleAttributes(STYLE_SPECIFICITY, selectedElement.attr(STYLE_ATTRIBUTE));
                    } else {
                        existingStyles = stylesToBeApplied.get(uniqueElementSelector);
                    }

                    stylesToBeApplied.put(uniqueElementSelector,
                            mergeStyle(
                                    existingStyles,
                                    getStyleAttributes(StyleUtils.getSpecificity(trimmedSel), properties)
                            )
                    );
                }
            } catch (IllegalArgumentException | Selector.SelectorParseException e) {
                LOG.error("An error while selecting", e);
            }

        }
    }

    /**
     * This method gets the map of element selector : style attributes and applies the style attributes
     * to the element
     *
     * @param document          the jsoup document which holds the html
     * @param stylesToBeApplied the styles to be applied for all the elements
     */
    private void applyStyles(Document document, LinkedHashMap<String, LinkedHashMap<String, StyleWithSpecificity>> stylesToBeApplied) {
        for (Map.Entry<String, LinkedHashMap<String, StyleWithSpecificity>> entry : stylesToBeApplied.entrySet()) {
            String elementSelector = entry.getKey();
            Element elementToApply = document.select(elementSelector).first();
            if (null == elementToApply) {
                LOG.error("Failed to find {}", elementSelector);
                continue;
            }
            LinkedHashMap<String, StyleWithSpecificity> styles = entry.getValue();
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, StyleWithSpecificity> propertyWithStyle : styles.entrySet()) {
                sb.append(propertyWithStyle.getKey()).append(":").append(styles.get(propertyWithStyle.getKey()).getValue()).append(";");
            }
            elementToApply.attr(STYLE_ATTRIBUTE, sb.toString());
        }
    }

    /**
     * This method gets the style attributes, calculates the css specificity based on the css selector
     * and creates a map of style attribute name : specificity for that attribute
     *
     * @param priority   the current style specificity
     * @param properties the style rules
     * @return the map of style attribute name : specificity for that attribute
     */
    private LinkedHashMap<String, StyleWithSpecificity> getStyleAttributes(StyleSpecificity priority, String properties) {
        LinkedHashMap<String, StyleWithSpecificity> styleAttributesMap = new LinkedHashMap<>();
        if (null == properties || properties.trim().length() == 0) {
            return styleAttributesMap;
        }
        String[] styleProperties = properties.split(";");
        for (String styleProperty : styleProperties) {
            String[] stylePropertyTokens = styleProperty.split(":");
            if (stylePropertyTokens.length != 2) {
                continue;
            }
            String propertyName = stylePropertyTokens[0].trim();
            String propertyValue = stylePropertyTokens[1].trim();
            StyleWithSpecificity styleWithSpecificity = new StyleWithSpecificity();
            styleWithSpecificity.setPriority(priority);
            styleWithSpecificity.setValue(propertyValue);
            styleAttributesMap.put(propertyName, styleWithSpecificity);
        }
        return styleAttributesMap;
    }

    /**
     * This method merges the existing styles with the styles from the current css selector.
     *
     * @param oldProps the existing style properties
     * @param newProps the style properties from the current css selector
     * @return merged style properties based on css specificity
     */
    private LinkedHashMap<String, StyleWithSpecificity> mergeStyle(LinkedHashMap<String, StyleWithSpecificity> oldProps,
                                                                   LinkedHashMap<String, StyleWithSpecificity> newProps) {
        Set<String> allProps = new LinkedHashSet<>();
        for (Map.Entry<String, StyleWithSpecificity> prop : oldProps.entrySet()) {
            if (!newProps.containsKey(prop.getKey())) {
                allProps.add(prop.getKey());
            }
        }
        allProps.addAll(newProps.keySet());

        return getFinalProps(oldProps, newProps, allProps);
    }

    /**
     * This method takes the existing style properties, and style properties from current css selector,
     * calculates the css specificity and provides the final set of properties to be applied
     *
     * @param oldProps the existing style properties for the element
     * @param newProps the style properties of current selector
     * @param allProps all the properties
     * @return the final set of properties
     */
    private LinkedHashMap<String, StyleWithSpecificity> getFinalProps(LinkedHashMap<String, StyleWithSpecificity> oldProps,
                                                                      LinkedHashMap<String, StyleWithSpecificity> newProps,
                                                                      Set<String> allProps) {
        LinkedHashMap<String, StyleWithSpecificity> finalProps = new LinkedHashMap<>();
        for (String property : allProps) {
            StyleWithSpecificity oldValue = oldProps.get(property);
            StyleWithSpecificity newValue = newProps.get(property);
            if (null == oldValue && null == newValue) {
                continue;
            }
            if (null == oldValue) {
                finalProps.put(property, newValue);
                continue;
            }
            if (null == newValue) {
                finalProps.put(property, oldValue);
                continue;
            }
            if (oldValue.getValue().contains(IMPORTANT_RULE)) {
                finalProps.put(property, oldValue);
                continue;
            }
            if (newValue.getValue().contains(IMPORTANT_RULE)) {
                finalProps.put(property, newValue);
                continue;
            }
            int compare = oldValue.getSpecificity().compareTo(newValue.getSpecificity());
            if (compare > 0) {
                finalProps.put(property, oldValue);
            } else {
                finalProps.put(property, newValue);
            }
        }
        return finalProps;
    }
}
