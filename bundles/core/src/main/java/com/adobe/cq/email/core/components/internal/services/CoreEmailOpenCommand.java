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

import com.day.cq.commons.TidyJSONWriter;
import com.day.cq.commons.servlets.HtmlStatusResponseHelper;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.AuthoringUIMode;
import com.day.cq.wcm.api.AuthoringUIModeService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.commands.WCMCommand;
import com.day.cq.wcm.api.commands.WCMCommandContext;
import com.day.cq.wcm.commons.WCMUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HtmlResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Based on com.day.cq.wcm.core.impl.commands.OpenCommand
 */
@Component(
    service = { WCMCommand.class },
    property = {
        "service.ranking:Integer=1",
        "cq.wcmcommand.methods=GET"
    }
)
@ServiceDescription("Core Email Open Command")
public class CoreEmailOpenCommand
    implements WCMCommand
{
    @Reference
    private AuthoringUIModeService authoringUIModeService;
    public static final String JSON_MODE = "jsonMode";
    private static final Logger log = LoggerFactory.getLogger(CoreEmailOpenCommand.class);

    private String[] cqWcmcommandMethods;

    @Override
    public String getCommandName()
    {
        return "open";
    }

    @Override
    public HtmlResponse performCommand(WCMCommandContext ctx, SlingHttpServletRequest request, SlingHttpServletResponse response, PageManager pageManager)
    {
        I18n i18n = new I18n(request);
        try
        {
            String path = request.getParameter("path");
            if ((path == null) || (path.length() == 0)) {
                return HtmlStatusResponseHelper.createStatusResponse(false, "No path parameter");
            }
            String params = "";
            if (path.indexOf("?") != -1)
            {
                params = path.substring(path.indexOf("?"));
                path = path.substring(0, path.indexOf("?"));
            }
            boolean scaffolding = false;
            if (path.endsWith(".scaffolding"))
            {
                scaffolding = true;
                path = path.substring(0, path.lastIndexOf("."));
            }
            Resource res = request.getResourceResolver().getResource(path);
            if (res == null)
            {
                response.sendError(404);
                return null;
            }
            String encPath = Text.escapePath(path);
            String view = scaffolding ? "scaffolding" : getView(res);
            String pattern = getPattern(request, view, path);
            String url = request.getContextPath() + pattern.replace("${path}", encPath) + params;
            if (request.getParameter("jsonMode") != null)
            {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                TidyJSONWriter w = new TidyJSONWriter(response.getWriter());
                w.setTidy(true);
                w.object();
                w.key("Location").value(url);
                w.endObject();
            }
            else
            {
                response.setHeader("Location", url);
                response.setStatus(302);
            }
            return null;
        }
        catch (Exception e)
        {
            log.error("Error during open command", e);
        }
        return HtmlStatusResponseHelper.createStatusResponse(false, i18n.get("Error during open command ({0})"));
    }

    public String getView(Resource res)
    {
        Resource content = res.getResourceResolver().getResource(res, "jcr:content");
        if (content != null)
        {
            ValueMap props = content.adaptTo(ValueMap.class);
            if(props != null) {
                String view = props.get("cq:defaultView", String.class);
                if (view != null) {
                    return view;
                }
                com.day.cq.wcm.api.components.Component comp = WCMUtils.getComponent(content);
                view = comp == null ? null : comp.getDefaultView();
                if (view != null) {
                    return view;
                }
            }
        }
        ValueMap props = res.adaptTo(ValueMap.class);
        if(props != null) {
            String view = props.get("cq:defaultView", String.class);
            if (view != null) {
                return view;
            }
            com.day.cq.wcm.api.components.Component comp = WCMUtils.getComponent(res);
            view = comp == null ? null : comp.getDefaultView();
            if (view != null) {
                return view;
            }
        }
        Page page = res.adaptTo(Page.class);
        if ((isAuthoredTemplate(res)) || ((page != null) && (isAuthoredTemplate(res.getParent())))) {
            return "templateeditor";
        }
        if (page != null) {
            return "contentfinder";
        }
        if (res.getResourceType().equals("dam:Asset")) {
            return "metadata";
        }
        return "html";
    }

    private String getPattern(SlingHttpServletRequest request, String view, String path)
    {
        if ("contentfinder".equals(view))
        {
            if ((path.startsWith("/etc/")) && (!path.startsWith("/etc/commerce/"))) {
                return this.authoringUIModeService.getEditorURL(AuthoringUIMode.CLASSIC) + "${path}.html";
            }
            if (path.startsWith("/content/campaigns"))
            {
                Resource resource = request.getResourceResolver().getResource(path + "/" + "jcr:content");
                if ((resource == null) || (
                    (!resource.isResourceType("wcm/designimporter/components/importerpage")) &&
                        (!resource.isResourceType("cq/personalization/components/teaserpage")) &&
                        (!resource.isResourceType("mcm/campaign/components/newsletter")) &&
                        (!resource.isResourceType("mcm/campaign/components/campaign_newsletterpage")) &&
                        (!resource.isResourceType("mcm/campaign/components/profile")) &&
                        (!resource.isResourceType("core/email/components/page/v1/page")))) {
                    return this.authoringUIModeService.getEditorURL(AuthoringUIMode.CLASSIC) + "${path}.html";
                }
            }
            AuthoringUIMode uiMode = this.authoringUIModeService.getAuthoringUIModeFromUserPreferences(request);
            if (uiMode == null) {
                uiMode = this.authoringUIModeService.getAuthoringUIModeFromOSGIConfig(request);
            }
            return (null != uiMode ? this.authoringUIModeService.getEditorURL(uiMode) : "") + "${path}.html";
        }
        if ("scaffolding".equals(view))
        {
            AuthoringUIMode uiMode = this.authoringUIModeService.getAuthoringUIMode(request);

            uiMode = AuthoringUIMode.CLASSIC;

            return (null != uiMode ? this.authoringUIModeService.getEditorURL(uiMode) : "") + "${path}.scaffolding.html";
        }
        if ("metadata".equals(view)) {
            return "/libs/wcm/core/content/damadmin.html#${path}";
        }
        if ("templateeditor".equals(view)) {
            return this.authoringUIModeService.getEditorURL(AuthoringUIMode.TOUCH) + "${path}.html";
        }
        if ("html".equals(view)) {
            return "${path}.html";
        }
        if ((view == null) || (view.length() == 0) || ("direct".equals(view))) {
            return "${path}";
        }
        return view;
    }

    protected void bindAuthoringUIModeService(AuthoringUIModeService paramAuthoringUIModeService)
    {
        this.authoringUIModeService = paramAuthoringUIModeService;
    }

    protected void unbindAuthoringUIModeService(AuthoringUIModeService paramAuthoringUIModeService)
    {
        if (this.authoringUIModeService == paramAuthoringUIModeService) {
            this.authoringUIModeService = null;
        }
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

