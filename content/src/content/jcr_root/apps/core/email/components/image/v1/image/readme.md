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
Image
====
Image component written in HTL for rendering an adaptive image within campaign content based on the [Image Core Component](https://github.com/adobe/aem-core-wcm-components/tree/main/content/src/content/jcr_root/apps/core/wcm/components/image/v3/image)

## Features
* Native loading of optimal rendition
* Scale image to to available width
* Decorative-only images
* Alternate text with option to get it from DAM
* Image linking

## Use Object
The Page component uses the following use objects:

## Component Policy Configuration Properties
The following configuration properties are used:

## Edit Dialog Properties
The following properties are written to JCR for this Page component and are expected to be available as `Resource` properties:

## Extending From This Component

## URL Formats

Images are loaded through the `com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet`, and therefore their URLs have the following patterns:

```text
Author:
/content/<project_path>/<page_path>/<component_path>/<component_name>.coreimg.<quality>.<width>.<extension>/<timestamp>/<filename>.<extension>

Publish:
/content/<project_path>/<page_path>/<component_path>/<component_name>.coreimg.<quality>.<width>.<extension>/<timestamp>/<filename>.<extension>
```

## BEM Description

```text
BLOCK cmp-image
    ELEMENT cmp-image__link
    ELEMENT cmp-image__image
    ELEMENT cmp-image__title
```

## SVG

SVG MIME-types are supported, but have some specific handling. Alternative smart image widths defined at the component policy dialog are ignored for SVG images, with `Image#getWidths` returning an empty array.

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.5, AEM as a Cloud Service
* **Status**: production-ready
* **User Documentation**: [https://www.adobe.com/go/aem_cmp_email_image_v1](https://www.adobe.com/go/aem_cmp_email_image_v1)
* **Authors**: 
