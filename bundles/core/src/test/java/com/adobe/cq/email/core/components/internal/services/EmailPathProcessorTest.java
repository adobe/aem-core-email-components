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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import com.day.cq.wcm.scripting.WCMBindingsConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailPathProcessorTest {

    private final EmailPathProcessor subject = new EmailPathProcessor();
    private final PathProcessor defaultPathProcessor = mock(PathProcessor.class);
    private final SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);

    @BeforeEach
    void setup() {
        subject.defaultPathProcessor = defaultPathProcessor;
        when(defaultPathProcessor.sanitize(anyString(), any(SlingHttpServletRequest.class))).then(returnsFirstArg());
        when(defaultPathProcessor.map(anyString(), any(SlingHttpServletRequest.class))).then(returnsFirstArg());
        when(defaultPathProcessor.externalize(anyString(), any(SlingHttpServletRequest.class))).then(returnsFirstArg());
    }

    @Test
    void testAccepts_resourcePage() {
        Resource resource = mock(Resource.class);
        Resource nonExistingResource = mock(Resource.class);
        Resource contentResource = mock(Resource.class);
        Page resourcePage = mock(Page.class);
        PageManager pageManager = mock(PageManager.class);
        PageManagerFactory pageManagerFactory = mock(PageManagerFactory.class);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);

        subject.pageManagerFactory = pageManagerFactory;
        when(request.getResource()).thenReturn(resource);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.resolve(any(), any())).thenReturn(nonExistingResource);
        when(pageManagerFactory.getPageManager(any())).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(resourcePage);
        when(resourcePage.getContentResource()).thenReturn(contentResource);

        // no matching resourceType
        assertFalse(subject.accepts("/anypath", request));

        // matching resourceType
        when(contentResource.isResourceType("core/email/components/page/v1/page")).thenReturn(Boolean.TRUE);
        assertTrue(subject.accepts("/anypath", request));
    }

    @Test
    void testAccepts_currentPageFromRequestURI() {
        Resource resource = mock(Resource.class);
        Resource contentResource = mock(Resource.class);
        Page resourcePage = mock(Page.class);
        PageManager pageManager = mock(PageManager.class);
        PageManagerFactory pageManagerFactory = mock(PageManagerFactory.class);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);

        subject.pageManagerFactory = pageManagerFactory;
        when(request.getRequestURI()).thenReturn("/page/path.html");
        when(request.getResource()).thenReturn(resource);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resource.getPath()).thenReturn("/page/path");
        when(resourceResolver.resolve(request, "/page/path.html")).thenReturn(resource);
        when(pageManagerFactory.getPageManager(any())).thenReturn(pageManager);
        when(pageManager.getPage("/page/path")).thenReturn(resourcePage);
        when(resourcePage.getContentResource()).thenReturn(contentResource);

        // no matching resourceType
        assertFalse(subject.accepts("/anypath", request));

        // matching resourceType
        when(contentResource.isResourceType("core/email/components/page/v1/page")).thenReturn(Boolean.TRUE);
        assertTrue(subject.accepts("/anypath", request));
    }

    @Test
    void testAccepts_currentPageFromBindings() {
        SlingBindings bindings = new SlingBindings();
        Page currentPage = mock(Page.class);
        Resource contentResource = mock(Resource.class);

        bindings.put(WCMBindingsConstants.NAME_CURRENT_PAGE, currentPage);
        when(currentPage.getContentResource()).thenReturn(contentResource);
        when(request.getAttribute(SlingBindings.class.getName())).thenReturn(bindings);


        // no matching resourceType
        assertFalse(subject.accepts("/anypath", request));

        // matching resourceType
        when(contentResource.isResourceType("core/email/components/page/v1/page")).thenReturn(Boolean.TRUE);
        assertTrue(subject.accepts("/anypath", request));
    }

    @Test
    void testMaskExpressionLanguage() {
        Map<String, String> placeholders = new LinkedHashMap<>();
        String text = "before <%= recipient.uid %> has the following <%@ salutation %> after";
        String masked = EmailPathProcessor.mask(text, placeholders);

        assertNotEquals(text, masked);
        assertEquals(2, placeholders.size());
        Iterator<String> expressions = placeholders.values().iterator();
        assertEquals("<%= recipient.uid %>", expressions.next());
        assertEquals("<%@ salutation %>", expressions.next());
        assertFalse(masked.contains("<%") && masked.contains("%>"));

        String unmaksed = EmailPathProcessor.unmask(masked, placeholders);
        assertEquals(text, unmaksed);
    }

    @Test
    void testSanitize() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        String path = "/content/foo/<%= recipient.country %>/<%= recipient.language %>/unsubscribe.html?muid=<%= recipient.encrypted_id %>";
        String result = subject.sanitize(path, request);

        verify(defaultPathProcessor).sanitize(captor.capture(), eq(request));

        assertNotEquals(path, captor.getValue());
        assertFalse(captor.getValue().contains("<%") && captor.getValue().contains("%>"));
        assertEquals(path, result);
    }

    @Test
    void testMap() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        String path = "/content/foo/<%= recipient.country %>/<%= recipient.language %>/unsubscribe.html?muid=<%= recipient.encrypted_id %>";
        String result = subject.map(path, request);

        verify(defaultPathProcessor).map(captor.capture(), eq(request));

        assertNotEquals(path, captor.getValue());
        assertFalse(captor.getValue().contains("<%") && captor.getValue().contains("%>"));
        assertEquals(path, result);
    }

    @Test
    void testExternalize() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        String path = "/content/foo/<%= recipient.country %>/<%= recipient.language %>/unsubscribe.html?muid=<%= recipient.encrypted_id %>";
        String result = subject.externalize(path, request);

        verify(defaultPathProcessor).externalize(captor.capture(), eq(request));

        assertNotEquals(path, captor.getValue());
        assertFalse(captor.getValue().contains("<%") && captor.getValue().contains("%>"));
        assertEquals(path, result);
    }

}
