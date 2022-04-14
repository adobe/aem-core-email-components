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
import com.day.cq.mcm.campaign.ACConnectorException;
import com.day.cq.mcm.campaign.CampaignConnector;
import com.day.cq.mcm.campaign.NewsletterStatus;
import com.day.cq.mcm.campaign.StatusService;
import com.day.cq.wcm.api.WCMMode;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EmailPageImplTest {
    private final AemContext ctx = new AemContext();

    @Mock
    Page page;

    @Mock
    CampaignConnector connector;

    @Mock
    StatusService statusService;

    @Mock
    LinkingStatusService linkingStatusService;


    @Mock
    private ModelFactory modelFactory;

    @BeforeEach
    void setUp() throws ACConnectorException, NewsletterException {
        ctx.addModelsForClasses(EmailPageImpl.class, EmailPage.class);
        ctx.load().json("/content/TestPage.json", "/content");
        ctx.currentResource("/content/experiencepage/jcr:content");
        lenient().when(modelFactory.getModelFromWrappedRequest(eq(ctx.request()), any(Resource.class), eq(Page.class))).thenReturn(page);
        NewsletterStatus status = mock(NewsletterStatus.class);
        lenient().when(status.getStatusCode()).thenReturn(NewsletterStatus.FAILED, NewsletterStatus.UNAVAILABLE, NewsletterStatus.MISSING, 0);
        lenient().when(connector.getWebserviceConfig(any())).thenReturn(null);
        lenient().when(connector.retrieveCredentials(any())).thenReturn(null);
        lenient().when(statusService.retrieveStatus(any(), any())).thenReturn(status);
        LinkingStatus linkingStatus = new LinkingStatus(true, new String[] {"a"});
        lenient().when(linkingStatusService.retrieveStatus(any(), any())).thenReturn(linkingStatus, new LinkingStatus(true, new String[] {}));
        ctx.registerService(ModelFactory.class, modelFactory, org.osgi.framework.Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        ctx.registerService(LinkingStatusService.class, linkingStatusService, org.osgi.framework.Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        ctx.registerService(StatusService.class, statusService, org.osgi.framework.Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        ctx.registerService(CampaignConnector.class, connector, org.osgi.framework.Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        ctx.currentResource("/content/experiencepage/jcr:content");
    }

    @Test
    void initModel() {
        EmailPage emailPage = ctx.request().adaptTo(EmailPageImpl.class);
        assertNotNull(emailPage);
        assertNotNull(emailPage.getPage());
        assertEquals(emailPage.getReferenceUrl(), "https://www.test.dev");
    }

    @Test
    void forceExceptions() throws ACConnectorException {
        lenient().when(connector.retrieveCredentials(any())).thenThrow(ACConnectorException.class);
        EmailPage emailPage = ctx.request().adaptTo(EmailPageImpl.class);
        lenient().when(connector.getWebserviceConfig(any())).thenThrow(ACConnectorException.class);
        emailPage = ctx.request().adaptTo(EmailPageImpl.class);

    }

    @Test
    void initModelEditMode() {
        WCMMode.EDIT.toRequest(ctx.request());
        EmailPage emailPage = ctx.request().adaptTo(EmailPageImpl.class);
        assertNotNull(emailPage);
        assertEquals(emailPage.getAlertType(), "info");
        assertEquals(emailPage.getStatusHeader(), "Remote status");
        assertEquals(emailPage.getStatusMessage(), "Linked with delivery a and approved.");
        emailPage = ctx.request().adaptTo(EmailPageImpl.class);
        // again
        emailPage = ctx.request().adaptTo(EmailPageImpl.class);
        emailPage = ctx.request().adaptTo(EmailPageImpl.class);
    }

}
