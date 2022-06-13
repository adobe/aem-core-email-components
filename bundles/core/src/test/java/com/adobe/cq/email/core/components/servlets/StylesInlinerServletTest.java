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
package com.adobe.cq.email.core.components.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.services.StylesInlinerService;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.drew.lang.Charsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StylesInlinerServletTest {
    private static final String INPUT = "TEST_PAGE";
    private static final String OUTPUT = "TEST_PAGE_WITH_INLINED_CSS";

    @Mock
    RequestResponseFactory requestResponseFactory;
    @Mock
    SlingRequestProcessor requestProcessor;
    @Mock
    StylesInlinerService stylesInlinerService;
    @Mock
    Resource resource;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    SlingHttpServletResponse resp;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    PrintWriter printWriter;

    private StylesInlinerServlet sut;

    @BeforeEach
    void setUp() throws IOException {
        this.sut = new StylesInlinerServlet();
        this.sut.setRequestResponseFactory(requestResponseFactory);
        this.sut.setRequestProcessor(requestProcessor);
        this.sut.setStylesInlinerService(
                stylesInlinerService
        );
        when(request.getResource()).thenReturn(resource);
        when(resource.getPath()).thenReturn("TEST_PATH");
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(requestResponseFactory.createRequest(eq("GET"), eq("TEST_PATH.html"), anyMap())).thenReturn(httpServletRequest);
        doAnswer(i -> {
            OutputStream outputStream = (OutputStream) i.getArguments()[0];
            IOUtils.write(INPUT, outputStream, Charsets.UTF_8);
            return mock(HttpServletResponse.class);
        }).when(requestResponseFactory).createResponse(any());
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resp.getWriter()).thenReturn(printWriter);
        when(stylesInlinerService.getHtmlWithInlineStyles(eq(resourceResolver), eq(INPUT))).thenReturn(OUTPUT);
    }

    @Test
    void success() throws Exception {
        sut.doGet(request, resp);
        verify(printWriter).write(eq(OUTPUT));
    }

}