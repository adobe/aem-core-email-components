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

@Component(service = InlineStylesConfig.class,
           immediate = true)
@Designate(ocd = InlineStylesConfig.Cfg.class)
public class InlineStylesConfig {

    public static final String EMBEDDED_STYLES = "embeddedStyles";
    public static final String CLIENT_LIBRARIES = "clientLibraries";

    @ObjectClassDefinition(name = "Inline Styles Config")
    public @interface Cfg {
        @AttributeDefinition(
                name = "Styles Inclusion",
                description = "How will the styles be included in the page? (default = Client Libraries)",
                options = {
                        @Option(label = "Client Libraries",
                                value = CLIENT_LIBRARIES),
                        @Option(label = "Embedded Styles",
                                value = EMBEDDED_STYLES)
                })
        String stylesInclusion() default CLIENT_LIBRARIES;
    }

    private String stylesInclusion;

    @Activate
    protected void activate(final Cfg cfg) {
        this.stylesInclusion = cfg.stylesInclusion();
    }

    public String getStylesInclusion() {
        return stylesInclusion;
    }

}
