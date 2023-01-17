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
import org.osgi.annotation.versioning.ProviderType;

/**
 * A service that processes HTML and inlines CSS styles.
 */
@ProviderType
public interface StylesInlinerService {

    /**
     * This method accepts the html string as the input, parses it, reads the style rules and adds it to the respective elements in the
     * html. Unused style rules, along with media query styles and pseudo classes, are inserted in the a {@code <style>} tag in the head of
     * the returned html.
     *
     * @param resourceResolver the resource resolver object
     * @param html             the html string
     * @return html with inline styles
     */
    String getHtmlWithInlineStyles(ResourceResolver resourceResolver, String html);

}
