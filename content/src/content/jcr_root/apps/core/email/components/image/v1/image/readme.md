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
* Linkable to content pages, external URLs or page anchors
* Allows an icon identifier to be configured for rendering an icon
* Personalization fields from Adobe Campaign

## Use Object
The Button component uses the `com.adobe.cq.wcm.core.components.models.Button` Sling model as its Use-object.

## Edit Dialog Properties
The following properties are written to JCR for the Button component and are expected to be available as `Resource` properties:

`./jcr:title` - defines the button text
`./link` - defines the button link
`./linkTarget` - defines if the link should be opened in a new browser tab
`./icon` - defines an icon identifier for rendering an icon
`./accessibilityLabel` - defines an accessibility label for the button
`./id` - defines the component HTML ID attribute

## BEM Description

```text
BLOCK cmp-button
    ELEMENT cmp-button__text
    ELEMENT cmp-button__icon
        MOD cmp-button__icon--<icon>
```

## Icon Styling

Icon styling must be done by users of the Email Core Components. Here's an example from the [Core Components Library.](https://github.com/adobe/aem-core-wcm-components/blob/72e2be7b9599aec7526be1adf3e4b3eaf3cf6f02/examples/ui.apps/src/content/jcr_root/apps/core-components-examples/clientlibs/clientlib-themes/core-components-clean/styles/components/carousel/base.less#L145)

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.5
* **Status**: production-ready
* **User Documentation**: [https://www.adobe.com/go/aem_cmp_email_button_v1](https://www.adobe.com/go/aem_cmp_email_button_v1)
* **Authors**: 
