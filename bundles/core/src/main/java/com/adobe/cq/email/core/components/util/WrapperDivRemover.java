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

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that remove specific DIVs from an HTML page
 */
public class WrapperDivRemover {
    private static final Logger LOG = LoggerFactory.getLogger(WrapperDivRemover.class);

    private WrapperDivRemover() {
        // to avoid instantiation
    }

    /**
     * Remove the DIVs selected from the HTML {@link Document}
     *
     * @param doc                          the HTML {@link Document}
     * @param wrapperDivClassesToBeRemoved the DIV classes to be removed
     */
    public static void removeWrapperDivs(Document doc, String[] wrapperDivClassesToBeRemoved) {
        if (Objects.isNull(doc) || Objects.isNull(wrapperDivClassesToBeRemoved) || wrapperDivClassesToBeRemoved.length == 0) {
            return;
        }
        try {
            Element parent = doc.parent();
            Elements children = doc.children();
            removeWrapperDivs(parent, children, wrapperDivClassesToBeRemoved);
        } catch (Throwable e) {
            LOG.warn("Error removing wrapper DIVs: " + e.getMessage(), e);
        }
    }

    private static void removeWrapperDivs(Element parent, Elements children, String[] wrapperDivClassesToBeRemoved) {
        if (Objects.isNull(children) || children.isEmpty()) {
            return;
        }
        for (Element child : children) {
            if (child.tagName().equalsIgnoreCase("div") && containsClassToBeRemoved(child, wrapperDivClassesToBeRemoved)) {
                child.unwrap();
                removeWrapperDivs(parent, parent.children(), wrapperDivClassesToBeRemoved);
            } else {
                removeWrapperDivs(child, child.children(), wrapperDivClassesToBeRemoved);
            }
        }
    }

    private static boolean containsClassToBeRemoved(Element element, String[] wrapperDivClassesToBeRemoved) {
        String elementClassAttribute = element.attr("class");
        if (StringUtils.isEmpty(elementClassAttribute)) {
            return false;
        }
        for (String wrapperDivClassToBeRemoved : wrapperDivClassesToBeRemoved) {
            if (elementClassAttribute.contains(wrapperDivClassToBeRemoved)) {
                return true;
            }
        }
        return false;
    }

}
