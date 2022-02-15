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

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

@Component(service = StylesInlinerConfig.class,
           immediate = true)
@Designate(ocd = StylesInlinerConfig.Cfg.class)
public class StylesInlinerConfig {
    public static final String PROCESS_SPECIFICITY = "PROCESS_SPECIFICITY";
    public static final String IGNORE_SPECIFICITY = "IGNORE_SPECIFICITY";
    public static final String ALWAYS_APPEND = "ALWAYS_APPEND";

    @ObjectClassDefinition(name = "Styles Inliner Config")
    public @interface Cfg {
        @AttributeDefinition(
                name = "Styles merging mode",
                description = "How will the CSS will be merged with HTML elements? (default = Process CSS specificity)",
                options = {
                        @Option(label = "Process CSS specificity",
                                value = PROCESS_SPECIFICITY),
                        @Option(label = "Ignore CSS specificity",
                                value = IGNORE_SPECIFICITY),
                        @Option(label = "Always append CSS properties",
                                value = ALWAYS_APPEND)
                })
        String stylesMergingMode() default PROCESS_SPECIFICITY;
    }

    private String stylesMergingMode;

    @Activate
    protected void activate(final Cfg cfg) {
        this.stylesMergingMode = cfg.stylesMergingMode();
    }

    public String getStylesMergingMode() {
        return stylesMergingMode;
    }

    public void setStylesMergingMode(String stylesMergingMode) {
        this.stylesMergingMode = stylesMergingMode;
    }
}
