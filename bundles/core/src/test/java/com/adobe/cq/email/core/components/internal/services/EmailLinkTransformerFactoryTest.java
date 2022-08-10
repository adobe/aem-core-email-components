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

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.rewriter.ProcessingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsArgAt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailLinkTransformerFactoryTest {

    private final UrlMapperServiceImpl urlMapperService = mock(UrlMapperServiceImpl.class);
    private final SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
    private final RequestPathInfo requestPathInfo = mock(RequestPathInfo.class);
    private final ContentHandler contentHandler = mock(ContentHandler.class);
    private final ProcessingContext processingContext = mock(ProcessingContext.class);
    private final EmailPathProcessor emailPathProcessor = mock(EmailPathProcessor.class);
    private EmailLinkTransformerFactory.TransformerImpl subject;

    @BeforeEach
    void setup() throws IOException {
        EmailLinkTransformerFactory factory = new EmailLinkTransformerFactory();
        factory.urlMapperService = urlMapperService;
        factory.emailPathProcessor = emailPathProcessor;

        when(urlMapperService.getMappedUrl(any(), any(), any())).then(returnsArgAt(2));
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectorString()).thenReturn("campaign.content");
        when(processingContext.getRequest()).thenReturn(request);
        when(emailPathProcessor.isEmailPageRequest(any())).thenReturn(Boolean.TRUE);

        subject = factory.createTransformer();
        subject.setContentHandler(contentHandler);
        subject.init(processingContext, null);
    }

    @Test
    public void testExternalizationEnabled() throws IOException {
        // init is always called in setup() with the right selector string
        assertTrue(subject.externalizationEnabled);

        when(requestPathInfo.getSelectorString()).thenReturn("");
        subject.init(processingContext, null);
        assertFalse(subject.externalizationEnabled);
    }

    @Test
    public void testEnabled() throws IOException {
        // init is always called in setup() assuming the request is an email page request
        assertTrue(subject.enabled);

        when(emailPathProcessor.isEmailPageRequest(any())).thenReturn(Boolean.FALSE);
        subject.init(processingContext, null);
        assertFalse(subject.enabled);
    }

    @ParameterizedTest
    @CsvSource({
        "%3C%= targetData.link%20%>, &lt;%= targetData.link %&gt;, false",
        "&lt;%@ unsubscribeLink %&gt;, &lt;%@ unsubscribeLink %&gt;, false",
        "/path/to/page.html?recipient=%3C%= recipient.id%20%>, /path/to/page.html?recipient=&lt;%= recipient.id %&gt;, true",
        "/path/to/&lt;%=recipient.country%&gt;/&lt;%=recipient.language%&gt;/page.html, /path/to/&lt;%=recipient.country%&gt;/&lt;%=recipient.language%&gt;/page.html, true",
        "/path/to/page.html#&lt;%=recipient.country%&gt;-&lt;%=recipient.language%&gt;, /path/to/page.html#&lt;%=recipient.country%&gt;-&lt;%=recipient.language%&gt;, true",
    })
    public void testLinkRewrite(String givenHref, String expectedHref, boolean externalizationCalled) throws SAXException {
        AttributesImpl attributes = new AttributesImpl();

        if (givenHref != null) {
            attributes.addAttribute("", "href", "href", "CDATA", givenHref);
        }

        subject.startElement("", "a", "a", attributes);

        if (expectedHref != null) {
            assertEquals(expectedHref, attributes.getValue("href"));
        } else {
            assertEquals(-1, attributes.getIndex("href"));
        }

        if (StringUtils.isNotEmpty(givenHref)) {
            assertEquals(EmailLinkTransformerFactory.LINK_CHECKER_ATTR_SKIP, attributes.getValue(EmailLinkTransformerFactory.LINK_CHECKER_ATTR));
        } else {
            assertNull(attributes.getValue(EmailLinkTransformerFactory.LINK_CHECKER_ATTR));
        }

        verify(urlMapperService, externalizationCalled ? times(1) : never()).getMappedUrl(any(), any(), any());
    }

    @Test
    public void testLinkRewriterWithExternalizationDisabled() throws IOException, SAXException {
        // test that the decoding/re-encoding works but the externalization is not called
        when(requestPathInfo.getSelectorString()).thenReturn("");
        subject.init(processingContext,null);

        testLinkRewrite(
            "/path/to/page.html?recipient=%3C%= recipient.id%20%>",
            "/path/to/page.html?recipient=&lt;%= recipient.id %&gt;",
            false);
    }

    @Test
    public void testLinkRewriteEmptyHref() throws SAXException {
        testLinkRewrite("", "", false);
        testLinkRewrite(null, null, false);
    }
}
