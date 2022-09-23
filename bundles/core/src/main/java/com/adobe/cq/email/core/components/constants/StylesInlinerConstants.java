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
package com.adobe.cq.email.core.components.constants;

/**
 * Utility that contains various constants used for style inlining
 */
public class StylesInlinerConstants {
    /**
     * Selector for style inliner servlet
     */
    public static final String INLINE_STYLES_SELECTOR = "inline-styles";

    /**
     * "link" tag used when parsing HTML page
     */
    public static final String LINK_TAG = "link";
    /**
     * "style" tag used when parsing HTML page
     */
    public static final String STYLE_TAG = "style";

    /**
     * "style" attribute used when parsing HTML page
     */
    public static final String STYLE_ATTRIBUTE = "style";
    /**
     * "stylesheet" attribute used when parsing HTML page
     */
    public static final String STYLESHEET_ATTRIBUTE = "stylesheet";
    /**
     * "rel" attribute used when parsing HTML page
     */
    public static final String REL_ATTRIBUTE = "rel";
    /**
     * Style delimiters used when parsing HTML page
     */
    public static final String STYLE_DELIMS = "{}";
    /**
     * New line character used when parsing HTML page
     */
    public static final String NEW_LINE = "\n";
    /**
     * Comments regex used when parsing HTML page
     */
    public static final String COMMENTS_REGEX = "\\/\\*[^*]*\\*+([^/*][^*]*\\*+)*\\/";
    /**
     * "head" tag used when parsing HTML page
     */
    public static final String HEAD_TAG = "head";
    /**
     * "!important" selector used to identify if a CSS rule is important
     */
    public static final String IMPORTANT_RULE = "!important";
    /**
     * "href" attribute used when parsing HTML page
     */
    public static final String HREF_ATTRIBUTE = "href";
}
