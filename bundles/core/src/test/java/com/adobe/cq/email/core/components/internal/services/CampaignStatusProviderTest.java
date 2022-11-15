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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.adobe.cq.email.core.components.internal.models.EmailPageImpl;
import com.adobe.cq.mcm.campaign.LinkingStatus;
import com.adobe.cq.mcm.campaign.LinkingStatusService;
import com.adobe.cq.mcm.campaign.NewsletterException;
import com.adobe.cq.wcm.core.components.testing.MockExternalizerFactory;
import com.adobe.granite.resourcestatus.ResourceStatus;
import com.day.cq.commons.Externalizer;
import com.day.cq.mcm.campaign.ACConnectorException;
import com.day.cq.mcm.campaign.CampaignConnector;
import com.day.cq.mcm.campaign.NewsletterStatus;
import com.day.cq.mcm.campaign.StatusService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins.CORE_COMPONENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({ AemContextExtension.class })
public class CampaignStatusProviderTest {

    public final AemContext context = new AemContextBuilder()
        .beforeSetUp(context -> {
            context.registerService(Externalizer.class, MockExternalizerFactory.getExternalizerService());
        })
        .plugin(CORE_COMPONENTS)
        .build();

    final LinkingStatusService linkingStatusService = mock(LinkingStatusService.class);
    final StatusService statusService = mock(StatusService.class);
    final CampaignConnector campaignConnector = mock(CampaignConnector.class);
    final CampaignStatusProvider subject = new CampaignStatusProvider();

    Page page;

    @BeforeEach
    void setup() {
        context.registerService(CampaignConnector.class, campaignConnector);
        context.registerService(LinkingStatusService.class, linkingStatusService);
        context.registerService(StatusService.class, statusService);

        context.registerInjectActivateService(subject);

        page = context.create().page("/content/campaigns/brand/master/campaign/email", "email", ImmutableMap.of(
            "sling:resourceType", EmailPageImpl.RESOURCE_TYPE
        ));
    }

    @Test
    void testReturnsNothingForNonEmailPages() {
        Page page = context.create().page("/path/to/page");
        testSubject(page);
    }

    @Test
    void testReturnsNothingWithNotCloudService() throws ACConnectorException {
        doThrow(ACConnectorException.class).when(campaignConnector).getWebserviceConfig(any());

        testSubject();
    }

    @Test
    void testReturnsWarningWithUnavailableCredentials() throws ACConnectorException {
        doReturn(mock(Configuration.class)).when(campaignConnector).getWebserviceConfig(any());
        doThrow(ACConnectorException.class).when(campaignConnector).retrieveCredentials(any());

        testSubject(new Expectation("Status", "Could not determine webservice credentials.", "warning"));
    }

    @Test
    void testReturnsNothingIfUnavailable() throws ACConnectorException, NewsletterException {
        setupStatus(NewsletterStatus.UNAVAILABLE, null);
        testSubject();
    }

    @Test
    void testReturnsErrorIfFailed() throws ACConnectorException, NewsletterException {
        setupStatus(NewsletterStatus.FAILED, "Expected");
        testSubject(new Expectation("Remote status", "Expected", "error"));
    }

    @Test
    void testReturnsInfoIfPreparing() throws ACConnectorException, NewsletterException {
        setupStatus(NewsletterStatus.PREPARING, "Expected");
        testSubject(new Expectation("Remote status", "Expected", "info"));
    }

    @Test
    void testReturnsWarningIfMissing() throws ACConnectorException, NewsletterException {
        setupStatus(NewsletterStatus.MISSING, "Expected");
        testSubject(new Expectation("Remote status", "Expected", "warning"));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "Linked with delivery {0}.;false;DM1",
        "Linked with delivery {0} and approved.;true;DM1",
        "Linked with {0} deliveries.;false;DM1,DM2",
        "Linked with {0} deliveries and approved.;true;DM1,DM2,DM3",
        "Approved.;true;",
    }, delimiter = ';')
    void testReturnsLinkedDeliveries(String expected, boolean approved, String deliveries) throws ACConnectorException, NewsletterException {
        Expectation expectation = new Expectation("Status", expected, "info");

        if (StringUtils.isNotEmpty(deliveries)) {
            String[] arr = deliveries.split(",");
            setupStatus(NewsletterStatus.UNAVAILABLE, "Expected", approved, arr);
            if (arr.length == 1) {
                expectation.placeholder = arr[0];
            } else {
                expectation.placeholder = Integer.toString(arr.length);
            }
        } else {
            setupStatus(NewsletterStatus.UNAVAILABLE, "Expected", approved);
        }

        testSubject(expectation);
    }

    @Test
    void testReturnsNothingIfNotApprovedAndNotLinked() throws ACConnectorException, NewsletterException {
        setupStatus(NewsletterStatus.UNAVAILABLE, null, false);
        testSubject();
    }

    private void setupStatus(int code, String message) throws ACConnectorException, NewsletterException {
        setupStatus(code, message, false);
    }

    private void setupStatus(int code, String message, boolean approved, String... deliveries)
        throws ACConnectorException, NewsletterException {
        NewsletterStatus status = mock(NewsletterStatus.class);
        when(status.getStatusCode()).thenReturn(code);
        when(status.getStatusMessage()).thenReturn(message);
        when(statusService.retrieveStatus(any(), any())).thenReturn(status);
        LinkingStatus linkingStatus = new LinkingStatus(approved, deliveries);
        when(linkingStatusService.retrieveStatus(any(), any())).thenReturn(linkingStatus);
    }

    private void testSubject(Expectation... expectations) {
        testSubject(page, expectations);
    }

    private void testSubject(Page target, Expectation... expectations) {
        List<ResourceStatus> statuses = subject.getStatuses(target.getContentResource());

        if (expectations.length == 0) {
            assertTrue(statuses.isEmpty());
        } else {
            assertEquals(expectations.length, statuses.size());
            for (int i = 0; i < expectations.length; i++) {
                Expectation expectation = expectations[i];
                Map<String, Object> status = statuses.get(i).getData();
                assertEquals(expectation.title, status.get("title"));
                assertEquals(expectation.message, status.get("message"));
                assertEquals(expectation.variant, status.get("variant"));
                if (expectation.placeholder != null) {
                    assertNotNull(status.get("i18n.message.snippets"));
                    assertTrue(status.get("i18n.message.snippets") instanceof List);
                    List snippets = (List) status.get("i18n.message.snippets");
                    assertEquals(1, snippets.size());
                    assertEquals(expectation.placeholder, snippets.get(0));
                }
            }
        }
    }

    private static class Expectation {

        private String title;
        private String message;
        private String variant;
        private String placeholder;

        public Expectation(String title, String message, String variant) {
            this.title = title;
            this.message = message;
            this.variant = variant;
        }
    }
}
