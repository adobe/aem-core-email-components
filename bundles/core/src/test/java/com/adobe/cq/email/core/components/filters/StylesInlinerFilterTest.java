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
package com.adobe.cq.email.core.components.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.engine.SlingRequestProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.cq.email.core.components.internal.services.CoreEmailOpenCommand;
import com.adobe.cq.email.core.components.services.StylesInlinerService;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.drew.lang.Charsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StylesInlinerFilterTest {
    private static final String INPUT = "TEST_PAGE";
    private static final String OUTPUT_PROCESSING_CSS_SPECIFICITY = "TEST_PAGE_WITH_INLINED_CSS_USING_SPECIFICITY";
    private static final String OUTPUT_IGNORING_CSS_SPECIFICITY = "TEST_PAGE_WITH_INLINED_CSS_WITHOUT_USING_SPECIFICITY";
    private static final String OUTPUT_ALWAYS_APPENDING_CSS_PROPERTIES = "TEST_PAGE_WITH_INLINED_CSS_ALWAYS_APPENDING_CSS_PROPERTIES";

    @Mock
    FilterChain filterChain;
    @Mock
    RequestResponseFactory requestResponseFactory;
    @Mock
    SlingRequestProcessor requestProcessor;
    @Mock
    StylesInlinerService stylesInlinerService;
    @Mock
    Resource resource;
    @Mock
    Resource contentResource;
    @Mock
    ValueMap valueMap;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    SlingHttpServletResponse response;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    PrintWriter printWriter;

    private StylesInlinerFilter sut;

    @BeforeEach
    void setUp() throws IOException {
        this.sut = new StylesInlinerFilter();
    }

    @Test
    void noSelectors() throws ServletException, IOException {
        RequestPathInfo requestPathInfo = mock(RequestPathInfo.class);
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{});
        sut.doFilter(request, response, filterChain);
        verify(request).getRequestPathInfo();
        verify(filterChain).doFilter(eq(request), eq(response));
        verifyNoMoreInteractions(filterChain, request, response);
    }

    @Test
    void invalidContent() throws ServletException, IOException {
        RequestPathInfo requestPathInfo = mock(RequestPathInfo.class);
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"campaign", "content"});
        when(response.getWriter()).thenReturn(printWriter);
        when(response.getCharacterEncoding()).thenReturn(Charsets.UTF_8.name());
        sut.doFilter(request, response, filterChain);
        verify(printWriter).write(anyString());
        verify(request).getRequestPathInfo();
        verify(filterChain).doFilter(eq(request), isA(InlinerResponseWrapper.class));
        verify(response, atLeast(1)).getContentType();
        verify(response).getBufferSize();
        verifyNoMoreInteractions(filterChain, request, response);
    }

    @Test
    void invalidResourceType() throws IOException, ServletException {
        RequestPathInfo requestPathInfo = mock(RequestPathInfo.class);
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"campaign", "content"});
        when(response.getWriter()).thenReturn(printWriter);
        when(response.getCharacterEncoding()).thenReturn(Charsets.UTF_8.name());
        when(request.getResource()).thenReturn(resource);
        when(resource.isResourceType(eq("core/email/components/page/v1/page"))).thenReturn(false);
        when(response.getContentType()).thenReturn("application/json");
        sut.doFilter(request, response, filterChain);
        verify(printWriter).write(anyString());
        verify(request).getRequestPathInfo();
        verify(filterChain).doFilter(eq(request), isA(InlinerResponseWrapper.class));
        verify(response, atLeast(1)).getContentType();
        verify(response).getBufferSize();
        verifyNoMoreInteractions(filterChain, request, response);
    }

    @Test
    void success() throws IOException, ServletException {
        injectReferences();
        RequestPathInfo requestPathInfo = mock(RequestPathInfo.class);
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"campaign", "content"});
        when(response.getWriter()).thenReturn(printWriter);
        when(response.getCharacterEncoding()).thenReturn(Charsets.UTF_8.name());
        when(request.getResource()).thenReturn(resource);
        when(resource.isResourceType(eq("core/email/components/page/v1/page"))).thenReturn(true);
        when(response.getContentType()).thenReturn("application/json");
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(stylesInlinerService.getHtmlWithInlineStyles(eq(resourceResolver), anyString(), anyString())).thenReturn("RESULT");
        sut.doFilter(request, response, filterChain);
        verify(printWriter).write(eq("RESULT"));
        verify(request).getRequestPathInfo();
        verify(filterChain).doFilter(eq(request), isA(InlinerResponseWrapper.class));
        verify(response, atLeast(1)).getContentType();
        verify(response).getBufferSize();
        verifyNoMoreInteractions(filterChain, request, response);
    }

    private void injectReferences() {
        try {
            Field stylesInlinerServiceField = StylesInlinerFilter.class.getDeclaredField("stylesInlinerService");
            stylesInlinerServiceField.setAccessible(true);
            stylesInlinerServiceField.set(sut, stylesInlinerService);
        } catch (Throwable e) {
            throw new RuntimeException("Error!");
        }
    }
}
