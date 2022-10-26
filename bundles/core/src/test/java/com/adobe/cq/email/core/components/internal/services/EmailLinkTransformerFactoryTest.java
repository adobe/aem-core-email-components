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
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.adobe.cq.email.core.components.internal.models.EmailPageImpl;
import com.adobe.cq.wcm.core.components.testing.MockExternalizerFactory;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManagerFactory;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins.CORE_COMPONENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ AemContextExtension.class })
public class EmailLinkTransformerFactoryTest {

    private final ContentHandler contentHandler = mock(ContentHandler.class);
    private final ProcessingContext processingContext = mock(ProcessingContext.class);
    public final AemContext context = new AemContextBuilder()
        .beforeSetUp(context -> {
            context.registerService(Externalizer.class, MockExternalizerFactory.getExternalizerService());
        })
        .plugin(CORE_COMPONENTS)
        .build();
    private EmailLinkTransformerFactory.TransformerImpl subject;
    private Page emailPage;
    private MockRequestPathInfo requestPathInfo;

    @BeforeEach
    void setup() throws IOException {
        emailPage = context.create().page(
            "/content/campaigns/email-page",
            "",
            "sling:resourceType", EmailPageImpl.RESOURCE_TYPE);

        context.currentPage(emailPage);
        context.registerService(PageManagerFactory.class, rr -> context.pageManager());

        requestPathInfo = (MockRequestPathInfo) context.request().getRequestPathInfo();
        requestPathInfo.setSelectorString("campaign.content");

        when(processingContext.getRequest()).thenReturn(context.request());

        EmailLinkTransformerFactory factory = context.registerInjectActivateService(new EmailLinkTransformerFactory());
        subject = factory.createTransformer();
        subject.setContentHandler(contentHandler);
        subject.init(processingContext, null);
    }

    @Test
    public void testEnabled() throws IOException {
        // setup() enables the transformer
        assertTrue(subject.externalizationEnabled);
        assertTrue(subject.enabled);
    }

    @Test
    public void testDisabled() throws IOException {
        context.currentPage(context.create().page("/any/other/page"));
        requestPathInfo.setSelectorString("");
        subject.init(processingContext, null);
        assertFalse(subject.externalizationEnabled);
        assertFalse(subject.enabled);
    }

    @ParameterizedTest
    @CsvSource({
        "%3C%= targetData.link%20%>, &lt;%= targetData.link %&gt;",
        "&lt;%@ unsubscribeLink %&gt;, &lt;%@ unsubscribeLink %&gt;",
        "/path/to/page.html?recipient=%3C%= recipient.id%20%>, https://example.org/path/to/page.html?recipient=&lt;%= recipient.id %&gt;",
        "/path/to/&lt;%=recipient.country%&gt;/&lt;%=recipient.language%&gt;/page.html, https://example.org/path/to/&lt;%=recipient.country%&gt;/&lt;%=recipient.language%&gt;/page.html",
        "/path/to/page.html#&lt;%=recipient.country%&gt;-&lt;%=recipient.language%&gt;, https://example.org/path/to/page.html#&lt;%=recipient.country%&gt;-&lt;%=recipient.language%&gt;",
    })
    public void testLinkRewrite(String givenHref, String expectedHref) throws SAXException {
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

        verify(contentHandler, times(1)).startElement(any(),any(), any(), any());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "src; /path/to/image.png; https://example.org/path/to/image.png",
        "srcset; /path/to/image-480.png 480px, /path/to/image.png 640px; https://example.org/path/to/image-480.png 480px, https://example.org/path/to/image.png 640px",
        "srcset; /path/to/image.png, /path/to/image-2x.png 2x; https://example.org/path/to/image.png, https://example.org/path/to/image-2x.png 2x",
    }, delimiter = ';')
    public void testImageRewrite(String attr, String given, String expected) throws IOException, SAXException {
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", attr, attr, "CDATA", given);

        subject.startElement("", "img", "img", attributes);

        assertEquals(expected, attributes.getValue(attr));

        verify(contentHandler, times(1)).startElement(any(),any(), any(), any());
    }

    @Test
    public void testLinkRewriteWithExternalizationDisabled() throws IOException, SAXException {
        // test that the decoding/re-encoding works but the externalization is not called
        requestPathInfo.setSelectorString("");
        subject.init(processingContext,null);

        testLinkRewrite(
            "/path/to/page.html?recipient=%3C%= recipient.id%20%>",
            "/path/to/page.html?recipient=&lt;%= recipient.id %&gt;");
    }

    @Test
    public void testLinkRewriteEmptyHref() throws SAXException {
        testLinkRewrite("", "");
    }

    @Test
    public void testLinkRewriteNoHref() throws SAXException {
        testLinkRewrite(null, null);
    }
}
