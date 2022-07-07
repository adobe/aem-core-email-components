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

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for HTML pages sanitization
 */
public class HtmlSanitizer {
    private static final Logger LOG = LoggerFactory.getLogger(HtmlSanitizer.class.getName());
    private static final String SCRIPT_TAG = "script";
    private static final String XSS_TAGS_RESOURCE = "sanitizer/xss_tags.txt";
    private static final String XSS_EVENTS_RESOURCE = "sanitizer/xss_events.txt";

    /**
     * Sanitizes the HTML page
     *
     * @param html the HTML page
     * @return the sanitized HTML page
     */
    public static String sanitizeHtml(String html) {
        if (StringUtils.isEmpty(html)) {
            return html;
        }
        Document document = Jsoup.parse(html);
        sanitizeDocument(document);
        if (Objects.isNull(document)) {
            return null;
        }
        return document.outerHtml();
    }

    /**
     * Sanitizes the HTML {@link Document}
     *
     * @param document the HTML {@link Document}
     */
    public static void sanitizeDocument(Document document) {
        if (Objects.isNull(document)) {
            return;
        }
        document.select(SCRIPT_TAG).remove();
        removeXssEvents(document);
    }

    private static void removeXssEvents(Document document) {
        for (String tag : getResourceValue(XSS_TAGS_RESOURCE)) {
            for (Element element : document.select(tag)) {
                for (String event : getResourceValue(XSS_EVENTS_RESOURCE)) {
                    if (!element.attr(event).isEmpty()) {
                        element.removeAttr(event);
                    }
                }
            }
        }
    }

    private static List<String> getResourceValue(String resourceName) {
        try {
            URL resource = HtmlSanitizer.class.getClassLoader().getResource(resourceName);
            if (Objects.isNull(resource)) {
                return Collections.emptyList();
            }
            List<String> result = new ArrayList<>();
            for (String line : Files.readAllLines(Paths.get(resource.toURI()))) {
                if (line.startsWith("#")) {
                    continue;
                }
                result.add(line);
            }
            return result;
        } catch (Throwable e) {
            LOG.warn("Resource " + resourceName + " not found");
            return Collections.emptyList();
        }
    }

}
