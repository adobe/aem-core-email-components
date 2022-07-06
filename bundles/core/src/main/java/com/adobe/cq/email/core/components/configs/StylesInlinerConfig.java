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
package com.adobe.cq.email.core.components.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.adobe.cq.email.core.components.pojo.HtmlInlinerConfiguration;

/**
 * OSGI configuration class for {@link com.adobe.cq.email.core.components.services.StylesInlinerService}
 */
@ObjectClassDefinition(name = "Styles inliner configuration")
public @interface StylesInlinerConfig {

    /**
     * DIV classes that will be removed from processed HTML
     *
     * @return array of classes, default value is "aem-Grid" and "aem-GridColumn"
     */
    @AttributeDefinition(name = "Wrapper DIV classes to be removed",
                         description = "List of classes of the DIVs to be removed from inlined style output HTML.",
                         type = AttributeType.STRING) String[] wrapperDivClassesToBeRemoved() default {"aem-Grid", "aem-GridColumn"};

    @AttributeDefinition(name = "HTML inliner configuration",
                         description =
                                 "List of HTML attributes to be created if there is a matching style for the specific element. It is a JSON " +
                                         "object with the following attributes: " + HtmlInlinerConfiguration.ELEMENT_TYPE +
                                         ": The type of HTML element to be targeted; " + HtmlInlinerConfiguration.CSS_PROPERTY_REG_EX +
                                         ": Regular expression to match the related CSS property; " +
                                         HtmlInlinerConfiguration.CSS_PROPERTY_OUTPUT_REG_EX +
                                         ": Regular expression to extract the value from the related CSS property to be applied to " +
                                         "the HTML element; " + HtmlInlinerConfiguration.HTML_ATTRIBUTE_NAME +
                                         ": The name of the HTML attribute to be created; " +
                                         HtmlInlinerConfiguration.OVERRIDE_IF_ALREADY_EXISTING +
                                         ": true/false, allows to select to override the HTML attribute if it is " +
                                         "already existing (otherwise it is ignored).",
                         type = AttributeType.STRING) String[] htmlInlinerConfiguration() default {
            HtmlInlinerConfiguration.IMG_WIDTH_DEFAULT};

}
