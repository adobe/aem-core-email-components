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
package com.adobe.cq.email.core.components.internal.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.components.internal.models.EmailPageImpl;
import com.adobe.cq.mcm.campaign.LinkingStatus;
import com.adobe.cq.mcm.campaign.LinkingStatusService;
import com.adobe.cq.mcm.campaign.NewsletterException;
import com.adobe.granite.resourcestatus.ResourceStatus;
import com.adobe.granite.resourcestatus.ResourceStatusProvider;
import com.day.cq.i18n.I18n;
import com.day.cq.mcm.campaign.ACConnectorException;
import com.day.cq.mcm.campaign.CampaignConnector;
import com.day.cq.mcm.campaign.NewsletterStatus;
import com.day.cq.mcm.campaign.StatusService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import com.day.cq.wcm.commons.status.EditorResourceStatus;
import com.day.cq.wcm.webservicesupport.Configuration;

@Component(service = ResourceStatusProvider.class)
public class CampaignStatusProvider implements ResourceStatusProvider {

    private static final Logger LOG = LoggerFactory.getLogger(CampaignStatusProvider.class);

    @Reference
    private PageManagerFactory pageManagerFactory;
    @Reference
    private CampaignConnector connector;
    @Reference
    private StatusService statusService;
    @Reference
    private LinkingStatusService linkingStatusService;
    @Reference
    private ResourceBundleProvider resourceBundleProvider;

    @NotNull
    @Override
    public String getType() {
        return "adobe-campaign";
    }

    @Nullable
    @Override
    public List<ResourceStatus> getStatuses(Resource resource) {
        PageManager pageManager = pageManagerFactory.getPageManager(resource.getResourceResolver());
        Page page = pageManager.getPage(resource.getPath());

        if (page == null) {
            page = pageManager.getContainingPage(resource);
        }

        Resource contentResource = page != null ? page.getContentResource() : null;
        StatusBuilder status = getStatusBuilder(contentResource);

        return status != null ? Collections.singletonList(status.build()) : Collections.emptyList();
    }

    private StatusBuilder getStatusBuilder(Resource contentResource) {
        // implementing the same logic as in /libs/mcm/campaign/components/status/status.jsp
        if (contentResource != null && contentResource.isResourceType(EmailPageImpl.RESOURCE_TYPE)) {
            Configuration wsConfig;
            String message = null;
            String snippet = null;
            String title = "Status";
            Status.Variant variant = EditorResourceStatus.Variant.INFO;

            // check if cloud service configuration is available
            try {
                wsConfig = connector.getWebserviceConfig(contentResource);
            } catch (ACConnectorException ex) {
                LOG.debug("Could not get adobe campaign webservice configuration for {}", contentResource.getPath(), ex);
                // return null to not show any status in this case (e.g. for AJO)
                return null;
            }

            try {
                connector.retrieveCredentials(wsConfig);
            } catch (ACConnectorException ex) {
                LOG.warn("Could not get adobe campaign webservice credentials for {}", contentResource.getPath(), ex);
                return newStatus(title, "Could not determine webservice credentials.", EditorResourceStatus.Variant.WARNING);
            }

            I18n i18n = new I18n(resourceBundleProvider.getResourceBundle(resourceBundleProvider.getDefaultLocale()));

            // status
            try {
                NewsletterStatus status = statusService.retrieveStatus(contentResource, i18n);
                int statusCode = status.getStatusCode();
                if (statusCode != NewsletterStatus.UNAVAILABLE) {
                    if (statusCode < 0) {
                        title = "Remote status";
                    }
                    message = status.getStatusMessage();
                }
                if (statusCode == NewsletterStatus.FAILED) {
                    variant = EditorResourceStatus.Variant.ERROR;
                } else if (statusCode == NewsletterStatus.MISSING || statusCode == NewsletterStatus.UNAVAILABLE) {
                    variant = EditorResourceStatus.Variant.WARNING;
                }

                if (message != null) {
                    return newStatus(title, message, variant);
                }
            } catch (ACConnectorException ex) {
                LOG.warn("Could not get status from adobe campaign for {}", contentResource.getPath(), ex);
            }

            // linking status
            try {
                LinkingStatus status = linkingStatusService.retrieveStatus(contentResource, i18n);
                String[] linked = status.getLinkedDeliveries();
                if (linked.length > 0) {
                    if (linked.length == 1) {
                        message = "Linked with delivery {0}";
                        snippet = linked[0];
                    } else {
                        message = "Linked with {0} deliveries";
                        snippet = Integer.toString(linked.length);
                    }
                    variant = EditorResourceStatus.Variant.INFO;
                }
                if (status.isApproved()) {
                    if (message == null) {
                        message = i18n.get("Approved");
                    } else {
                        message += i18n.get(" and approved");
                    }
                    variant = EditorResourceStatus.Variant.INFO;
                }
                if (message != null) {
                    return newStatus(title, message + ".", variant).addSnippet(snippet);
                }
            } catch (NewsletterException ex) {
                LOG.warn("Cloud not get linking status from adobe campaign for {}", contentResource.getPath(), ex);
            }
        }

        return null;
    }

    private StatusBuilder newStatus(String title, String msg, EditorResourceStatus.Variant variant) {
        return new StatusBuilder(getType(), title, msg).setVariant(variant);
    }

    private static class Status extends EditorResourceStatus {
        private final List<String> snippets;

        public Status(EditorResourceStatus status, List<String> snippets) {
            super(status.getType(), status.getTitle(), status.getMessage(), status.getPriority(), status.getVariant(), status.getIcon(),
                status.getActions(), status.getAdditionalData());
            this.snippets = snippets != null ? new ArrayList<>(snippets) : null;
        }

        @Nullable
        @Override
        public Map<String, Object> getData() {
            Map<String, Object> data = super.getData();

            if (data == null || data.isEmpty() || snippets == null) {
                return data;
            }

            // create a copy of the data and add the message snippets
            data = new HashMap<>(data);
            data.put("i18n.message.snippets", snippets);

            return Collections.unmodifiableMap(data);
        }
    }

    private static class StatusBuilder extends EditorResourceStatus.Builder {
        private List<String> snippets;

        public StatusBuilder(@NotNull String type, @NotNull String title, @NotNull String message) {
            super(type, title, message);
        }


        @NotNull
        @Override
        public CampaignStatusProvider.StatusBuilder setVariant(@Nullable EditorResourceStatus.Variant variant) {
            super.setVariant(variant);
            return this;
        }

        public StatusBuilder addSnippet(String snippet) {
            if (StringUtils.isNotEmpty(snippet)) {
                if (snippets == null) {
                    snippets = new ArrayList<>(1);
                }
                snippets.add(snippet);
            }
            return this;
        }

        @Override
        public EditorResourceStatus build() {
            return new Status(super.build(), snippets);
        }
    }

}
