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
package com.adobe.cq.email.core.components.internal.models;


import com.adobe.cq.email.core.components.models.EmailPage;
import com.adobe.cq.mcm.campaign.LinkingStatus;
import com.adobe.cq.mcm.campaign.LinkingStatusService;
import com.adobe.cq.mcm.campaign.NewsletterException;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.xfa.ut.StringUtils;
import com.day.cq.i18n.I18n;
import com.day.cq.mcm.campaign.ACConnectorException;
import com.day.cq.mcm.campaign.CampaignConnector;
import com.day.cq.mcm.campaign.CampaignException;
import com.day.cq.mcm.campaign.NewsletterStatus;
import com.day.cq.mcm.campaign.StatusService;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.webservicesupport.Configuration;

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

/**
 * {@link EmailPage} implementation
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = {EmailPage.class}, resourceType = "core/email/components/page")
public class EmailPageImpl implements EmailPage {
    @ValueMapValue
    @Optional
    @Default(values = "")
    private String referenceUrl;

    @Self
    protected SlingHttpServletRequest request;

    @Inject
    protected Resource resource;

    @Inject
    private CampaignConnector connector;

    @Inject
    private ModelFactory modelFactory;

    @Inject
    private StatusService statusService;

    @Inject
    private LinkingStatusService linkingStatusService;

    private static final Logger LOG = LoggerFactory.getLogger(EmailPageImpl.class);

    private Page page;

    private String statusHeader = null;

    private String statusMessage = null;

    private String alertType = "info";

    private String errorMessage = null;

    private String errorMessageTech = null;

    private I18n i18n;

    @PostConstruct
    protected void initModel() {
        LOG.debug("Init model.");
        request.setAttribute("referenceUrl", referenceUrl);
        page = modelFactory.getModelFromWrappedRequest(request, resource, Page.class);
        i18n = new I18n(request);
        initConnector();
        if (!WCMMode.fromRequest(request).equals(WCMMode.DISABLED)) {
            setAlertType();
        }
        if (!StringUtils.isEmpty(errorMessageTech)) {
            LOG.warn(errorMessageTech);
        }
    }

    /**
     * Get the wcm core page.
     * @return current wcm core page
     */
    @Override
    public Page getPage() {
        return page;
    }

    /**
     * Get the reference url for links.
     * @return reference url
     */
    @Override
    public String getReferenceUrl() {
        return referenceUrl;
    }

    /**
     * Get the status header
     * @return status header
     */
    @Override
    public String getStatusHeader() {
        return statusHeader;
    }

    /**
     * Get the status message
     * @return status message
     */
    @Override
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Get the alert type
     * @return alert type
     */
    @Override
    public String getAlertType() {
        return alertType;
    }

    private void initConnector() {
        try {
            Configuration wsConfig = connector.getWebserviceConfig(request.getResource());
            LOG.debug("Webservice config is in place for resource {}.", request.getResource());
            try {
                connector.retrieveCredentials(wsConfig);
                LOG.debug("Credentials are set.");
            } catch (ACConnectorException ace) {
                errorMessage = i18n.get("Could not determine webservice credentials.");
                errorMessageTech = "Could not determine webservice credentials: " + ace.getMessage();
                LOG.warn("Error with credentials: {}", ace.getMessage());
            }
        } catch (ACConnectorException ex) {
            errorMessage = i18n.get("Missing or invalid Adobe Campaign webservice config.");
            errorMessageTech = "Missing or invalid Adobe Campaign webservice config: " + ex.getMessage();
            LOG.warn("Error webservice config: {}", ex.getMessage());
        }
    }

    /**
     * Set the alert type based on the values retrieved by the status service.
     */
    private void setAlertType() {
        statusHeader = i18n.get("Status");
        statusMessage = errorMessage;
        // status
        if (StringUtils.isEmpty(statusMessage)) {
            LOG.debug("Webservice working, checking document status now.");
            try {
                NewsletterStatus status = statusService.retrieveStatus(resource, i18n);
                int statusCode = status.getStatusCode();
                if (statusCode != NewsletterStatus.UNAVAILABLE) {
                    if (statusCode < 0) {
                        statusHeader = i18n.get("Remote status");
                    }
                    statusMessage = status.getStatusMessage();
                }
                if (statusCode == NewsletterStatus.FAILED) {
                    alertType = "error";
                } else if (statusCode == NewsletterStatus.MISSING || statusCode == NewsletterStatus.UNAVAILABLE) {
                    alertType = "notice";
                } else {
                    alertType = "info";
                }
                LOG.debug("NL status: {}, alertType: {}", status.getStatusCode(), alertType);
            } catch (CampaignException ex) {
                LOG.info("Campaign Exception: {}", ex.getMessage());
            }
        }
        if (StringUtils.isEmpty(statusMessage)) {
            LOG.debug("Status message is empty - analyzing further.");
            try {
                LinkingStatus status = linkingStatusService.retrieveStatus(resource, i18n);
                String[] linked = status.getLinkedDeliveries();
                if (linked.length > 0) {
                    if (linked.length == 1) {
                        statusMessage = i18n.getVar("Linked with delivery {0}", null, linked[0]);
                    } else {
                        statusMessage = i18n.getVar("Linked with {0} deliveries", null, Integer.toString(linked.length));
                    }
                    alertType = "info";
                } else {
                    LOG.debug("No linked deliveries.");
                }
                if (status.isApproved()) {
                    if (statusMessage == null) {
                        statusMessage = i18n.get("Approved");
                    } else {
                        statusMessage += i18n.get(" and approved");
                    }
                    alertType = "info";
                } else {
                    LOG.debug("No approval.");
                }
                if (statusMessage != null) {
                    statusMessage += ".";
                }
            } catch (NewsletterException ne) {
                LOG.info("Newsletter Exception {}", ne.getMessage());
            }
            LOG.debug("AlertType {}, Status Message {}, status header {}.", alertType, statusMessage, statusHeader);
        }

    }

}
