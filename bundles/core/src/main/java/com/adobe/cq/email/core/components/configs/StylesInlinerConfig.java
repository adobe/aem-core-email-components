package com.adobe.cq.email.core.components.configs;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Styles inliner configuration")
public @interface StylesInlinerConfig {

    String[] wrapperDivClassesToBeRemoved() default {"aem-Grid", "aem-GridColumn"};
}
