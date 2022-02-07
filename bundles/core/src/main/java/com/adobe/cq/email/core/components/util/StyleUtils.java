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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.components.pojo.StyleSpecificity;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;

import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.HREF_ATTRIBUTE;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.LINK_TAG;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.REL_ATTRIBUTE;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.STYLESHEET_ATTRIBUTE;

/**
 * This is a utility for the styles inliner service.
 */
public class StyleUtils {

    private StyleUtils() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(StyleUtils.class.getName());

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

    /**
     * This method gets all the style rules from either the embedded styles or external style sheet based on the
     * hasExternalStyleSheet flag.
     *
     * @param doc                    the jsoup document which holds the html
     * @param hasExternalStyleSheet  true if the styles are defined in external style sheet.
     * @param requestResponseFactory the request response factory
     * @param requestProcessor       the sling request processor
     * @param resourceResolver       the resource resolver object
     * @return all style rules
     */
    public static List<String> getStyles(Document doc, boolean hasExternalStyleSheet, RequestResponseFactory requestResponseFactory,
                                         SlingRequestProcessor requestProcessor, ResourceResolver resourceResolver) {
        List<String> stylesList = new ArrayList<>();
        if (hasExternalStyleSheet) {
            Elements linkTags = doc.select(LINK_TAG);
            for (Element linkTag : linkTags) {
                String relAttribute = linkTag.attr(REL_ATTRIBUTE);
                if (StringUtils.isNotBlank(relAttribute) && relAttribute.equals(STYLESHEET_ATTRIBUTE) &&
                        StringUtils.isNotEmpty(linkTag.attr(HREF_ATTRIBUTE))) {
                    Map<String, Object> params = new HashMap<>();
                    HttpServletRequest req = requestResponseFactory.createRequest("GET", linkTag.attr(HREF_ATTRIBUTE),
                            params);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    HttpServletResponse response = requestResponseFactory.createResponse(out);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    try {
                        requestProcessor.processRequest(req, response, resourceResolver);
                        String styles = out.toString(StandardCharsets.UTF_8.name());
                        stylesList.add(styles);
                    } catch (ServletException | IOException e) {
                        LOG.error("An error occurred while getting stylesheet", e);
                    }
                    linkTag.remove();
                }

            }
        } else {
            Elements styleTags = doc.select("style");
            for (Element styleTag : styleTags) {
                String styles = styleTag.getAllElements().get(0).data();
                stylesList.add(styles);
                styleTag.remove();
            }
        }
        return stylesList;
    }
}
