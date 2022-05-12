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

import com.adobe.cq.email.core.components.internal.configuration.AuthorModeUIConfig;
import com.adobe.cq.email.core.components.util.HtmlSanitizer;
import com.day.cq.wcm.api.AuthoringUIMode;
import com.day.cq.wcm.api.AuthoringUIModeService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.day.text.Text;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
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
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.ItemBasedPrincipal;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link AuthoringUIModeService} and {@link Filter} implementation based on com.day.cq.wcm.core.impl.AuthoringUIModeServiceImpl
 */
@Component(
        service = {AuthoringUIModeService.class, Filter.class},
        property = {
                "sling.filter.scope=request",
                "service.ranking:Integer=-2501"
        }
)
@ServiceDescription("Core Email Authoring UI Mode Service")
@Designate(ocd = AuthorModeUIConfig.class)
public class CoreEmailAuthoringUIModeServiceImpl
        implements AuthoringUIModeService, Filter {
    private static final Logger LOG = LoggerFactory.getLogger(CoreEmailAuthoringUIModeServiceImpl.class.getName());
    private AuthorModeUIConfig config;

    private static final Logger log = LoggerFactory.getLogger(CoreEmailAuthoringUIModeServiceImpl.class);
    private static final String WCM_AUTHORING_MODE_COOKIE = "cq-authoring-mode";
    private static final String WCM_AUTHORING_MODE_USER_PREFERENCE = "authoringMode";
    private static final String[] EXCLUDED_PATHS = {"/etc/commerce/", "/etc/segmentation/contexthub", "/etc/workflow/models/"};
    private AuthoringUIMode defaultAuthoringUIMode;
    private String editorUrlClassic;
    private String editorUrlTouch;
    private static final String WCM_EDITOR_URL_CLASSIC_DEFAULT = "/cf#";
    private static final String WCM_EDITOR_URL_TOUCH_DEFAULT = "/editor.html";

    @Override
    public AuthoringUIMode getAuthoringUIMode(SlingHttpServletRequest slingRequest) {
        AuthoringUIMode authoringUIMode = null;
        if (slingRequest.getAuthType() != null) {
            authoringUIMode = AuthoringUIMode.fromRequest(slingRequest);

            //The following check if only required as a workaround, until this class is updated in AEM SP13
            if (AuthoringUIMode.CLASSIC.equals(authoringUIMode)) {
                Resource resource = slingRequest.getResource();
                Resource content = resource.getChild("jcr:content");
                if (content != null && content.isResourceType("core/email/components/page")) {
                    return AuthoringUIMode.TOUCH;
                }
            }

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

    @Override
    public AuthoringUIMode getAuthoringUIModeFromCookie(SlingHttpServletRequest slingRequest) {
        Cookie authoringModeCookie = slingRequest.getCookie("cq-authoring-mode");
        if ((authoringModeCookie != null) && (!StringUtils.isEmpty(authoringModeCookie.getValue()))) {
            try {
                return AuthoringUIMode.valueOf(authoringModeCookie.getValue());
            } catch (IllegalArgumentException iae) {
                log.error("AuthoringUIMode not found for value {}: ", authoringModeCookie.getValue(), iae);
            }
        }
        return null;
    }

    @Override
    public AuthoringUIMode getAuthoringUIModeFromUserPreferences(SlingHttpServletRequest slingRequest) {
        Resource userPreferences = getUserPreferences(slingRequest.getResourceResolver(), null);
        String userPreference = (String) ResourceUtil.getValueMap(userPreferences).get("authoringMode", String.class);
        if (userPreference != null) {
            return AuthoringUIMode.valueOf(userPreference);
        }
        return null;
    }

    @Override
    public AuthoringUIMode getAuthoringUIModeFromOSGIConfig(SlingHttpServletRequest slingRequest) {
        return this.defaultAuthoringUIMode;
    }

    @Override
    public String getEditorURL(AuthoringUIMode authoringUIMode) {
        if (AuthoringUIMode.CLASSIC.equals(authoringUIMode)) {
            return this.editorUrlClassic;
        }
        if (AuthoringUIMode.TOUCH.equals(authoringUIMode)) {
            return this.editorUrlTouch;
        }
        return null;
    }

    @Override
    public void setUserAuthoringUIMode(ResourceResolver resolver, String userId, AuthoringUIMode authoringUIMode, boolean save)
            throws RepositoryException {
        Resource userPreferences = getUserPreferences(resolver, userId);
        if (userPreferences != null) {
            Node userPreferencesNode = userPreferences.adaptTo(Node.class);
            if (userPreferencesNode != null) {
                userPreferencesNode.setProperty("authoringMode", authoringUIMode != null ? authoringUIMode.name() : null);
                if (save) {
                    userPreferencesNode.getSession().save();
                }
            }
        }
    }

    private Resource getUserPreferences(ResourceResolver resolver, String userId) {
        try {
            Authorizable user = null;
            if (userId != null) {
                Session session = (Session) resolver.adaptTo(Session.class);
                if ((session instanceof JackrabbitSession)) {
                    user = ((JackrabbitSession) session).getUserManager().getAuthorizable(userId);
                }
            } else {
                user = (Authorizable) resolver.adaptTo(Authorizable.class);
            }
            if (user != null) {
                Principal p = user.getPrincipal();
                if ((p instanceof ItemBasedPrincipal)) {
                    String homePath = ((ItemBasedPrincipal) p).getPath();
                    return resolver.getResource(Text.makeCanonicalPath(homePath + "/preferences"));
                }
            }
        } catch (RepositoryException e) {
            log.error("Unable to get user preferences", e);
        }
        return null;
    }

    /**
     * Init method
     * @param filterConfig {@link FilterConfig}
     * @throws ServletException if something went wrong
     */
    public void init(FilterConfig filterConfig)
            throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        LOG.debug("UI Mode Filter start filtering the request.");
        if ((request instanceof SlingHttpServletRequest)) {
            SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
            SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
            Resource resource = slingRequest.getResource();
            if (WCMMode.fromRequest(slingRequest).equals(WCMMode.DISABLED)) {
                filterChain.doFilter(request, response);
                return;
            }
            AuthoringUIMode authoringUIMode = getAuthoringUIMode(slingRequest);
            if (authoringUIMode != null) {
                Cookie authoringModeCookie = slingRequest.getCookie("cq-authoring-mode");
                if ((isEditor(resource, this.editorUrlClassic)) && (!authoringUIMode.equals(AuthoringUIMode.CLASSIC))) {
                    authoringUIMode = AuthoringUIMode.CLASSIC;
                    LOG.trace("UI mode set to classic based on editor settings.");
                } else if ((isEditor(resource, this.editorUrlTouch)) && (!authoringUIMode.equals(AuthoringUIMode.TOUCH))) {
                    authoringUIMode = AuthoringUIMode.TOUCH;
                    LOG.trace("UI mode set to touch based on editor settings.");
                } else if (authoringUIMode.equals(AuthoringUIMode.TOUCH)) {
                    String path = resource.getPath();
                    if (("html".equals(slingRequest.getRequestPathInfo().getExtension())) && (resource.adaptTo(Page.class) != null) &&
                            (path.startsWith("/etc/")) && (!StringUtils.startsWithAny(path, EXCLUDED_PATHS)) &&
                            (!isPageOfAuthoredTemplate(resource))) {
                        authoringUIMode = AuthoringUIMode.CLASSIC;
                        LOG.trace("UI mode set to classic based path or resource - path: {}.", path);
                    }
                    if (("html".equals(slingRequest.getRequestPathInfo().getExtension())) && (resource.adaptTo(Page.class) != null) &&
                            (path.startsWith("/content/campaigns"))) {
                        LOG.trace("Calculating UI mode for campaign pages: {}.", path);

                        Resource content = resource.getChild("jcr:content");

                        List<String> excludeClassicUITypes = Arrays.asList(
                                new String[]{"wcm/designimporter/components/importerpage", "cq/personalization/components/teaserpage",
                                        "cq/personalization/components/offerproxy", "mcm/campaign/components/newsletter",
                                        "mcm/campaign/components/campaign_newsletterpage", "mcm/campaign/components/profile",
                                        "core/email/components/page"});

                        boolean forceClassic = true;
                        for (String excludedType : excludeClassicUITypes) {
                            if ((content != null) &&
                                    (content.isResourceType(excludedType))) {
                                LOG.debug("force classic set to false for type {}.", excludedType);
                                forceClassic = false;
                                break;
                            } else {
                                LOG.trace("Ignore type {}.", excludedType);
                            }
                        }
                        if ((content == null) || (forceClassic)) {
                            authoringUIMode = AuthoringUIMode.CLASSIC;
                        }
                    }
                }
                if (((authoringModeCookie == null) || (!authoringModeCookie.getValue().equals(authoringUIMode.name()))) &&
                        (!slingResponse.isCommitted())) {
                    authoringModeCookie = new Cookie("cq-authoring-mode", authoringUIMode.name());
                    authoringModeCookie.setPath(slingRequest.getContextPath() + "/");
                    authoringModeCookie.setMaxAge(604800);
                    slingResponse.addCookie(authoringModeCookie);
                }
                slingRequest.setAttribute(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME, authoringUIMode);
            }
        } else {
            LOG.debug("Wrong request type. Do not touch request data.");
        }
        filterChain.doFilter(request, response);
    }

    public void destroy() {
    }

    @Activate
    protected void activate(AuthorModeUIConfig config) {
        LOG.debug("Starting service.");
        this.defaultAuthoringUIMode = AuthoringUIMode.valueOf(
                StringUtils.isEmpty(config.getDefaultAuthoringUIMode()) ? "TOUCH" : config.getDefaultAuthoringUIMode());
        this.editorUrlClassic = config.getClassicEditorUrl();
        this.editorUrlTouch = config.getTouchEditorUrl();
        LOG.trace("Config values set to {}, {} , {}.", defaultAuthoringUIMode, editorUrlClassic, editorUrlTouch);
    }

    private boolean isEditor(Resource resource, String editor) {
        if (editor.endsWith("#")) {
            editor = editor.substring(0, editor.length() - 1);
        }
        if (editor.endsWith(".html")) {
            editor = editor.substring(0, editor.length() - 5);
        }
        Resource editorResource = resource.getResourceResolver().resolve(editor);
        return (editorResource != null) && (resource.getPath().equals(editorResource.getPath()));
    }

    private boolean isPageOfAuthoredTemplate(Resource resource) {
        if (resource == null) {
            return false;
        }
        boolean isNamedAccordingly = ("structure".equals(resource.getName())) || ("initial".equals(resource.getName()));
        boolean isOfPageResourceType = "cq:Page".equals(resource.getValueMap().get("jcr:primaryType"));

        Resource parent = resource.getParent();
        boolean isParentOfTemplateResourceType = false;
        boolean isParentAuthoredTempalte = false;
        if (parent != null) {
            isParentOfTemplateResourceType = "cq:Template".equals(parent.getValueMap().get("jcr:primaryType"));
            isParentAuthoredTempalte = isAuthoredTemplate(parent);
        }
        return (isNamedAccordingly) && (isOfPageResourceType) && (isParentOfTemplateResourceType) && (isParentAuthoredTempalte);
    }

    private boolean isAuthoredTemplate(Resource resource) {
        boolean isAuthored = false;
        if (resource != null) {
            isAuthored = (resource.getChild("initial") != null) && (resource.getChild("structure") != null) &&
                    (resource.getChild("policies") != null);
        }
        return isAuthored;
    }
}

