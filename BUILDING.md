# Building AEM Email Core Components

## Compiling the Core Components

To compile your own version of the Core Components, you can build and install everything on your running AEM instance by issuing the following command in the top level folder of the project:

    mvn clean install -PautoInstallSinglePackage

You can also install individual packages/bundles by issuing the following command in the top-level folder of the project:

    mvn clean install -PautoInstallPackage -pl <project_name(s)> -am

Note that:
* `-pl/-projects` option specifies the list of projects that you want to install
* `-am/-also-make` options specifies that dependencies should also be built

For convenience, the following deployment profiles are provided when running the Maven install goal with `mvn install`:
* `autoInstallSinglePackage`: Install everything to the AEM author instance.
* `autoInstallSinglePackagePublish`: Install everything to the AEM publish instance.
* `autoInstallPackage`: Install the `ui.content` and `ui.apps` content packages to the AEM author instance.
* `autoInstallPackagePublish`: Install the `ui.content` and `ui.apps` content packages to the  AEM publish instance.

The hostname and port of the instance can be changed with the following user defined properties:
* `aem.host` and `aem.port` for the author instance.
* `aem.publish.host` and `aem.publish.port` for the publish instance.

### AEM as a Cloud Service SDK

When compiling and deploying to AEM as a Cloud Service SDK, you can use the `cloud` profile (in conjunction with
previously documented profiles) to generate `cloud`-ready artifacts (with components located in `/libs` instead
of `/apps`). To allow recompilation of the HTL scripts, you should disable `aem-precompiled-scripts` bundle.

Due to [FELIX-6365](https://issues.apache.org/jira/browse/FELIX-6365), please only use `autoInstallPackage` and
`autoInstallPackagePublish` when working with the AEM as a Cloud Service SDK!

## NPM modules

Modules that export Javscript and CSS are usually also configured as NPM modules.

For these, the following scripts are used:
* `npm run lint` - Runs both JS and CSS linters in parallel.
* `npm run eslint` - Runs JS linter only.
* `npm run eslint:fix` - Runs JS linter only and fixes auto-fixable found issues.
* `npm run stylelint` - Runs CSS linter only.
* `npm run stylelint:fix` - Runs CSS linter only and fixes auto-fixable found issues.
* `npm run sync-pom-version` - Syncs the package.json version with the pom.xml version.

## Branch conventions

### `master` branch

The `master` branch is the stable branch, where releases are cut from. Please send all pull requests
to `development` branch!

### `development` branch

The `development` branch is the staging branch, where feature and bug-fixes are staged and tested before
being cherry-picked to `master` in preparation of a release.

### Other branches

We normally use branches such as:
    * `issue/<number>` for changes related to a specific issue
    * `feature/<name>` for changes related to a specific feature that is still being floated around and not ready for review
    * `release/<version>` for changes that are scheduled for a specific future release, due to incompatibilities with `development` branch
