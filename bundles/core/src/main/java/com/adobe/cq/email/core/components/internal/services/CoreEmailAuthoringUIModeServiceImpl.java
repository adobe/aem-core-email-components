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

    import com.day.cq.wcm.api.AuthoringUIMode;
    import com.day.cq.wcm.api.AuthoringUIModeService;
    import com.day.cq.wcm.api.Page;
    import com.day.cq.wcm.api.WCMMode;
    import com.day.text.Text;
    import java.io.IOException;
    import java.security.Principal;
    import java.util.Arrays;
    import java.util.Dictionary;
    import java.util.List;
    import javax.jcr.Node;
    import javax.jcr.RepositoryException;
    import javax.jcr.Session;
    import javax.servlet.Filter;
    import javax.servlet.FilterChain;
    import javax.servlet.FilterConfig;
    import javax.servlet.ServletException;
    import javax.servlet.ServletRequest;
    import javax.servlet.ServletResponse;
    import javax.servlet.http.Cookie;
    import org.apache.commons.lang3.StringUtils;
    import org.apache.felix.scr.annotations.Activate;
    import org.apache.felix.scr.annotations.Component;
    import org.apache.felix.scr.annotations.Properties;
    import org.apache.felix.scr.annotations.Property;
    import org.apache.felix.scr.annotations.Service;
    import org.apache.jackrabbit.api.JackrabbitSession;
    import org.apache.jackrabbit.api.security.principal.ItemBasedPrincipal;
    import org.apache.jackrabbit.api.security.user.Authorizable;
    import org.apache.sling.api.SlingHttpServletRequest;
    import org.apache.sling.api.SlingHttpServletResponse;
    import org.apache.sling.api.resource.Resource;
    import org.apache.sling.api.resource.ResourceResolver;
    import org.apache.sling.api.resource.ResourceUtil;
    import org.apache.sling.commons.osgi.PropertiesUtil;
    import org.osgi.service.component.ComponentContext;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

