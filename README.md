# AEM Email Core Components

[![Test](https://github.com/adobe/aem-core-email-components/workflows/Test/badge.svg)](https://github.com/adobe/aem-core-email-components/actions?query=workflow%3ATest)
[![Code Coverage](https://codecov.io/gh/adobe/aem-core-email-components/branch/master/graph/badge.svg)](https://codecov.io/gh/adobe/aem-core-email-components)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.email.components.all/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.email.components.all)
[![javadoc](https://javadoc.io/badge2/com.adobe.cq/core.email.components.core/javadoc.svg)](https://javadoc.io/doc/com.adobe.cq/core.email.components.core)

Set of standardized Email components for [Adobe Experience Manager (AEM)](https://www.adobe.com/marketing/experience-manager.html) to speed up development time and reduce maintenance cost of your websites.

## Welcome

* **Contributions** are welcome, read our [contributing guide](CONTRIBUTING.md) for more information.
* **Ideas and questions** are discussed on our [public mailing list](https://groups.google.com/forum/#!forum/aem-core-components-dev); you can also [subscribe via email](mailto:aem-core-components-dev+subscribe@googlegroups.com).

## Documentation

* **[Component Documentation](https://github.com/adobe/aem-core-email-components/wiki):** Wiki for developers and authors, with details about each component.


### Template Components

1. [Page](content/src/content/jcr_root/apps/core/email/components/page/v1/page)

### Page Authoring Components

2. [Title](content/src/content/jcr_root/apps/core/email/components/title/v1/title)
3. [Text](content/src/content/jcr_root/apps/core/email/components/text/v1/text)
4. [Image](content/src/content/jcr_root/apps/core/email/components/image/v1/image)
5. [Button](content/src/content/jcr_root/apps/core/email/components/button/v1/button)
6. [Teaser](content/src/content/jcr_root/apps/core/email/components/teaser/v1/teaser)
7. [Experience Fragment](content/src/content/jcr_root/apps/core/email/components/experiencefragment/v1/experiencefragment)
8. [Content Fragment](content/src/content/jcr_root/apps/core/email/components/contentfragment/v1/contentfragment)


### Container Components

9. [Container](content/src/content/jcr_root/apps/core/email/components/container/v1/container)
10. [Segmentation](content/src/content/jcr_root/apps/core/email/components/segmentation/v1/segmentation)


### System Requirements

Core Components | AEM as a Cloud Service | AEM 6.5 SP13 | Java SE | Maven
----------------|------------------------|--------------|---------|---------|


The Email Core Components require the use of [editable templates](https://docs.adobe.com/content/help/en/experience-manager-learn/sites/page-authoring/template-editor-feature-video-use.html) and do not support Classic UI nor static templates. If needed, check out the [AEM Modernization Tools](https://opensource.adobe.com/aem-modernize-tools/pages/tools.html).

### Known Issues

## Release Notes

### Current release:

#### V0.12 (scheduled 12/7)
* Text component - ACC markup not supported on Links by @edoardo-goracci in https://github.com/adobe/aem-core-email-components/pull/181
* Wrapper DIV remover performance fix by @edoardo-goracci in https://github.com/adobe/aem-core-email-components/pull/182

#### V0.11 (scheduled 07/6)
* Disable Layout Mode in Experience fragment page by @noorradi in https://github.com/adobe/aem-core-email-components/pull/163
* Provide capability to set custom segment by @bpauli in https://github.com/adobe/aem-core-email-components/pull/164
* Fix for conditions rendered in preview mode  by @noorradi in https://github.com/adobe/aem-core-email-components/pull/167
* Teaser Component - Personalization Link does not work for CTA links by @bpauli in https://github.com/adobe/aem-core-email-components/pull/169
* Add missing IT for AuthorUIMode check by @bpauli in https://github.com/adobe/aem-core-email-components/pull/172
* Style Inliner - Incorrect encoding when adding <style> attribute by @edoardo-goracci in https://github.com/adobe/aem-core-email-components/pull/171
* Segmentation component HTML template change by @edoardo-goracci in https://github.com/adobe/aem-core-email-components/pull/170
* Segmentation item HTML template fix by @edoardo-goracci in https://github.com/adobe/aem-core-email-components/pull/174
* Remove no longer needed service #141 by @noorradi in https://github.com/adobe/aem-core-email-components/pull/176
* HTML inliner by @edoardo-goracci in https://github.com/adobe/aem-core-email-components/pull/178
* Remove Client Lib category from page.html and added cq-conf to page p… by @noorradi in https://github.com/adobe/aem-core-email-components/pull/179

### Past releases:

#### V0.10 (scheduled 06/15)
* Container component - column policy https://github.com/adobe/aem-core-email-components/issues/125
* Teaser Component - Personalization Plugin for Links tab - https://github.com/adobe/aem-core-email-components/issues/135
* Update Segmentation Component - https://github.com/adobe/aem-core-email-components/issues/89

#### V0.9 (scheduled 06/01)
* Title Component - incorrect link when using campaign RTE Plugin https://github.com/adobe/aem-core-email-components/issues/129
* Button Component - incorrect link when using campaign RTE Plugin https://github.com/adobe/aem-core-email-components/issues/131
* Define and update HTML markup for text, image, button, title & teaser components https://github.com/adobe/aem-core-email-components/issues/23
* Provide content structure https://github.com/adobe/aem-core-email-components/issues/60
* Add JavaDocs to all exposed packages, methods and constants https://github.com/adobe/aem-core-email-components/issues/108
* Container Component - Unable to specify Policy for child components https://github.com/adobe/aem-core-email-components/issues/76
* Container component - column policy https://github.com/adobe/aem-core-email-components/issues/125

#### V0.8 (scheduled 05/18)
* Update and Rename Container Component https://github.com/adobe/aem-core-email-components/issues/90
* Features/teaser component https://github.com/adobe/aem-core-email-components/issues/9
* Features/update link title https://github.com/adobe/aem-core-email-components/issues/104
*  Container Component - Unable to specify Policy for child components https://github.com/adobe/aem-core-email-components/issues/76
* Create Component Versions https://github.com/adobe/aem-core-email-components/issues/106

#### V0.7 (scheduled 05/04)
* Update status section: https://github.com/adobe/aem-core-email-components/issues/62
* Component HTML Markup: https://github.com/adobe/aem-core-email-components/issues/20

#### V0.6 (scheduled 04/20)
* Title component: https://github.com/adobe/aem-core-email-components/issues/8
    * Technical documentation: https://github.com/adobe/aem-core-email-components/wiki/Title-component:-Technical-documentation
* Content Fragment component: https://github.com/adobe/aem-core-email-components/issues/10
    * https://github.com/adobe/aem-core-email-components/wiki/Content-Fragment-(Technical-Documentation)
* Experience Fragment component: https://github.com/adobe/aem-core-email-components/issues/11
    * Technical documentation: https://github.com/adobe/aem-core-email-components/wiki/Experience-Fragment-component-(Technical-Documentation)
* Provide content structure: https://github.com/adobe/aem-core-email-components/issues/60
* Button component: https://github.com/adobe/aem-core-email-components/issues/7
    * Technical documentation: https://github.com/adobe/aem-core-email-components/wiki/Button-component:-Technical-documentation

#### V0.5 (scheduled 04/06)
* Fix RTE personalization in Text component: https://github.com/adobe/aem-core-email-components/issues/61
* Segmentation Component - no "default" option (like Targeting Mode): https://github.com/adobe/aem-core-email-components/issues/66
* Remove CaConfig for StylesInliner and move its options to EmailPage properties: https://github.com/adobe/aem-core-email-components/issues/79
* Style Inliner inlines the generic styles instead of specific one: https://github.com/adobe/aem-core-email-components/issues/63
* Style inliner: always call it when displaying page in "wcmmode=disabled": https://github.com/adobe/aem-core-email-components/issues/75
* Segmentation Component - Adobe Campaign Classic is unable to evaluate the segmentation conditions: https://github.com/adobe/aem-core-email-components/issues/71
* Multiple Javascript errors when loading a page in editor.html: https://github.com/adobe/aem-core-email-components/issues/64
* Container Component - loads with 0px width: https://github.com/adobe/aem-core-email-components/issues/69
* Segmentation Component - Tabs are not appearing as Tabs: https://github.com/adobe/aem-core-email-components/issues/65

#### V0.4 (scheduled 03/23)
* Integration AEM -> ACC
    * Technical documentation: Integrating AEM with ACC
* Image component: https://github.com/adobe/aem-core-email-components/issues/6
    * Technical documentation: https://github.com/adobe/aem-core-email-components/wiki/Image-component:-Technical-documentation
* Segmentation component: https://github.com/adobe/aem-core-email-components/issues/12
    * Technical documentation: https://github.com/adobe/aem-core-email-components/wiki/Segmentation-component-(Technical-Documentation)
    * Technical documentation Segmentation Item: https://github.com/adobe/aem-core-email-components/wiki/Segmentation-Item-component-(Technical-Documentation)
* Externalize URLs: https://github.com/adobe/aem-core-email-components/issues/33
    * Technical documentation: https://github.com/adobe/aem-core-email-components/wiki/UrlMapperService:-Technical-documentation

#### V0.3 (released 03/09)
* Container component: https://github.com/adobe/aem-core-email-components/issues/4
    * Technical documentation: https://github.com/adobe/aem-core-email-components/wiki/Container-Component-(Technical-Documentation)
* Text component: https://github.com/adobe/aem-core-email-components/issues/5
    * Technical documentation: https://github.com/adobe/aem-core-email-components/wiki/Text-component:-Technical-documentation
* Moved Email header component to page properties: https://github.com/adobe/aem-core-email-components/issues/38
    * RTE Personalization: https://github.com/adobe/aem-core-email-components/wiki/RTE-Personalization

####  V0.2 (released 02/23)
* Page component: https://github.com/adobe/aem-core-email-components/issues/3
* Push content from AEM to ACC: https://github.com/adobe/aem-core-email-components/issues/18
    * This will not be part of the AEM Core Email Components project, but of AEM Service Pack 6.5.13
    * For details on how to connect AEM with ACC, please check https://github.com/adobe/aem-core-email-components/wiki/Integrating-AEM-with-ACC

