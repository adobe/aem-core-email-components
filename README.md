# AEM Email Core Components
[![CircleCI](https://circleci.com/gh/adobe/aem-core-email-components.svg?style=svg)](https://circleci.com/gh/adobe/aem-core-email-components)
[![Code Coverage](https://codecov.io/gh/adobe/aem-core-email-components/branch/main/graph/badge.svg)](https://codecov.io/gh/adobe/aem-core-email-components)
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

Core Components | AEM as a Cloud Service | AEM 6.5   | Java SE | Maven 
----------------|------------------------|-----------|-------|--------
[2.21.2](https://github.com/adobe/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-2.21.2)  | Continual | 6.5.14.0+ | 8, 11 | 3.3.9+


The Email Core Components require the use of [editable templates](https://docs.adobe.com/content/help/en/experience-manager-learn/sites/page-authoring/template-editor-feature-video-use.html) and do not support Classic UI nor static templates. If needed, check out the [AEM Modernization Tools](https://opensource.adobe.com/aem-modernize-tools/pages/tools.html).

### Known Issues

#### Anchor links get externalised

For older versions of AEM 6.5 and AEM as a Cloud Service the `campaign-link-rewrite` transformer was required for a Apache Sling Rewrite pipeline to be picked up by the Adobe Campaign ContentServlet. This transformer is causing anchor links to be exteranlised. In newer verions of AEM it will be possible to configure the rewriter pipeline used by the ContentServlet without having to use the `campaign-link-rewrite` transformer. See the [E-Mail Link Transformer Documentation](https://github.com/adobe/aem-core-email-components/wiki/E-Mail-Link-Transfomer#anchor-links-a-hrefanchor-get-externalised) for more details.