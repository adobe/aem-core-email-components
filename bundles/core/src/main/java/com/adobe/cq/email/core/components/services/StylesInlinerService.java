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
package com.adobe.cq.email.core.components.services;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * Service that processes HTML pages and inlines CSS style
 */
public interface StylesInlinerService {

    /**
     * This method accepts the html string as the input, parses it, reads the style rules and adds it to the respective elements in the
     * html. Unused style rules, along with media query styles and pseudo classes, are inserted in the <style> tag.
     *
     * @param resourceResolver the resource resolver object
     * @param jsonContent      the json string containing HTML
     * @param charset          the request charset
     * @return json object that has a "html" property that contains the html with inline styles
     */
    String getHtmlWithInlineStylesJson(ResourceResolver resourceResolver, String jsonContent, String charset);

    /**
     * This method accepts the html string as the input, parses it, reads the style rules and adds it to the respective elements in the
     * html. Unused style rules, along with media query styles and pseudo classes, are inserted in the <style> tag.
     *
     * @param resourceResolver the resource resolver object
     * @param html             the html string
     * @return html with inline styles
     */
    String getHtmlWithInlineStyles(ResourceResolver resourceResolver, String html);

}
