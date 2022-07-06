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

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for ACC-syntax checking in URLs
 */
public class AccUrlProcessor {

    /**
     * Processes an URL and, if it contains the ACC opening or closing markup that has been URL-encoded, it decodes it
     *
     * @param url the URL
     */
    public static String process(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        String encodedOpeningAccMarkup = URLEncoder.encode("<%");
        String encodedClosingAccMarkup = URLEncoder.encode("%>");
        String encodedPercentageCharacter = URLEncoder.encode("%");
        if (url.startsWith(encodedPercentageCharacter) && url.endsWith(encodedPercentageCharacter)) {
            url = URLEncoder.encode("<") + url + URLEncoder.encode(">");
        }
        if (url.contains(encodedOpeningAccMarkup) || url.contains(encodedClosingAccMarkup)) {
            return URLDecoder.decode(url);
        }
        return url;
    }
}
