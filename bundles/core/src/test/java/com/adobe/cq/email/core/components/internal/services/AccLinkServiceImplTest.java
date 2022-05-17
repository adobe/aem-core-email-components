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

import java.net.URLEncoder;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.internal.services.AccLinkServiceImpl.AccLink;
import com.adobe.cq.email.core.components.services.UrlMapperService;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccLinkServiceImplTest {

    private static final String URL_WITHOUT_ACC_MARKUP = "/generic/relative/url/without/acc/markup";
    private static final String URL_WITH_ACC_MARKUP = "/generic/relative/url/with/acc/markup/<% recipient.name %>";
    private static final String ENCODED_URL_WITH_ACC_MARKUP = URLEncoder.encode(URL_WITH_ACC_MARKUP);
    private static final String ABSOLUTE_URL_WITHOUT_ACC_MARKUP = "http://server:8080" + URL_WITHOUT_ACC_MARKUP;
    private static final String ABSOLUTE_URL_WITH_ACC_MARKUP = "http://server:8080" + URL_WITH_ACC_MARKUP;
    private static final String ENCODED_ABSOLUTE_URL_WITH_ACC_MARKUP = "http://server:8080" + ENCODED_URL_WITH_ACC_MARKUP;


    @Mock
    UrlMapperService urlMapperService;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    PageManager pageManager;
    @Mock
    Page page;

    private AccLinkServiceImpl sut;

    @BeforeEach
    void setUp() {
        this.sut = new AccLinkServiceImpl();
        this.sut.urlMapperService = urlMapperService;
    }

    @Test
    void nullResourceResolver() {
        assertNull(this.sut.create(null, request, URL_WITHOUT_ACC_MARKUP));
    }

    @Test
    void nullRequest() {
        assertNull(this.sut.create(resourceResolver, null, URL_WITHOUT_ACC_MARKUP));
    }

    @Test
    void nullUrl() {
        assertNull(this.sut.create(resourceResolver, request, null));
    }

    @Test
    void emptyUrl() {
        assertNull(this.sut.create(resourceResolver, request, ""));
    }

    @Test
    void noUrlFromMapperService() {
        assertNull(this.sut.create(resourceResolver, request, URL_WITHOUT_ACC_MARKUP));
    }

    @Test
    void exceptionThrown() {
        when(urlMapperService.getMappedUrl(eq(resourceResolver), eq(request), eq(URL_WITHOUT_ACC_MARKUP))).thenThrow(
                new RuntimeException("Test exception"));
        assertNull(this.sut.create(resourceResolver, request, URL_WITHOUT_ACC_MARKUP));
    }

    @Test
    void urlWithoutAccMarkupNotReferringToAPage() {
        when(urlMapperService.getMappedUrl(eq(resourceResolver), eq(request), eq(URL_WITHOUT_ACC_MARKUP))).thenReturn(
                ABSOLUTE_URL_WITHOUT_ACC_MARKUP);
        Link expected = new AccLink<>(ABSOLUTE_URL_WITHOUT_ACC_MARKUP);
        Link actual = this.sut.create(resourceResolver, request, URL_WITHOUT_ACC_MARKUP);
        assertEquals(expected.isValid(), actual.isValid());
        assertEquals(expected.getURL(), actual.getURL());
        assertEquals(expected.getMappedURL(), actual.getMappedURL());
        assertEquals(expected.getExternalizedURL(), actual.getExternalizedURL());
        assertEquals(expected.getHtmlAttributes(), actual.getHtmlAttributes());
        assertEquals(expected.getReference(), actual.getReference());
        assertEquals(expected, actual);
    }

    @Test
    void urlWithoutAccMarkupReferringToAPage() {
        when(resourceResolver.adaptTo(eq(PageManager.class))).thenReturn(pageManager);
        when(pageManager.getPage(eq(URL_WITHOUT_ACC_MARKUP))).thenReturn(page);
        when(urlMapperService.getMappedUrl(eq(resourceResolver), eq(request), eq(URL_WITHOUT_ACC_MARKUP))).thenReturn(
                ABSOLUTE_URL_WITHOUT_ACC_MARKUP);
        Link expected = new AccLink<>(ABSOLUTE_URL_WITHOUT_ACC_MARKUP + ".html");
        Link actual = this.sut.create(resourceResolver, request,
                URL_WITHOUT_ACC_MARKUP);
        assertEquals(expected.isValid(), actual.isValid());
        assertEquals(expected.getURL(), actual.getURL());
        assertEquals(expected.getMappedURL(), actual.getMappedURL());
        assertEquals(expected.getExternalizedURL(), actual.getExternalizedURL());
        assertEquals(expected.getHtmlAttributes(), actual.getHtmlAttributes());
        assertEquals(expected.getReference(), actual.getReference());
        assertEquals(expected, actual);
    }

    @Test
    void urlWithAccMarkupNotReferringToAPage() {
        when(urlMapperService.getMappedUrl(eq(resourceResolver), eq(request), eq(URL_WITH_ACC_MARKUP))).thenReturn(
                ENCODED_ABSOLUTE_URL_WITH_ACC_MARKUP);
        Link expected = new AccLink<>(ABSOLUTE_URL_WITH_ACC_MARKUP);
        Link actual = this.sut.create(resourceResolver, request, URL_WITH_ACC_MARKUP);
        assertEquals(expected.isValid(), actual.isValid());
        assertEquals(expected.getURL(), actual.getURL());
        assertEquals(expected.getMappedURL(), actual.getMappedURL());
        assertEquals(expected.getExternalizedURL(), actual.getExternalizedURL());
        assertEquals(expected.getHtmlAttributes(), actual.getHtmlAttributes());
        assertEquals(expected.getReference(), actual.getReference());
        assertEquals(expected, actual);
    }

}