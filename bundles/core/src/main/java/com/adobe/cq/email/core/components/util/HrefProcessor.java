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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HrefProcessor {

    private static final String BODY_ELEMENT = "body";
    private static final String LINK_ELEMENT = "a";
    private static final String HREF_ATTRIBUTE = "href";
    private static final String X_CQ_LINKCHECKER_ATTRIBUTE = "x-cq-linkchecker";
    private static final String SKIP_VALUE = "skip";
    private static final String ENCODED_OPENING_ACC_MARKUP = "%3C%";
    private static final String ENCODED_CLOSING_ACC_MARKUP = "%&gt;";
    private static final String ENCODED_SPACE = "%20";
    private static final String OPENING_ACC_MARKUP = "<%";
    private static final String CLOSING_ACC_MARKUP = "%>";
    private static final String SPACE = " ";

    public static String process(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        Document doc = Jsoup.parse(text);
        if (Objects.isNull(doc)) {
            return text;
        }
        Element body = doc.selectFirst(BODY_ELEMENT);
        if (Objects.isNull(body)) {
            return text;
        }
        updateHrefs(body);
        return body.html();
    }

    private static void updateHrefs(Element body) {
        if (Objects.isNull(body)) {
            return;
        }
        for (Element element : body.select(LINK_ELEMENT)) {
            String original = element.attr(HREF_ATTRIBUTE);
            String decoded = original.replaceAll(ENCODED_OPENING_ACC_MARKUP, OPENING_ACC_MARKUP)
                    .replaceAll(ENCODED_CLOSING_ACC_MARKUP, CLOSING_ACC_MARKUP).replaceAll(ENCODED_SPACE, SPACE);
            if (original.equals(decoded)) {
                continue;
            }
            element.attr(HREF_ATTRIBUTE, decoded);
            element.attr(X_CQ_LINKCHECKER_ATTRIBUTE, SKIP_VALUE);
        }
    }

}
