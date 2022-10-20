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
package com.adobe.cq.email.core.components.internal.filters;

import java.io.IOException;
import javax.servlet.ServletOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.internal.util.InlinerResponseWrapper;
import com.adobe.cq.mcm.campaign.NewsletterException;
import com.day.cq.mcm.campaign.ACConnectorException;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class InlinerResponseWrapperTest {
    private final AemContext ctx = new AemContext();

    @BeforeEach
    void setUp() throws ACConnectorException, NewsletterException {
        ctx.load().json("/content/TestPage.json", "/content");
        ctx.currentResource("/content/experiencepage/jcr:content");
        ctx.response().setCharacterEncoding("utf-8");
    }

    @Test
    void getOutputStreamTest() throws IOException {
        InlinerResponseWrapper wrapper = new InlinerResponseWrapper(ctx.response());
        ServletOutputStream stream = wrapper.getOutputStream();
        stream.setWriteListener(null);
        assertFalse(stream.isReady());
        stream.write(42);
        wrapper.flushBuffer();
        wrapper.getResponseAsBytes();
        assertNotEquals(null, wrapper.getResponseAsString());
        assertThrows(IllegalStateException.class, () -> {
            wrapper.getWriter();
        });
    }

    @Test
    void getWriterTest() throws IOException {
        InlinerResponseWrapper wrapper = new InlinerResponseWrapper(ctx.response());
        wrapper.getWriter();
        wrapper.flushBuffer();
        wrapper.getResponseAsBytes();
        assertNotEquals(null, wrapper.getResponseAsString());
        assertThrows(IllegalStateException.class, () -> {
            wrapper.getOutputStream();
        });
    }

}
