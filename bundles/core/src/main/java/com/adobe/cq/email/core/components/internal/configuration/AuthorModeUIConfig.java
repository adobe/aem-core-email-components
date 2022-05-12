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
package com.adobe.cq.email.core.components.internal.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Context Aware Configuration class used by {@link com.adobe.cq.email.core.components.internal.services.CoreEmailAuthoringUIModeServiceImpl}
 */
@ObjectClassDefinition(name = "Core Email AuthorUI Service Configuration",
                       description = "")
public @interface AuthorModeUIConfig {

    /**
     * Getter for the default authoring UI mode
     *
     * @return the default authoring UI mode, default value = "TOUCH"
     */
    @AttributeDefinition(name = "Default Authoring Mode",
                         description = "")
    String getDefaultAuthoringUIMode() default "TOUCH";

    /**
     * Getter for the classic editor URL
     *
     * @return the classic editor URL, default value = "/cf#"
     */
    @AttributeDefinition(name = "Classic Editor URL",
                         description = "")
    String getClassicEditorUrl() default "/cf#";

    /**
     * Getter for the touch editor URL
     *
     * @return the touch editor URL, default value = "/editor.html"
     */
    @AttributeDefinition(name = "Touch Editor URL",
                         description = "")
    String getTouchEditorUrl() default "/editor.html";

}
