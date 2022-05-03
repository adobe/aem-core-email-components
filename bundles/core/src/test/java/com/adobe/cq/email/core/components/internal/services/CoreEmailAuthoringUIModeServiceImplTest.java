package com.adobe.cq.email.core.components.internal.services;

import java.io.IOException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.ItemBasedPrincipal;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.internal.configuration.AuthorModeUIConfig;
import com.adobe.cq.email.core.components.internal.request.EmptyHttpServletRequest;
import com.day.cq.wcm.api.AuthoringUIMode;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreEmailAuthoringUIModeServiceImplTest {
    private static final String CLASSIC_EDITOR_URL = "https://server:port/classic/editor/url.html#";
    private static final String TOUCH_EDITOR_URL = "https://server:port/touch/editor/url.html#";
    @Mock
    SlingHttpServletRequest request;
    @Mock
    SlingHttpServletResponse response;
    @Mock
    Resource resource;
    @Mock
    FilterChain filterChain;

    private CoreEmailAuthoringUIModeServiceImpl sut;

    @BeforeEach
    void setUp() {
        this.sut = new CoreEmailAuthoringUIModeServiceImpl();
        AuthorModeUIConfig config = mock(AuthorModeUIConfig.class);
        when(config.getDefaultAuthoringUIMode()).thenReturn(AuthoringUIMode.CLASSIC.name());
        when(config.getClassicEditorUrl()).thenReturn(CLASSIC_EDITOR_URL);
        when(config.getTouchEditorUrl()).thenReturn(TOUCH_EDITOR_URL);
        this.sut.activate(config);
    }

    @Test
    void getAuthoringUIMode_Classic() {
        when(request.getAuthType()).thenReturn("AUTH_TYPE");
        when(request.getAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(AuthoringUIMode.CLASSIC);
        when(request.getResource()).thenReturn(resource);
        assertEquals(AuthoringUIMode.CLASSIC, sut.getAuthoringUIMode(request));
    }

    @Test
    void getAuthoringUIMode_Cookie() {
        when(request.getAuthType()).thenReturn("AUTH_TYPE");
        when(request.getAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(null);
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn(AuthoringUIMode.CLASSIC.name());
        when(request.getCookie(eq("cq-authoring-mode"))).thenReturn(cookie);
        assertEquals(AuthoringUIMode.CLASSIC, sut.getAuthoringUIMode(request));
    }

    @Test
    void getAuthoringUIMode_UserPreferences() throws RepositoryException {
        when(request.getAuthType()).thenReturn("AUTH_TYPE");
        when(request.getAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(null);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        Authorizable authorizable = mock(Authorizable.class);
        when(resourceResolver.adaptTo(Authorizable.class)).thenReturn(authorizable);
        ItemBasedPrincipal principal = mock(ItemBasedPrincipal.class);
        when(authorizable.getPrincipal()).thenReturn(principal);
        when(principal.getPath()).thenReturn("/generic/path");
        Resource userPreferences = mock(Resource.class);
        when(resourceResolver.getResource(eq("/generic/path/preferences"))).thenReturn(userPreferences);
        ValueMap valueMap = mock(ValueMap.class);
        when(userPreferences.getValueMap()).thenReturn(valueMap);
        when(valueMap.get(eq("authoringMode"), eq(String.class))).thenReturn(AuthoringUIMode.CLASSIC.name());
        assertEquals(AuthoringUIMode.CLASSIC, sut.getAuthoringUIMode(request));
    }

    @Test
    void getAuthoringUIMode_OsgiConfig() {
        when(request.getAuthType()).thenReturn("AUTH_TYPE");
        when(request.getAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(null);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        assertEquals(AuthoringUIMode.CLASSIC, sut.getAuthoringUIMode(request));
    }

    @Test
    void getEditorUrl_Classic() {
        assertEquals(CLASSIC_EDITOR_URL, sut.getEditorURL(AuthoringUIMode.CLASSIC));
    }

    @Test
    void getEditorUrl_Touch() {
        assertEquals(TOUCH_EDITOR_URL, sut.getEditorURL(AuthoringUIMode.TOUCH));
    }

    @Test
    void setAuthoringUIMode() throws RepositoryException {
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        JackrabbitSession jackrabbitSession = mock(JackrabbitSession.class);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(jackrabbitSession);
        UserManager userManager = mock(UserManager.class);
        when(jackrabbitSession.getUserManager()).thenReturn(userManager);
        Authorizable authorizable = mock(Authorizable.class);
        when(userManager.getAuthorizable(eq("USER_ID"))).thenReturn(authorizable);
        ItemBasedPrincipal principal = mock(ItemBasedPrincipal.class);
        when(authorizable.getPrincipal()).thenReturn(principal);
        when(principal.getPath()).thenReturn("/generic/path");
        Resource userPreferences = mock(Resource.class);
        when(resourceResolver.getResource(eq("/generic/path/preferences"))).thenReturn(userPreferences);
        Node node = mock(Node.class);
        when(userPreferences.adaptTo(Node.class)).thenReturn(node);
        Session session = mock(Session.class);
        when(node.getSession()).thenReturn(session);
        sut.setUserAuthoringUIMode(resourceResolver, "USER_ID", AuthoringUIMode.CLASSIC, true);
        verify(node).setProperty(eq("authoringMode"), eq(AuthoringUIMode.CLASSIC.name()));
        verify(session).save();
    }

    @Test
    void doFilter_WcmModeDisabled() throws ServletException, IOException {
        when(request.getResource()).thenReturn(resource);
        when(request.getAttribute(eq(WCMMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(WCMMode.DISABLED);
        sut.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(eq(request), eq(response));
        verifyZeroInteractions(response);
        verifyNoMoreInteractions(request, filterChain);
    }

    @Test
    void doFilter_WrongRequestType() throws ServletException, IOException {
        EmptyHttpServletRequest wrongRequestType = new EmptyHttpServletRequest();
        sut.doFilter(wrongRequestType, response, filterChain);
        verify(filterChain).doFilter(eq(wrongRequestType), eq(response));
        verifyZeroInteractions(request, response);
        verifyNoMoreInteractions(filterChain);
    }

    @Test
    void doFilter_Cookie_Classic() throws ServletException, IOException {
        when(request.getResource()).thenReturn(resource);
        when(request.getAttribute(eq(WCMMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(WCMMode.EDIT);
        when(request.getAuthType()).thenReturn("AUTH_TYPE");
        when(request.getAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(AuthoringUIMode.CLASSIC);
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn(AuthoringUIMode.TOUCH.name());
        when(request.getCookie(eq("cq-authoring-mode"))).thenReturn(cookie);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        Resource editorResource = mock(Resource.class);
        when(resourceResolver.resolve(eq(CLASSIC_EDITOR_URL.substring(0, CLASSIC_EDITOR_URL.length() - 6)))).thenReturn(editorResource);
        String resourcePath = "/resource/path";
        when(resource.getPath()).thenReturn(resourcePath);
        when(editorResource.getPath()).thenReturn(resourcePath);
        sut.doFilter(request, response, filterChain);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();
        assertEquals("cq-authoring-mode", capturedCookie.getName());
        assertEquals(AuthoringUIMode.CLASSIC.name(), capturedCookie.getValue());
        verify(request).getContextPath();
        verify(response).isCommitted();
        verify(request).setAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME), eq(AuthoringUIMode.CLASSIC));
        verify(filterChain).doFilter(eq(request), eq(response));
        verifyZeroInteractions(response);
        verifyNoMoreInteractions(request, filterChain);
    }

    @Test
    void doFilter_Cookie_Touch() throws ServletException, IOException {
        when(request.getResource()).thenReturn(resource);
        when(request.getAttribute(eq(WCMMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(WCMMode.EDIT);
        when(request.getAuthType()).thenReturn("AUTH_TYPE");
        when(request.getAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(AuthoringUIMode.CLASSIC);
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn(AuthoringUIMode.TOUCH.name());
        when(request.getCookie(eq("cq-authoring-mode"))).thenReturn(cookie);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        Resource invalidEditorResource = mock(Resource.class);
        when(resourceResolver.resolve(eq(CLASSIC_EDITOR_URL.substring(0, CLASSIC_EDITOR_URL.length() - 6)))).thenReturn(
                invalidEditorResource);
        Resource editorResource = mock(Resource.class);
        when(resourceResolver.resolve(eq(TOUCH_EDITOR_URL.substring(0, TOUCH_EDITOR_URL.length() - 6)))).thenReturn(editorResource);
        String resourcePath = "/resource/path";
        when(resource.getPath()).thenReturn(resourcePath);
        when(editorResource.getPath()).thenReturn(resourcePath);
        sut.doFilter(request, response, filterChain);
        verify(request).setAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME), eq(AuthoringUIMode.TOUCH));
        verify(filterChain).doFilter(eq(request), eq(response));
        verifyZeroInteractions(response);
        verifyNoMoreInteractions(request, filterChain);
    }

    @Test
    void doFilter_Cookie_NotEditor_Etc_NotAuthoredTemplate() throws ServletException, IOException {
        when(request.getResource()).thenReturn(resource);
        when(request.getAttribute(eq(WCMMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(WCMMode.EDIT);
        when(request.getAuthType()).thenReturn("AUTH_TYPE");
        when(request.getAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(AuthoringUIMode.TOUCH);
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn(AuthoringUIMode.TOUCH.name());
        when(request.getCookie(eq("cq-authoring-mode"))).thenReturn(cookie);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        Resource invalidEditorResource = mock(Resource.class);
        when(resourceResolver.resolve(eq(CLASSIC_EDITOR_URL.substring(0, CLASSIC_EDITOR_URL.length() - 6)))).thenReturn(
                invalidEditorResource);
        when(resourceResolver.resolve(eq(TOUCH_EDITOR_URL.substring(0, TOUCH_EDITOR_URL.length() - 6)))).thenReturn(invalidEditorResource);
        String resourcePath = "/etc/resource/path";
        when(resource.getPath()).thenReturn(resourcePath);
        RequestPathInfo requestPathInfo = mock(RequestPathInfo.class);
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getExtension()).thenReturn("html");
        when(resource.adaptTo(eq(Page.class))).thenReturn(mock(Page.class));
        when(resource.getName()).thenReturn("structure");
        ValueMap valueMap = mock(ValueMap.class);
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get(eq("jcr:primaryType"))).thenReturn("cq:Page");
        Resource parent = mock(Resource.class);
        when(resource.getParent()).thenReturn(parent);
        ValueMap parentValueMap = mock(ValueMap.class);
        when(parent.getValueMap()).thenReturn(parentValueMap);
        when(parentValueMap.get(eq("jcr:primaryType"))).thenReturn("cq:Template");
        when(parent.getChild(eq("initial"))).thenReturn(mock(Resource.class));
        when(parent.getChild(eq("structure"))).thenReturn(mock(Resource.class));
        when(parent.getChild(eq("policies"))).thenReturn(null);
        sut.doFilter(request, response, filterChain);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();
        assertEquals("cq-authoring-mode", capturedCookie.getName());
        assertEquals(AuthoringUIMode.CLASSIC.name(), capturedCookie.getValue());
        verify(request).getContextPath();
        verify(response).isCommitted();
        verify(request).setAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME), eq(AuthoringUIMode.CLASSIC));
        verify(filterChain).doFilter(eq(request), eq(response));
        verifyZeroInteractions(response);
        verifyNoMoreInteractions(request, filterChain);
    }

    @Test
    void doFilter_Cookie_NotEditor_ContentCampaignsPath() throws ServletException, IOException {
        when(request.getResource()).thenReturn(resource);
        when(request.getAttribute(eq(WCMMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(WCMMode.EDIT);
        when(request.getAuthType()).thenReturn("AUTH_TYPE");
        when(request.getAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(AuthoringUIMode.TOUCH);
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn(AuthoringUIMode.TOUCH.name());
        when(request.getCookie(eq("cq-authoring-mode"))).thenReturn(cookie);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        Resource invalidEditorResource = mock(Resource.class);
        when(resourceResolver.resolve(eq(CLASSIC_EDITOR_URL.substring(0, CLASSIC_EDITOR_URL.length() - 6)))).thenReturn(
                invalidEditorResource);
        when(resourceResolver.resolve(eq(TOUCH_EDITOR_URL.substring(0, TOUCH_EDITOR_URL.length() - 6)))).thenReturn(invalidEditorResource);
        String resourcePath = "/content/campaigns/resource/path";
        when(resource.getPath()).thenReturn(resourcePath);
        RequestPathInfo requestPathInfo = mock(RequestPathInfo.class);
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getExtension()).thenReturn("html");
        when(resource.adaptTo(eq(Page.class))).thenReturn(mock(Page.class));
        sut.doFilter(request, response, filterChain);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();
        assertEquals("cq-authoring-mode", capturedCookie.getName());
        assertEquals(AuthoringUIMode.CLASSIC.name(), capturedCookie.getValue());
        verify(request).getContextPath();
        verify(response).isCommitted();
        verify(request).setAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME), eq(AuthoringUIMode.CLASSIC));
        verify(filterChain).doFilter(eq(request), eq(response));
        verifyZeroInteractions(response);
        verifyNoMoreInteractions(request, filterChain);
    }

    @Test
    void doFilter_Cookie_NotEditor_ContentCampaignsPath_ForceClassic() throws ServletException, IOException {
        when(request.getResource()).thenReturn(resource);
        when(request.getAttribute(eq(WCMMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(WCMMode.EDIT);
        when(request.getAuthType()).thenReturn("AUTH_TYPE");
        when(request.getAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME))).thenReturn(AuthoringUIMode.TOUCH);
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn(AuthoringUIMode.TOUCH.name());
        when(request.getCookie(eq("cq-authoring-mode"))).thenReturn(cookie);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        Resource invalidEditorResource = mock(Resource.class);
        when(resourceResolver.resolve(eq(CLASSIC_EDITOR_URL.substring(0, CLASSIC_EDITOR_URL.length() - 6)))).thenReturn(
                invalidEditorResource);
        when(resourceResolver.resolve(eq(TOUCH_EDITOR_URL.substring(0, TOUCH_EDITOR_URL.length() - 6)))).thenReturn(invalidEditorResource);
        String resourcePath = "/content/campaigns/resource/path";
        when(resource.getPath()).thenReturn(resourcePath);
        RequestPathInfo requestPathInfo = mock(RequestPathInfo.class);
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getExtension()).thenReturn("html");
        when(resource.adaptTo(eq(Page.class))).thenReturn(mock(Page.class));
        Resource child = mock(Resource.class);
        when(resource.getChild(eq("jcr:content"))).thenReturn(child);
        doAnswer(i -> i.getArgument(0).equals("core/email/components/page")).when(child).isResourceType(anyString());
        when(child.isResourceType(eq("core/email/components/page"))).thenReturn(true);
        sut.doFilter(request, response, filterChain);
        verify(request).setAttribute(eq(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME), eq(AuthoringUIMode.TOUCH));
        verify(filterChain).doFilter(eq(request), eq(response));
        verifyZeroInteractions(response);
        verifyNoMoreInteractions(request, filterChain);
    }
}