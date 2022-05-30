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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HtmlResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.wcm.api.AuthoringUIModeService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.commands.WCMCommandContext;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreEmailOpenCommandTest {
    private static final String EDITOR_URL = "/some/url";
    @Mock
    private AuthoringUIModeService authoringUIModeService;
    @Mock
    WCMCommandContext ctx;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    SlingHttpServletResponse response;
    @Mock
    PageManager pageManager;
    private CoreEmailOpenCommand sut;

    @BeforeEach
    void setUp() {
        this.sut = new CoreEmailOpenCommand();
        this.sut.bindAuthoringUIModeService(authoringUIModeService);
    }

    @AfterEach
    void tearDown() {
        this.sut.unbindAuthoringUIModeService(authoringUIModeService);
    }

    @Test
    void getCommandName() {
        assertEquals("open", sut.getCommandName());
    }

    @Test
    void performCommand_Exception() {
        when(request.getParameter("path")).thenThrow(new RuntimeException("TEST_EXCEPTION"));
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertEquals(500, htmlResponse.getStatusCode());
    }

    @Test
    void performCommand_NoPath() {
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertEquals(500, htmlResponse.getStatusCode());
    }

    @Test
    void performCommand_NoResource() throws IOException {
        when(request.getParameter("path")).thenReturn("/resource/path/with/some/params/and.scaffolding?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertNull(htmlResponse);
        verify(response).sendError(eq(404));
    }

    @Test
    void performCommand_Direct_NoJsonMode_DefaultViewFromChildValueMap() {
        when(request.getContextPath()).thenReturn("https://server:port");
        when(request.getParameter("path")).thenReturn("/content/campaigns/resource/path/with/some/params?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/content/campaigns/resource/path/with/some/params"))).thenReturn(resource);
        ResourceResolver innerResourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(innerResourceResolver);
        Resource jcrContentRes = mock(Resource.class);
        when(innerResourceResolver.getResource(eq(resource), eq("jcr:content"))).thenReturn(jcrContentRes);
        ValueMap valueMap = mock(ValueMap.class);
        when(jcrContentRes.adaptTo(eq(ValueMap.class))).thenReturn(valueMap);
        when(valueMap.get(eq("cq:defaultView"), eq(String.class))).thenReturn("direct");
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertNull(htmlResponse);
        verify(response).setHeader(eq("Location"),
                eq("https://server:port/content/campaigns/resource/path/with/some/params?param1=1&param2=2"));
        verify(response).setStatus(eq(302));
    }

    @Test
    void performCommand_ContentFinder_NoJsonMode_DefaultViewFromChildValueMap() {
        when(request.getContextPath()).thenReturn("https://server:port");
        when(request.getParameter("path")).thenReturn("/content/campaigns/resource/path/with/some/params?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/content/campaigns/resource/path/with/some/params"))).thenReturn(resource);
        Resource jcrContentResource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/content/campaigns/resource/path/with/some/params/jcr:content"))).thenReturn(
                jcrContentResource);
        doAnswer(i -> "core/email/components/page/v1/page".equals(i.getArgument(0))).when(jcrContentResource).isResourceType(anyString());
        when(jcrContentResource.isResourceType(eq("core/email/components/page/v1/page"))).thenReturn(true);
        ResourceResolver innerResourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(innerResourceResolver);
        Resource jcrContentRes = mock(Resource.class);
        when(innerResourceResolver.getResource(eq(resource), eq("jcr:content"))).thenReturn(jcrContentRes);
        ValueMap valueMap = mock(ValueMap.class);
        when(jcrContentRes.adaptTo(eq(ValueMap.class))).thenReturn(valueMap);
        when(valueMap.get(eq("cq:defaultView"), eq(String.class))).thenReturn("contentfinder");
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertNull(htmlResponse);
        verify(response).setHeader(eq("Location"),
                eq("https://server:port/content/campaigns/resource/path/with/some/params.html?param1=1&param2=2"));
        verify(response).setStatus(eq(302));
    }

    @Test
    void performCommand_ContentFinder_NoJsonMode_DefaultViewFromComponentChild() {
        when(request.getContextPath()).thenReturn("https://server:port");
        when(request.getParameter("path")).thenReturn("/content/campaigns/resource/path/with/some/params?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/content/campaigns/resource/path/with/some/params"))).thenReturn(resource);
        Resource jcrContentResource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/content/campaigns/resource/path/with/some/params/jcr:content"))).thenReturn(
                jcrContentResource);
        doAnswer(i -> "core/email/components/page/v1/page".equals(i.getArgument(0))).when(jcrContentResource).isResourceType(anyString());
        when(jcrContentResource.isResourceType(eq("core/email/components/page/v1/page"))).thenReturn(true);
        ResourceResolver innerResourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(innerResourceResolver);
        Resource jcrContentRes = mock(Resource.class);
        when(innerResourceResolver.getResource(eq(resource), eq("jcr:content"))).thenReturn(jcrContentRes);
        ValueMap valueMap = mock(ValueMap.class);
        when(jcrContentRes.adaptTo(eq(ValueMap.class))).thenReturn(valueMap);
        when(jcrContentRes.getResourceResolver()).thenReturn(innerResourceResolver);
        ComponentManager componentManager = mock(ComponentManager.class);
        when(innerResourceResolver.adaptTo(ComponentManager.class)).thenReturn(componentManager);
        Component component = mock(Component.class);
        when(componentManager.getComponentOfResource(eq(jcrContentRes))).thenReturn(component);
        when(component.getDefaultView()).thenReturn("contentfinder");
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertNull(htmlResponse);
        verify(response).setHeader(eq("Location"),
                eq("https://server:port/content/campaigns/resource/path/with/some/params.html?param1=1&param2=2"));
        verify(response).setStatus(eq(302));
    }

    @Test
    void performCommand_ContentFinder_NoJsonMode_DefaultViewFromCurrentNodeProp() {
        when(request.getContextPath()).thenReturn("https://server:port");
        when(request.getParameter("path")).thenReturn("/content/campaigns/resource/path/with/some/params?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/content/campaigns/resource/path/with/some/params"))).thenReturn(resource);
        Resource jcrContentResource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/content/campaigns/resource/path/with/some/params/jcr:content"))).thenReturn(
                jcrContentResource);
        doAnswer(i -> "core/email/components/page/v1/page".equals(i.getArgument(0))).when(jcrContentResource).isResourceType(anyString());
        when(jcrContentResource.isResourceType(eq("core/email/components/page/v1/page"))).thenReturn(true);
        ResourceResolver innerResourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(innerResourceResolver);
        ValueMap valueMap = mock(ValueMap.class);
        when(resource.adaptTo(eq(ValueMap.class))).thenReturn(valueMap);
        when(valueMap.get(eq("cq:defaultView"), eq(String.class))).thenReturn("contentfinder");
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertNull(htmlResponse);
        verify(response).setHeader(eq("Location"),
                eq("https://server:port/content/campaigns/resource/path/with/some/params.html?param1=1&param2=2"));
        verify(response).setStatus(eq(302));
    }

    @Test
    void performCommand_ContentFinder_NoJsonMode_DefaultViewFromCurrentNodeComponent() {
        when(request.getContextPath()).thenReturn("https://server:port");
        when(request.getParameter("path")).thenReturn("/content/campaigns/resource/path/with/some/params?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/content/campaigns/resource/path/with/some/params"))).thenReturn(resource);
        Resource jcrContentResource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/content/campaigns/resource/path/with/some/params/jcr:content"))).thenReturn(
                jcrContentResource);
        doAnswer(i -> "core/email/components/page/v1/page".equals(i.getArgument(0))).when(jcrContentResource).isResourceType(anyString());
        when(jcrContentResource.isResourceType(eq("core/email/components/page/v1/page"))).thenReturn(true);
        ResourceResolver innerResourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(innerResourceResolver);
        ValueMap valueMap = mock(ValueMap.class);
        when(resource.adaptTo(eq(ValueMap.class))).thenReturn(valueMap);
        when(resource.getResourceResolver()).thenReturn(innerResourceResolver);
        ComponentManager componentManager = mock(ComponentManager.class);
        when(innerResourceResolver.adaptTo(ComponentManager.class)).thenReturn(componentManager);
        Component component = mock(Component.class);
        when(componentManager.getComponentOfResource(eq(resource))).thenReturn(component);
        when(component.getDefaultView()).thenReturn("contentfinder");
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertNull(htmlResponse);
        verify(response).setHeader(eq("Location"),
                eq("https://server:port/content/campaigns/resource/path/with/some/params.html?param1=1&param2=2"));
        verify(response).setStatus(eq(302));
    }

    @Test
    void performCommand_TemplateEditor_NoJsonMode_PageParentAuthored() {
        when(authoringUIModeService.getEditorURL(any())).thenReturn(EDITOR_URL);
        when(request.getContextPath()).thenReturn("https://server:port");
        when(request.getParameter("path")).thenReturn("/content/campaigns/resource/path/with/some/params?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/content/campaigns/resource/path/with/some/params"))).thenReturn(resource);
        ResourceResolver innerResourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(innerResourceResolver);
        Page page = mock(Page.class);
        doAnswer(i -> i.getArgument(0).equals(Page.class) ? page : null).when(resource).adaptTo(any());
        Resource parentRes = mock(Resource.class);
        when(resource.getParent()).thenReturn(parentRes);
        when(parentRes.getChild(eq("initial"))).thenReturn(mock(Resource.class));
        when(parentRes.getChild(eq("structure"))).thenReturn(mock(Resource.class));
        when(parentRes.getChild(eq("policies"))).thenReturn(mock(Resource.class));
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertNull(htmlResponse);
        verify(response).setHeader(eq("Location"),
                eq("https://server:port/some/url/content/campaigns/resource/path/with/some/params.html?param1=1&param2=2"));
        verify(response).setStatus(eq(302));
    }

    @Test
    void performCommand_ContentFinder_NoJsonMode_PageNorParentAuthored() {
        when(authoringUIModeService.getEditorURL(any())).thenReturn(EDITOR_URL);
        when(request.getContextPath()).thenReturn("https://server:port");
        when(request.getParameter("path")).thenReturn("/content/campaigns/resource/path/with/some/params?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/content/campaigns/resource/path/with/some/params"))).thenReturn(resource);
        ResourceResolver innerResourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(innerResourceResolver);
        Page page = mock(Page.class);
        doAnswer(i -> i.getArgument(0).equals(Page.class) ? page : null).when(resource).adaptTo(any());
        Resource parentRes = mock(Resource.class);
        when(resource.getParent()).thenReturn(parentRes);
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertNull(htmlResponse);
        verify(response).setHeader(eq("Location"),
                eq("https://server:port/some/url/content/campaigns/resource/path/with/some/params.html?param1=1&param2=2"));
        verify(response).setStatus(eq(302));
    }

    @Test
    void performCommand_Metadata_NoJsonMode() {
        when(request.getContextPath()).thenReturn("https://server:port");
        when(request.getParameter("path")).thenReturn("/content/campaigns/resource/path/with/some/params?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/content/campaigns/resource/path/with/some/params"))).thenReturn(resource);
        ResourceResolver innerResourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(innerResourceResolver);
        when(resource.getResourceType()).thenReturn("dam:Asset");
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertNull(htmlResponse);
        verify(response).setHeader(eq("Location"),
                eq("https://server:port/libs/wcm/core/content/damadmin.html#/content/campaigns/resource/path/with/some/params?param1=1&param2=2"));
        verify(response).setStatus(eq(302));
    }

    @Test
    void performCommand_Scaffolding_NoJsonMode() {
        when(authoringUIModeService.getEditorURL(any())).thenReturn(EDITOR_URL);
        when(request.getContextPath()).thenReturn("https://server:port");
        when(request.getParameter("path")).thenReturn("/resource/path/with/some/params/and.scaffolding?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/resource/path/with/some/params/and"))).thenReturn(resource);
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertNull(htmlResponse);
        verify(response).setHeader(eq("Location"),
                eq("https://server:port/some/url/resource/path/with/some/params/and.scaffolding.html?param1=1&param2=2"));
        verify(response).setStatus(eq(302));
    }

    @Test
    void performCommand_NoScaffolding_NoJsonMode() {
        when(authoringUIModeService.getEditorURL(any())).thenReturn(EDITOR_URL);
        when(request.getContextPath()).thenReturn("https://server:port");
        when(request.getParameter("path")).thenReturn("/resource/path/with/some/params?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/resource/path/with/some/params"))).thenReturn(resource);
        ResourceResolver innerResourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(innerResourceResolver);
        Resource jcrContentRes = mock(Resource.class);
        when(innerResourceResolver.getResource(eq(resource), eq("jcr:content"))).thenReturn(jcrContentRes);
        ValueMap valueMap = mock(ValueMap.class);
        when(jcrContentRes.adaptTo(eq(ValueMap.class))).thenReturn(valueMap);
        when(valueMap.get(eq("cq:defaultView"), eq(String.class))).thenReturn("scaffolding");
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertNull(htmlResponse);
        verify(response).setHeader(eq("Location"),
                eq("https://server:port/some/url/resource/path/with/some/params.scaffolding.html?param1=1&param2=2"));
        verify(response).setStatus(eq(302));
    }

    @Test
    void performCommand_Html_NoJsonMode() {
        when(request.getContextPath()).thenReturn("https://server:port");
        when(request.getParameter("path")).thenReturn("/resource/path/with/some/params?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/resource/path/with/some/params"))).thenReturn(resource);
        ResourceResolver innerResourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(innerResourceResolver);
        when(resource.getResourceType()).thenReturn("RESOURCE_TYPE");
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        assertNull(htmlResponse);
        verify(response).setHeader(eq("Location"),
                eq("https://server:port/resource/path/with/some/params.html?param1=1&param2=2"));
        verify(response).setStatus(eq(302));
    }

    @Test
    void performCommand_Html_JsonMode() throws IOException {
        when(request.getContextPath()).thenReturn("https://server:port");
        when(request.getParameter("path")).thenReturn("/resource/path/with/some/params?param1=1&param2=2");
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(eq("/resource/path/with/some/params"))).thenReturn(resource);
        ResourceResolver innerResourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(innerResourceResolver);
        when(resource.getResourceType()).thenReturn("RESOURCE_TYPE");
        when(request.getParameter(eq("jsonMode"))).thenReturn(Boolean.TRUE.toString());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        when(response.getWriter()).thenReturn(writer);
        HtmlResponse htmlResponse = sut.performCommand(ctx, request, response, pageManager);
        writer.close();
        assertNull(htmlResponse);
        verify(response).setContentType(eq("application/json"));
        verify(response).setCharacterEncoding(eq("utf-8"));
        verify(response).getWriter();
        JsonReader reader = Json.createReader(new ByteArrayInputStream(out.toByteArray()));
        JsonObject jsonObject = reader.readObject();
        assertEquals(1, jsonObject.keySet().size());
        String location = jsonObject.getString("Location");
        assertEquals("https://server:port/resource/path/with/some/params.html?param1=1&param2=2", location);
    }

}