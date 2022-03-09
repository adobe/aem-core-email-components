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
package com.adobe.cq.email.core.components.config;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label = "Styles inliner configuration",
               description = "Context-aware configuration for style inliner service")
public @interface StylesInlinerContextAwareConfiguration {

    @Property(label = "Style merger mode", description = "HTML sanitizing mode.", property = {
        "widgetType=dropdown",
        "dropdownOptions=["
            + "{'value':'PROCESS_SPECIFICITY','description':'Evaluate specificity'},"
            + "{'value':'IGNORE_SPECIFICITY','description':'Ignore specificity'},"
            + "{'value':'ALWAYS_APPEND','description':'Always append'}"
            + "]"
    })
    String stylesMergingMode() default "PROCESS_SPECIFICITY";

    @Property(label = "Sanitizing", description = "HTML sanitizing mode.", property = {
        "widgetType=dropdown",
        "dropdownOptions=["
            + "{'value':'FULL','description':'Full sanitizing'},"
            + "{'value':'REMOVE_SCRIPT_TAGS_ONLY','description':'Remove script tags only'},"
            + "{'value':'NONE','description':'Do not remove anything.'}"
            + "]"
    })
    String htmlSanitizingMode() default "FULL";

}
