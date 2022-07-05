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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class HrefProcessor {
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
        List<Href> encodedHrefs = getEncodedHrefs(text);
        if (encodedHrefs.isEmpty()) {
            return text;
        }
        for (Href encodedHref : encodedHrefs) {
            text = text.replaceAll(encodedHref.getOriginal(), encodedHref.getDecoded());
        }
        return text;
    }

    static List<Href> getEncodedHrefs(String text) {
        if (StringUtils.isEmpty(text)) {
            return Collections.emptyList();
        }
        Pattern pattern = Pattern.compile("href=\"(.*?)\"");
        Matcher matcher = pattern.matcher(text);
        List<Href> hrefs = new ArrayList<>();
        while (matcher.find()) {
            String original = matcher.group();
            String decoded = original.replaceAll(ENCODED_OPENING_ACC_MARKUP, OPENING_ACC_MARKUP)
                    .replaceAll(ENCODED_CLOSING_ACC_MARKUP, CLOSING_ACC_MARKUP).replaceAll(ENCODED_SPACE, SPACE);
            if (original.equals(decoded)) {
                continue;
            }
            hrefs.add(new Href(original, decoded));
        }
        return hrefs;
    }

    public static class Href {
        private final String original;
        private final String decoded;

        public Href(String original, String decoded) {
            this.original = original;
            this.decoded = decoded;
        }

        public String getOriginal() {
            return original;
        }

        public String getDecoded() {
            return decoded;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Href href = (Href) o;
            return Objects.equals(original, href.original) && Objects.equals(decoded, href.decoded);
        }

        @Override
        public int hashCode() {
            return Objects.hash(original, decoded);
        }

    }
}
