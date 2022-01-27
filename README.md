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

* **[Component Library](https://www.adobe.com/go/aem_cmp_library):** A collection of examples to view the components in their various configurations.
* **[Component Documentation](https://docs.adobe.com/content/help/en/experience-manager-core-components/using/introduction.html):** For developers and authors, with details about each component.
* Get Started:
  - **[WKND Tutorial](https://docs.adobe.com/content/help/en/experience-manager-learn/getting-started-wknd-tutorial-develop/overview.html):** A two-day tutorial for building a new site.
  - **[Summit Tutorial](https://expleague.azureedge.net/labs/L767/index.html):** A two-hour tutorial for building a new site (from a Lab at US Summit 2019).
  - **[Gems Webinar](https://helpx.adobe.com/experience-manager/kt/eseminars/gems/AEM-Core-Components.html):** A guided tour of the Core Components (recorded on Dec 2018).

## Features

* **Production-Ready:** 28 robust components that are well tested, widely used, and that perform well.
* **Cloud-Ready:** Whether on [AEM as a Cloud Service](https://docs.adobe.com/content/help/en/experience-manager-cloud-service/landing/home.html), on [Adobe Managed Services](https://github.com/adobe/aem-project-archetype/tree/master/src/main/archetype/dispatcher.ams), or on-premise, they just work.
* **Versatile:** The components represent generic concepts with which the authors can assemble nearly any layout.
* **Configurable:** Template-level [content policies](https://docs.adobe.com/content/help/en/experience-manager-65/developing/platform/templates/page-templates-editable.html#content-policies) define which features the page authors are allowed to use or not.
* **Trackable:** The [Adobe Client Data Layer](DATA_LAYER_INTEGRATION.md) integration allows to track all aspects of the visitor experience.
* **Accessible:** They comply [WCAG 2.1 standard](https://www.w3.org/TR/WCAG21/), provide ARIA labels, and support keyboard navigation.
* **SEO-Friendly:** The HTML output is semantic and provides schema.org microdata annotations.
* **WebApp-Ready:** The [streamlined JSON output](https://docs.adobe.com/content/help/en/experience-manager-learn/foundation/development/develop-sling-model-exporter.html) allows client-side rendering, still with a possibility of [in-context editing](https://docs.adobe.com/content/help/en/experience-manager-learn/sites/spa-editor/spa-editor-framework-feature-video-use.html).
* **Design Kit:** A [UI kit for Adobe XD](https://experienceleague.adobe.com/docs/experience-manager-learn/assets/AEM-CoreComponents-UI-Kit.xd) allows designers to create wireframes that they can then [style as needed](https://github.com/adobe/aem-guides-wknd/releases/download/aem-guides-wknd-0.0.2/AEM_UI-kit-WKND.xd).
* **Themeable:** The components implement the [Style System](https://docs.adobe.com/content/help/en/experience-manager-65/developing/components/style-system.html), and the markup follows [BEM CSS conventions](http://getbem.com/).
* **Customizable:** Several patterns allow [easy customization](https://docs.adobe.com/content/help/en/experience-manager-core-components/using/developing/customizing.html), from adjusting the HTML to advanced functionality reuse.
* **Versioned:** The [versioning policy](https://github.com/adobe/aem-core-email-components/wiki/Versioning-policies) ensures we won't break your site when improving things that might impact you.
* **Open Sourced:** If something is not as it should, [contribute](CONTRIBUTING.md) your improvements!

### Template Components

1. [Page](content/src/content/jcr_root/apps/core/email/components/page/v2/page)

### Page Authoring Components

2. [Title](content/src/content/jcr_root/apps/core/email/components/title/v2/title)
3. [Text](content/src/content/jcr_root/apps/core/email/components/text/v2/text)
4. [Image](content/src/content/jcr_root/apps/core/email/components/image/v2/image)
5. [Button](content/src/content/jcr_root/apps/core/email/components/button/v1/button)
6. [Teaser](content/src/content/jcr_root/apps/core/email/components/teaser/v1/teaser)
7. [Experience Fragment](content/src/content/jcr_root/apps/core/email/components/experiencefragment/v1/experiencefragment)
8. [Content Fragment](content/src/content/jcr_root/apps/core/email/components/contentfragment/v1/contentfragment)


### Container Components

9. [Container](content/src/content/jcr_root/apps/core/email/components/container/v1/container)


### Roadmap

To learn about the main upcoming components and features, visit the [roadmap wiki page](https://github.com/adobe/aem-core-email-components/wiki).

## Usage

To include the Core Components in a new project, we strongly advise to use the [AEM Project Archetype](https://github.com/adobe/aem-project-archetype); this guarantees a starting point that complies to all recommended practices from Adobe.

For existing projects, take example from the [AEM Project Archetype](https://github.com/adobe/aem-project-archetype) by looking at the `core.email.components` references in the main [`pom.xml`](https://github.com/adobe/aem-project-archetype/blob/master/src/main/archetype/pom.xml), in [`all/pom.xml`](https://github.com/adobe/aem-project-archetype/blob/master/src/main/archetype/all/pom.xml), and in [`ui.apps/pom.xml`](https://github.com/adobe/aem-project-archetype/blob/master/src/main/archetype/ui.apps/pom.xml). For the rest, make sure to create Proxy Components, to load the client libraries and to allow the components on the template, as instructed in [Using Core Components](https://docs.adobe.com/content/help/en/experience-manager-core-components/using/get-started/using.html).

### System Requirements

Core Components | AEM as a Cloud Service | AEM 6.5 | AEM 6.4 | Java SE | Maven
----------------|------------------------|---------|---------|---------|---------


For the requirements from previous Core Component releases, see [Historical System Requirements](VERSIONS.md).

The Core Components require the use of [editable templates](https://docs.adobe.com/content/help/en/experience-manager-learn/sites/page-authoring/template-editor-feature-video-use.html) and do not support Classic UI nor static templates. If needed, check out the [AEM Modernization Tools](https://opensource.adobe.com/aem-modernize-tools/pages/tools.html).

Setup your local development environment for [AEM as a Cloud Service SDK](https://docs.adobe.com/content/help/en/experience-manager-learn/cloud-service/local-development-environment-set-up/overview.html) or for [older versions of AEM](https://docs.adobe.com/content/help/en/experience-manager-learn/foundation/development/set-up-a-local-aem-development-environment.html).

### Building

To compile your own version of the Core Components, you can build and install everything on your running AEM instance by issuing the following command in the top level folder of the project:

    mvn clean install -PautoInstallSinglePackage

You can also install individual packages/bundles by issuing the following command in the top-level folder of the project:

    mvn clean install -PautoInstallPackage -pl <project_name(s)> -am

With AEM as a Cloud Service SDK, use the `cloud` profile as follows to deploy the components into `/libs` instead of `/apps`:

    mvn clean install -PautoInstallSinglePackage,cloud

Note that:
* `-pl/-projects` option specifies the list of projects that you want to install
* `-am/-also-make` options specifies that dependencies should also be built

For detailed informations see [BUILDING.md](BUILDING.md).

### Configuration

The Core Components leverage both OSGi configurations and Sling context-aware configurations. For a description of defined configurations see [CONFIGS.md](CONFIGS.md)

### Known Issues

