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
# Segmentation Component

Extensible segmentation component for the Core Email Components for composing campaign content written in HTL and based on the [Tabs Core Component](https://github.com/adobe/aem-core-wcm-components/tree/main/content/src/content/jcr_root/apps/core/wcm/components/tabs/v1/tabs)

The Segmentation Component presents content wrapped with a conditional statement which renders only when the condition is true.

## Features

* Allows the creation of different containers for each segment and its content
* Allowed components can be configured through policy configuration.
* Navigation between container panels via the toolbar
* HTML ID

## Component Policy Configuration Properties

The component policy dialog allows definition of allowed components for the Segmentation component.

## Edit Dialog Properties

The following properties are written to JCR for this Segmentation component and are expected to be available as `Resource` properties:

* `./id` - Defines the component HTML ID attribute

## Client Libraries

The component provides a `core.email.components.segmentation.v1.segmentation` library category that contains a recommended base CSS styling. It should be added to a relevant site client library using the embed property.

## Information

* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.5
* **Status**: production-ready
* **User Documentation**: [https://www.adobe.com/go/aem_cmp_email_segmentation_v1](https://www.adobe.com/go/aem_cmp_email_segmentation_v1)
* **Authors**:
