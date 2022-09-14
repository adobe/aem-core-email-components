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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.Externalizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlMapperServiceImplTest {
    private static final String DOMAIN = "http://domain.com";
    private static final String CONTENT_PATH = "/test/content/image.jpg";

    @Mock
    Externalizer externalizer;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    SlingHttpServletRequest request;

    private UrlMapperServiceImpl sut;

    @BeforeEach
    void setUp() {
        this.sut = new UrlMapperServiceImpl();
        this.sut.externalizer = externalizer;
    }

    @Test
    void nullResourceResolver() {
        assertEquals(CONTENT_PATH, sut.getMappedUrl(null, request, CONTENT_PATH));
    }

    @Test
    void nullRequest() {
        assertEquals(CONTENT_PATH, sut.getMappedUrl(resourceResolver, null, CONTENT_PATH));
    }

    @Test
    void nullContentPath() {
        assertNull(sut.getMappedUrl(resourceResolver, request, null));
    }

    @Test
    void fromResourceResolver() {
        String resourceUrl = DOMAIN + CONTENT_PATH;
        String requestUri = "/test/current-content-page.html";
        StringBuffer requestUrl = new StringBuffer(DOMAIN).append(requestUri);
        when(request.getRequestURL()).thenReturn(requestUrl);
        when(request.getRequestURI()).thenReturn(requestUri);
        when(resourceResolver.map(any(), eq(CONTENT_PATH))).thenReturn(resourceUrl);
        assertEquals(resourceUrl, sut.getMappedUrl(resourceResolver, request, CONTENT_PATH));
    }

    @Test
    void fromExternalizer() {
        String resourceUrl = DOMAIN + CONTENT_PATH;
        String requestUri = "/test/current-content-page.html";
        StringBuffer requestUrl = new StringBuffer(DOMAIN).append(requestUri);
        when(request.getRequestURL()).thenReturn(requestUrl);
        when(request.getRequestURI()).thenReturn(requestUri);
        when(resourceResolver.map(any(), eq(CONTENT_PATH))).thenReturn(null);
        when(externalizer.externalLink(eq(resourceResolver), eq(Externalizer.PUBLISH), eq(CONTENT_PATH))).thenReturn(resourceUrl);
        assertEquals(resourceUrl, sut.getMappedUrl(resourceResolver, request, CONTENT_PATH));
    }

    @Test
    void exceptionThrownByBothMethods() {
        when(request.getRequestURL()).thenThrow(new RuntimeException("Error!"));
        when(externalizer.externalLink(eq(resourceResolver), eq(Externalizer.PUBLISH), eq(CONTENT_PATH))).thenThrow(new RuntimeException(
                "Error!"));
        assertEquals(CONTENT_PATH, sut.getMappedUrl(resourceResolver, request, CONTENT_PATH));
    }
}
