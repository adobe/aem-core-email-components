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
import java.util.Objects;
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

import com.day.cq.contentsync.handler.util.RequestResponseFactory;

import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.HREF_ATTRIBUTE;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.LINK_TAG;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.REL_ATTRIBUTE;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.STYLESHEET_ATTRIBUTE;

/**
 * This is a utility for the styles inliner service.
 */
public class StyleExtractor {

    private StyleExtractor() {
        // to avoid instantiation
    }

    private static final Logger LOG = LoggerFactory.getLogger(StyleExtractor.class.getName());

    /**
     * This method extracts all the style rules from both the embedded styles and external style sheet
     *
     * @param doc                    the jsoup document which holds the html
     * @param requestResponseFactory the request response factory
     * @param requestProcessor       the sling request processor
     * @param resourceResolver       the resource resolver object
     * @return all style rules
     */
    public static List<String> extract(Document doc, RequestResponseFactory requestResponseFactory,
                                       SlingRequestProcessor requestProcessor, ResourceResolver resourceResolver) {
        List<String> stylesList = new ArrayList<>();
        if (Objects.isNull(doc) || Objects.isNull(requestResponseFactory) || Objects.isNull(requestProcessor) ||
                Objects.isNull(resourceResolver)) {
            return stylesList;
        }
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
        Elements styleTags = doc.select("style");
        for (Element styleTag : styleTags) {
            String styles = styleTag.getAllElements().get(0).data();
            stylesList.add(styles);
            styleTag.remove();
        }
        return stylesList;
    }
}
