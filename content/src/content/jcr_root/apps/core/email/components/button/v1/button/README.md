<!--
Copyright 2022 Adobe

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
# Button Component

Extensible button component for the Core Email Components for composing campaign content written in HTL and based on the [Button Core Component](https://github.com/adobe/aem-core-wcm-components/tree/main/content/src/content/jcr_root/apps/core/wcm/components/button/v2/button)

The Button Component serves as a call-to-action link.

## Features

* Define text of button
* Define link for when button is clicked
* Define icon for the button
* Insert Campaign variables into text fields
* HTML ID

## Technical Details

This component inherit its structure and behavior from the [Button component (v2)](https://github.com/adobe/aem-core-wcm-components/tree/main/content/src/content/jcr_root/apps/core/wcm/components/button/v2/button) of the `core.wcm.component` package. The only change is the integration of the personalization plugin, which results in the addition of a button to insert personalization fields from Adobe Campaign in the **Text** and **Link** fields in the following locations:

* Configuration dialog toolbar
* Full screen configuration dialog toolbar

## Tests and Coverage

The class `ButtonIT` is the Selenium test class. It checks for the existence of the personalization buttons in each of the previously mentioned windows.

## Information

* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.5
* **Status**: production-ready
* **User Documentation**: [https://www.adobe.com/go/aem_cmp_email_button_v1](https://www.adobe.com/go/aem_cmp_email_button_v1)
* **Authors**:
