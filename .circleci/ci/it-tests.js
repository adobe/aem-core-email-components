/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
 'use strict';

 const ci = new (require('./ci.js'))();
 
 ci.context();
 
 ci.stage('Project Configuration');
 const config = ci.restoreConfiguration();
 console.log(config);
 const qpPath = '/home/circleci/cq';
 const buildPath = '/home/circleci/build';
 const { TYPE, BROWSER, AEM } = process.env;
 
 try {
     ci.stage("Integration Tests");
     let wcmVersion = ci.sh('mvn help:evaluate -Dexpression=core.wcm.components.version -q -DforceStdout', true);
     let excludedCategory = AEM === 'classic' ? 'junit.category.IgnoreOn65' : 'junit.category.IgnoreOnCloud';
 
     ci.dir(qpPath, () => {
         // Connect to QP
         ci.sh('./qp.sh -v bind --server-hostname localhost --server-port 55555');
         
         // Download latest add-on release from artifactory
         let extras = "";
         if (AEM == 'classic') {
            // install core wcm components dependeny
             extras += ` --bundle com.adobe.cq:core.wcm.components.all:${wcmVersion}:zip`;
         }
         
         // Start CQ
         ci.sh(`./qp.sh -v start --id author --runmode author --port 4502 --qs-jar /home/circleci/cq/author/cq-quickstart.jar \
             --bundle org.apache.sling:org.apache.sling.junit.core:1.0.23:jar \
             ${extras} \
             ${ci.addQpFileDependency(config.modules['core.email.components.content'])} \
             ${ci.addQpFileDependency(config.modules['core.email.components.core'])} \
             ${ci.addQpFileDependency(config.modules['core.email.components.examples.ui.apps'])} \
             ${ci.addQpFileDependency(config.modules['core.email.components.examples.ui.content'])} \
             ${ci.addQpFileDependency(config.modules['core.email.components.it.content'])} \
             --vm-options \\\"-Xmx1536m -XX:MaxPermSize=256m -Djava.awt.headless=true -javaagent:${process.env.JACOCO_AGENT}=destfile=crx-quickstart/jacoco-it.exec\\\"`);
     });
 
 
 
     // Run integration tests
     if (TYPE === 'integration') {
         ci.dir('testing/it/http', () => {
             ci.sh(`mvn clean verify -U -B \
                 -Ptest-all \
                 -Dexclude.category=${excludedCategory} \
                 -Dsling.it.instance.url.1=http://localhost:4502 \
                 -Dsling.it.instance.runmode.1=author \
                 -Dsling.it.instances=1`);
         });
     }
     
     // Run UI tests
     if (TYPE === 'selenium') {
        ci.dir('testing/it/e2e-selenium', () => {
            ci.sh(`IP=localhost mvn clean verify -U -B \
                -Ptest-all \
                -Dexclude.category=${excludedCategory} \
                -Dsel.jup.default.browser=${BROWSER}`);
        });
     }
     
     ci.dir(qpPath, () => {
         // Stop CQ
         ci.sh('./qp.sh -v stop --id author');
     });
     
     // No coverage for UI tests
     if (TYPE === 'selenium') {
         return;
     }
     
     // Create coverage reports
     const createCoverageReport = () => {
         // Executing the integration tests runs also executes unit tests and generates a Jacoco report for them. To 
         // strictly separate unit test from integration test coverage, we explicitly delete the unit test report first.
         ci.sh('rm -rf target/site/jacoco');
 
         // Download Jacoco file which is exposed by a webserver running inside the AEM container.
         ci.sh('curl -O -f http://localhost:3000/crx-quickstart/jacoco-it.exec');
 
         // Generate new report
         ci.sh(`mvn -B org.jacoco:jacoco-maven-plugin:${process.env.JACOCO_VERSION}:report -Djacoco.dataFile=jacoco-it.exec`);
 
         // Upload report to codecov
         ci.sh('curl -s https://codecov.io/bash | bash -s -- -c -F integration -f target/site/jacoco/jacoco.xml');
     };
 
     ci.dir('bundles/core', createCoverageReport);
 
 } finally { // Always download logs from AEM container
     ci.sh('mkdir logs');
     ci.dir('logs', () => {
         // A webserver running inside the AEM container exposes the logs folder, so we can download log files as needed.
         ci.sh('curl -O -f http://localhost:3000/crx-quickstart/logs/error.log');
         ci.sh('curl -O -f http://localhost:3000/crx-quickstart/logs/stdout.log');
         ci.sh('curl -O -f http://localhost:3000/crx-quickstart/logs/stderr.log');
         ci.sh('curl -O -f http://localhost:3000/crx-quickstart/logs/access.log');
         ci.sh(`find . -name '*.log' -type f -size +32M -exec echo 'Truncating: ' {} \\; -execdir truncate --size 32M {} +`);
     });
 }