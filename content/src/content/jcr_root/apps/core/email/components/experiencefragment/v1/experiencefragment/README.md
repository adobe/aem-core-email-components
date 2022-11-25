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
# Experience Fragment Component

Extensible Experience Fragment component for the Core Email Components for composing campaign content written in HTL and based on the [Experience Fragment Core Component](https://github.com/adobe/aem-core-wcm-components/tree/main/content/src/content/jcr_root/apps/core/wcm/components/experiencefragment/v2/experiencefragment)

The Experience Fragment Component allows embedding an Experience Fragment in campaign content.

## Features

* Can be used on both templates and pages
* Defines a configurable Experience Fragment variation to be displayed
* HTML ID

## Edit Dialog Properties

The following property is written to JCR for the experience fragment component and is expected to be available as a Resource property:

`./fragmentVariationPath` - defines the path to the experience fragment variation to be rendered.
`./id` - defines the component HTML ID attribute

## Tests and Coverage

The class `ExperienceFragmentIT` is the Selenium test class. It checks for the existence of the dialog in fullscreen enabled/disabled.

## Information

* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.5
* **Status**: production-ready
* **User Documentation**: [https://www.adobe.com/go/aem_cmp_email_xf_v1](https://www.adobe.com/go/aem_cmp_email_xf_v1)
* **Authors**: 
