<!--
Copyright 2021 Adobe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
# Text Component
Extensible text component for the Core Email Components for composing campaign content written in HTL and based on the [Text Core Component](https://github.com/adobe/aem-core-wcm-components/tree/main/content/src/content/jcr_root/apps/core/wcm/components/text/v2/text)

## Features

* Allows the creation of different containers for each segment and its content
* Allowed components can be configured through policy configuration.
* Navigation between container panels via the toolbar

## Technical Details

This component inherit its structure and behavior from the [Text component (v2) of the core.wcm.component package.](https://github.com/adobe/aem-core-wcm-components/tree/main/content/src/content/jcr_root/apps/core/wcm/components/text/v2/text) The only change is the integration of the personalization plugin, which adds buttons to insert Adobe Campaign personalization fields in the following locations:

* Inline editor toolbar
* Full screen editor toolbar
* Configuration dialog toolbar
* Full screen configuration dialog toolbar

There is some custom logic on the Java model class `com.adobe.cq.email.core.components.models.TextModel` related to link handling. Specifically, the `getText` method calls the `com.adobe.cq.email.core.components.util.HrefProcessor` utility class, which processes the actual content of Rich Text Component and looks for link elements. If they are present, it checks to see if the `href` attribute contains some URL-encoded Adobe Campaign markup. If so, it decodes it and adds the `x-cq-linkchecker="skip"` attribute to skip AEM link checker execution on that link.

## Tests and Coverage

The class `EmailTextIt` is the Selenium test class. It checks for the existence of the personalization buttons in each of the previously mentioned windows.

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.5
* **Status**: production-ready
* **User Documentation**: [https://www.adobe.com/go/aem_cmp_email_text_v1](https://www.adobe.com/go/aem_cmp_email_text_v1)
* **Authors**: 