@Component(metatype=true, label="%authoringUIModeService.name", description="%authoringUIModeService.description")
@Service({AuthoringUIModeService.class, Filter.class})
@Properties({@Property(name="sling.filter.scope", value={"request"}, propertyPrivate=true), @Property(name="service.ranking", intValue={-2499}, propertyPrivate=true)})
public class CoreEmailAuthoringUIModeServiceImpl
    implements AuthoringUIModeService, Filter
{
    private static final Logger log = LoggerFactory.getLogger(CoreEmailAuthoringUIModeServiceImpl.class);
    private static final String WCM_AUTHORING_MODE_COOKIE = "cq-authoring-mode";
    private static final String WCM_AUTHORING_MODE_USER_PREFERENCE = "authoringMode";
    private static final String[] EXCLUDED_PATHS = { "/etc/commerce/", "/etc/segmentation/contexthub", "/etc/workflow/models/" };
    private AuthoringUIMode defaultAuthoringUIMode;
    private String editorUrlClassic;
    private String editorUrlTouch;
    @Property({"TOUCH"})
    public static final String WCM_DEFAULT_AUTHORING_MODE_PROP = "authoringUIModeService.default";
    private static final String WCM_EDITOR_URL_CLASSIC_DEFAULT = "/cf#";
    @Property(value={"/cf#"}, propertyPrivate=true)
    public static final String WCM_EDITOR_URL_CLASSIC_PROP = "authoringUIModeService.editorUrl.classic";
    private static final String WCM_EDITOR_URL_TOUCH_DEFAULT = "/editor.html";
    @Property(value={"/editor.html"}, propertyPrivate=true)
    public static final String WCM_EDITOR_URL_TOUCH_PROP = "authoringUIModeService.editorUrl.touch";

    public AuthoringUIMode getAuthoringUIMode(SlingHttpServletRequest slingRequest)
    {
        AuthoringUIMode authoringUIMode = null;
        if (slingRequest.getAuthType() != null)
        {
            authoringUIMode = AuthoringUIMode.fromRequest(slingRequest);
            if (authoringUIMode == null) {
                authoringUIMode = getAuthoringUIModeFromCookie(slingRequest);
            }
            if (authoringUIMode == null) {
                authoringUIMode = getAuthoringUIModeFromUserPreferences(slingRequest);
            }
            if (authoringUIMode == null) {
                authoringUIMode = getAuthoringUIModeFromOSGIConfig(slingRequest);
            }
        }
        return authoringUIMode;
    }

    public AuthoringUIMode getAuthoringUIModeFromCookie(SlingHttpServletRequest slingRequest)
    {
        Cookie authoringModeCookie = slingRequest.getCookie("cq-authoring-mode");
        if ((authoringModeCookie != null) && (!StringUtils.isEmpty(authoringModeCookie.getValue()))) {
            try
            {
                return AuthoringUIMode.valueOf(authoringModeCookie.getValue());
            }
            catch (IllegalArgumentException iae)
            {
                log.error("AuthoringUIMode not found for value {}: ", authoringModeCookie.getValue(), iae);
            }
        }
        return null;
    }

    public AuthoringUIMode getAuthoringUIModeFromUserPreferences(SlingHttpServletRequest slingRequest)
    {
        Resource userPreferences = getUserPreferences(slingRequest.getResourceResolver(), null);
        String userPreference = (String)ResourceUtil.getValueMap(userPreferences).get("authoringMode", String.class);
        if (userPreference != null) {
            return AuthoringUIMode.valueOf(userPreference);
        }
        return null;
    }

    public AuthoringUIMode getAuthoringUIModeFromOSGIConfig(SlingHttpServletRequest slingRequest)
    {
        return this.defaultAuthoringUIMode;
    }

    public String getEditorURL(AuthoringUIMode authoringUIMode)
    {
        if (AuthoringUIMode.CLASSIC.equals(authoringUIMode)) {
            return this.editorUrlClassic;
        }
        if (AuthoringUIMode.TOUCH.equals(authoringUIMode)) {
            return this.editorUrlTouch;
        }
        return null;
    }

    public void setUserAuthoringUIMode(ResourceResolver resolver, String userId, AuthoringUIMode authoringUIMode, boolean save)
        throws RepositoryException
    {
        Resource userPreferences = getUserPreferences(resolver, userId);
        if (userPreferences != null)
        {
            Node userPreferencesNode = userPreferences.adaptTo(Node.class);
            if(userPreferencesNode != null) {
                userPreferencesNode.setProperty("authoringMode", authoringUIMode != null ? authoringUIMode.name() : null);
                if (save) {
                    userPreferencesNode.getSession().save();
                }
            }
        }
    }

    private Resource getUserPreferences(ResourceResolver resolver, String userId)
    {
        try
        {
            Authorizable user = null;
            if (userId != null)
            {
                Session session = (Session)resolver.adaptTo(Session.class);
                if ((session instanceof JackrabbitSession)) {
                    user = ((JackrabbitSession)session).getUserManager().getAuthorizable(userId);
                }
            }
            else
            {
                user = (Authorizable)resolver.adaptTo(Authorizable.class);
            }
            if (user != null)
            {
                Principal p = user.getPrincipal();
                if ((p instanceof ItemBasedPrincipal))
                {
                    String homePath = ((ItemBasedPrincipal)p).getPath();
                    return resolver.getResource(Text.makeCanonicalPath(homePath + "/preferences"));
                }
            }
        }
        catch (RepositoryException e)
        {
            log.error("Unable to get user preferences", e);
        }
        return null;
    }

    public void init(FilterConfig filterConfig)
        throws ServletException
    {}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
        throws IOException, ServletException
    {
        if ((request instanceof SlingHttpServletRequest))
        {
            SlingHttpServletRequest slingRequest = (SlingHttpServletRequest)request;
            SlingHttpServletResponse slingResponse = (SlingHttpServletResponse)response;
            Resource resource = slingRequest.getResource();
            if (WCMMode.fromRequest(slingRequest).equals(WCMMode.DISABLED))
            {
                filterChain.doFilter(request, response);
                return;
            }
            AuthoringUIMode authoringUIMode = getAuthoringUIMode(slingRequest);
            if (authoringUIMode != null)
            {
                Cookie authoringModeCookie = slingRequest.getCookie("cq-authoring-mode");
                if ((isEditor(resource, this.editorUrlClassic)) && (!authoringUIMode.equals(AuthoringUIMode.CLASSIC)))
                {
                    authoringUIMode = AuthoringUIMode.CLASSIC;
                }
                else if ((isEditor(resource, this.editorUrlTouch)) && (!authoringUIMode.equals(AuthoringUIMode.TOUCH)))
                {
                    authoringUIMode = AuthoringUIMode.TOUCH;
                }
                else if (authoringUIMode.equals(AuthoringUIMode.TOUCH))
                {
                    String path = resource.getPath();
                    if (("html".equals(slingRequest.getRequestPathInfo().getExtension())) && (resource.adaptTo(Page.class) != null) &&
                        (path.startsWith("/etc/")) && (!StringUtils.startsWithAny(path, EXCLUDED_PATHS)) && (!isPageOfAuthoredTemplate(resource))) {
                        authoringUIMode = AuthoringUIMode.CLASSIC;
                    }
                    if (("html".equals(slingRequest.getRequestPathInfo().getExtension())) && (resource.adaptTo(Page.class) != null) &&
                        (path.startsWith("/content/campaigns")))
                    {
                        Resource content = resource.getChild("jcr:content");

                        List<String> excludeClassicUITypes = Arrays.asList(new String[] { "wcm/designimporter/components/importerpage", "cq/personalization/components/teaserpage", "cq/personalization/components/offerproxy", "mcm/campaign/components/newsletter", "mcm/campaign/components/campaign_newsletterpage", "mcm/campaign/components/profile", "core/email/components/email-page" });

                        boolean forceClassic = true;
                        for (String excludedType : excludeClassicUITypes) {
                            if ((content != null) &&
                                (content.isResourceType(excludedType)))
                            {
                                forceClassic = false;
                                break;
                            }
                        }
                        if ((content == null) || (forceClassic)) {
                            authoringUIMode = AuthoringUIMode.CLASSIC;
                        }
                    }
                }
                if (((authoringModeCookie == null) || (!authoringModeCookie.getValue().equals(authoringUIMode.name()))) && (!slingResponse.isCommitted()))
                {
                    authoringModeCookie = new Cookie("cq-authoring-mode", authoringUIMode.name());
                    authoringModeCookie.setPath(slingRequest.getContextPath() + "/");
                    authoringModeCookie.setMaxAge(604800);
                    slingResponse.addCookie(authoringModeCookie);
                }
                slingRequest.setAttribute(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME, authoringUIMode);
            }
        }
        filterChain.doFilter(request, response);
    }

    public void destroy() {}

    @Activate
    protected void activate(ComponentContext context)
    {
        Dictionary<?, ?> configuration = context.getProperties();
        this.defaultAuthoringUIMode = AuthoringUIMode.valueOf(PropertiesUtil.toString(configuration.get("authoringUIModeService.default"), "authoringUIModeService.default"));
        this.editorUrlClassic = PropertiesUtil.toString(configuration.get("authoringUIModeService.editorUrl.classic"), "/cf#");
        this.editorUrlTouch = PropertiesUtil.toString(configuration.get("authoringUIModeService.editorUrl.touch"), "/editor.html");
    }

    private boolean isEditor(Resource resource, String editor)
    {
        if (editor.endsWith("#")) {
            editor = editor.substring(0, editor.length() - 1);
        }
        if (editor.endsWith(".html")) {
            editor = editor.substring(0, editor.length() - 5);
        }
        Resource editorResource = resource.getResourceResolver().resolve(editor);
        return (editorResource != null) && (resource.getPath().equals(editorResource.getPath()));
    }

    private boolean isPageOfAuthoredTemplate(Resource resource)
    {
        if (resource == null) {
            return false;
        }
        boolean isNamedAccordingly = ("structure".equals(resource.getName())) || ("initial".equals(resource.getName()));
        boolean isOfPageResourceType = "cq:Page".equals(resource.getValueMap().get("jcr:primaryType"));

        Resource parent = resource.getParent();
        boolean isParentOfTemplateResourceType = false;
        boolean isParentAuthoredTempalte = false;
        if (parent != null)
        {
            isParentOfTemplateResourceType = "cq:Template".equals(parent.getValueMap().get("jcr:primaryType"));
            isParentAuthoredTempalte = isAuthoredTemplate(parent);
        }
        return (isNamedAccordingly) && (isOfPageResourceType) && (isParentOfTemplateResourceType) && (isParentAuthoredTempalte);
    }

    private boolean isAuthoredTemplate(Resource resource)
    {
        boolean isAuthored = false;
        if (resource != null) {
            isAuthored = (resource.getChild("initial") != null) && (resource.getChild("structure") != null) && (resource.getChild("policies") != null);
        }
        return isAuthored;
    }
}

