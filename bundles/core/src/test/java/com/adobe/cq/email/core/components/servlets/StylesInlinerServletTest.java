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
import java.lang.annotation.Annotation;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.engine.SlingRequestProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.config.StylesInlinerContextAwareConfiguration;
import com.adobe.cq.email.core.components.enumerations.HtmlSanitizingMode;
import com.adobe.cq.email.core.components.enumerations.StyleMergerMode;
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
    private static final String OUTPUT_PROCESSING_CSS_SPECIFICITY = "TEST_PAGE_WITH_INLINED_CSS_USING_SPECIFICITY";
    private static final String OUTPUT_IGNORING_CSS_SPECIFICITY = "TEST_PAGE_WITH_INLINED_CSS_WITHOUT_USING_SPECIFICITY";
    private static final String OUTPUT_ALWAYS_APPENDING_CSS_PROPERTIES = "TEST_PAGE_WITH_INLINED_CSS_ALWAYS_APPENDING_CSS_PROPERTIES";

    @Mock
    RequestResponseFactory requestResponseFactory;
    @Mock
    SlingRequestProcessor requestProcessor;
    @Mock
    StylesInlinerService stylesInlinerService;
    @Mock
    Resource resource;
    @Mock
    ConfigurationBuilder configurationBuilder;
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
        doAnswer(i -> {
            StyleMergerMode styleMergerMode = i.getArgument(2);
            if (StyleMergerMode.IGNORE_SPECIFICITY.equals(styleMergerMode)) {
                return OUTPUT_IGNORING_CSS_SPECIFICITY;
            }
            if (StyleMergerMode.ALWAYS_APPEND.equals(styleMergerMode)) {
                return OUTPUT_ALWAYS_APPENDING_CSS_PROPERTIES;
            }
            return OUTPUT_PROCESSING_CSS_SPECIFICITY;
        }).when(stylesInlinerService).getHtmlWithInlineStyles(eq(resourceResolver), eq(INPUT),
                any(), any());
    }

    @Test
    void noConfig() throws ServletException, IOException {
        when(resource.adaptTo(eq(ConfigurationBuilder.class))).thenReturn(null);
        sut.doGet(request, resp);
        verify(printWriter).write(eq(OUTPUT_PROCESSING_CSS_SPECIFICITY));
    }

    @Test
    void noStyleMergerMode() throws ServletException, IOException {
        when(resource.adaptTo(eq(ConfigurationBuilder.class))).thenReturn(configurationBuilder);
        when(configurationBuilder.as(StylesInlinerContextAwareConfiguration.class)).thenReturn(create(null));
        sut.doGet(request, resp);
        verify(printWriter).write(eq(OUTPUT_PROCESSING_CSS_SPECIFICITY));
    }

    @Test
    void processingCssSpecificity() throws Exception {
        when(resource.adaptTo(eq(ConfigurationBuilder.class))).thenReturn(configurationBuilder);
        when(configurationBuilder.as(StylesInlinerContextAwareConfiguration.class)).thenReturn(create(StyleMergerMode.PROCESS_SPECIFICITY));
        sut.doGet(request, resp);
        verify(printWriter).write(eq(OUTPUT_PROCESSING_CSS_SPECIFICITY));
    }

    @Test
    void ignoringCssSpecificity() throws Exception {
        when(resource.adaptTo(eq(ConfigurationBuilder.class))).thenReturn(configurationBuilder);
        when(configurationBuilder.as(StylesInlinerContextAwareConfiguration.class)).thenReturn(create(StyleMergerMode.IGNORE_SPECIFICITY));
        sut.doGet(request, resp);
        verify(printWriter).write(eq(OUTPUT_IGNORING_CSS_SPECIFICITY));
    }

    @Test
    void alwaysAppendingCssProperties() throws Exception {
        when(resource.adaptTo(eq(ConfigurationBuilder.class))).thenReturn(configurationBuilder);
        when(configurationBuilder.as(StylesInlinerContextAwareConfiguration.class)).thenReturn(create(StyleMergerMode.ALWAYS_APPEND));
        sut.doGet(request, resp);
        verify(printWriter).write(eq(OUTPUT_ALWAYS_APPENDING_CSS_PROPERTIES));
    }

    private StylesInlinerContextAwareConfiguration create(StyleMergerMode mode) {
        return new StylesInlinerContextAwareConfiguration() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return StylesInlinerContextAwareConfiguration.class;
            }

            @Override
            public StyleMergerMode stylesMergingMode() {
                return mode;
            }

            @Override
            public HtmlSanitizingMode htmlSanitizingMode() {
                return HtmlSanitizingMode.FULL;
            }
        };
    }
}