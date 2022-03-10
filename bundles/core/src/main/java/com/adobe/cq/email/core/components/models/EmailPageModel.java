/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.email.core.components.models;


import com.adobe.cq.wcm.core.components.models.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public class EmailPageModel {
    @ValueMapValue
    @Optional
    @Default(values = "")
    private String referenceUrl;

    @Self
    protected SlingHttpServletRequest request;

    @Inject
    protected Resource resource;

    @Inject
    private ModelFactory modelFactory;

    private static final Logger LOG = LoggerFactory.getLogger(EmailPageModel.class);

    private Page page;

    @PostConstruct
    private void initModel() {
        LOG.debug("Init model.");
        request.setAttribute("referenceUrl", referenceUrl);
        page = modelFactory.getModelFromWrappedRequest(request, resource, Page.class);
    }

    /**
     * Get the wcm core page.
     * @return current wcm core page
     */
    public Page getPage() {
        return page;
    }

    /**
     * Get the reference url for links.
     * @return  reference url
     */
    public String getReferenceUrl() {
        return referenceUrl;
    }

}
