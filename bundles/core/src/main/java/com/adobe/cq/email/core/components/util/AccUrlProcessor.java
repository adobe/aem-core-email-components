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

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for ACC-syntax checking in URLs.
 * <p>
 * This is in particular necessary in order to undo the sanitization the {@link com.adobe.cq.wcm.core.components.internal.link.DefaultPathProcessor}
 * applies on {@link com.adobe.cq.wcm.core.components.commons.link.Link}s. But also to undo the encoding applied to links in the RTE Link
 * Plugin.
 */
public class AccUrlProcessor {

    private static final String ENCODED_LT = "%3C";
    private static final String ENCODED_GT = "%3E";
    private static final String ENCODED_PERCENTAGE = "%25";
    private static final String OPENING = "<%";
    private static final String CLOSING = "%>";

    private static final Map<String, List<String>> ENCODINGS;

    static {
        ENCODINGS = new LinkedHashMap<>(3);
        ENCODINGS.put(OPENING, Arrays.asList(ENCODED_LT + ENCODED_PERCENTAGE, ENCODED_LT + "%", "&lt;%"));
        ENCODINGS.put(CLOSING, Arrays.asList(ENCODED_PERCENTAGE + ENCODED_GT, "%" + ENCODED_GT, "%&gt;"));
        ENCODINGS.put(" ", Collections.singletonList("%20"));
    }


    /**
     * Processes an URL and, if it contains the ACC opening or closing markup that has been URL-encoded, it decodes it
     *
     * @param url the URL
     */
    public static String process(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }

        // the encoded < and > are trimmed of by new URI(url, false).toString() for urls that contain only ACC markup
        if (url.startsWith(ENCODED_PERCENTAGE) && url.endsWith(ENCODED_PERCENTAGE)) {
            url = ENCODED_LT + url + ENCODED_GT;
        }

        for (Map.Entry<String, List<String>> entry : ENCODINGS.entrySet()) {
            String decoded = entry.getKey();
            for (String encoded : entry.getValue()) {
                url = url.replaceAll(encoded, decoded);
            }
        }

        return url;
    }
}
