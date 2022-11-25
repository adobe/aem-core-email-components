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
# Container Component

Extensible container component for the Core Email Components for composing campaign content written in HTL and based on the [Container Core Component](https://github.com/adobe/aem-core-wcm-components/tree/main/content/src/content/jcr_root/apps/core/wcm/components/container/v1/container)

The Container Component provides a simplified responsive grid consisting of six columns.

## Features

* Configurable layout with dropdown of different column split up options
* Extends the core container component

## Use Object

The Container component uses the `com.adobe.cq.email.core.components.models.Container` Sling model as its Use-object.

## Edit Dialog Properties

The following properties are written to JCR for this Container component and are expected to be available as Resource properties:

* `./layout` - defines the layout type with the column split up of either full-width, half; half, one-third; two-third, two-third; one-third and third; third; or third

### Common Properties

* `./id` - defines the component HTML ID attribute

## Client Libraries

The component provides a `core.email.components.container` client library category that contains a recommended base CSS styling component. It should be added to a relevant site client library using the embed property.

When using a proxy component of the container, the `cq:template` folder from the container superType has to be copied.

## Information

* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.5
* **Status**: production-ready
* **User Documentation**: [https://www.adobe.com/go/aem_cmp_email_container_v1](https://www.adobe.com/go/aem_cmp_email_container_v1)
* **Authors**:
