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

import com.adobe.cq.email.core.components.enumerations.HtmlSanitizingMode;

public class HtmlSanitizer {
    private static final String XSS_TAGS_RESOURCE = "sanitizer/xss_tags.txt";
    private static final String XSS_EVENTS_RESOURCE = "sanitizer/xss_events.txt";

    private static final Logger LOG = LoggerFactory.getLogger(HtmlSanitizer.class.getName());


    public static String sanitizeHtml(HtmlSanitizingMode mode, String html) {
        if (StringUtils.isEmpty(html)) {
            return html;
        }
        if (Objects.isNull(mode)) {
            mode = HtmlSanitizingMode.FULL;
        }
        Document document = sanitizeDocument(mode, Jsoup.parse(html));
        if (Objects.isNull(document)) {
            return null;
        }
        return document.outerHtml();
    }

    public static Document sanitizeDocument(HtmlSanitizingMode mode, Document parsed) {
        if (Objects.isNull(parsed)) {
            return null;
        }
        if (HtmlSanitizingMode.NONE.equals(mode)) {
            return parsed;
        }
        if (HtmlSanitizingMode.FULL.equals(mode) || HtmlSanitizingMode.REMOVE_SCRIPT_TAGS_ONLY.equals(mode)) {
            parsed.select("script").remove();
        }
        if (HtmlSanitizingMode.FULL.equals(mode)) {
            removeXssEvents(parsed);
        }
        return parsed;
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
