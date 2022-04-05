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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.engine.SlingRequestProcessor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.components.config.StylesInlinerContextAwareConfiguration;
import com.adobe.cq.email.core.components.enumerations.HtmlSanitizingMode;
import com.adobe.cq.email.core.components.enumerations.StyleMergerMode;
import com.adobe.cq.email.core.components.internal.css.CssInliner;
import com.adobe.cq.email.core.components.services.StylesInlinerService;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.day.cq.wcm.api.WCMMode;

@Component(
        service = Filter.class,
        property = {
                "sling.filter.scope=request",
                "service.ranking:Integer=-2502",
                "sling.filter.extensions=html"
        }
)
public class StylesInlinerFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(CssInliner.class);
    static final String RESOURCE_TYPE = "core/email/components/page";
    static final String PROCESSED_ATTRIBUTE = "styles_filter_processed";

    @Reference
    private transient RequestResponseFactory requestResponseFactory;

    @Reference
    private transient SlingRequestProcessor requestProcessor;

    @Reference
    private transient StylesInlinerService stylesInlinerService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;
        boolean alreadyProcessed = hasBeenProcessed(request);
        Resource resource = request.getResource();
        Resource contentResource = resource.getChild(JcrConstants.JCR_CONTENT);
        if (Objects.isNull(contentResource) || !contentResource.getResourceType().equals(RESOURCE_TYPE) ||
                !WCMMode.DISABLED.equals(WCMMode.fromRequest(request)) ||
                alreadyProcessed) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        String pagePath = resource.getPath();
        HttpServletRequest req = requestResponseFactory.createRequest("GET", pagePath + ".html",
                params);
        req.setAttribute(PROCESSED_ATTRIBUTE, true);
        WCMMode.DISABLED.toRequest(req);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpServletResponse response = requestResponseFactory.createResponse(out);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        requestProcessor.processRequest(req, response, request.getResourceResolver());
        StylesInlinerContextAwareConfiguration configuration = getConfiguration(resource);
        String htmlWithInlineStyles =
                stylesInlinerService.getHtmlWithInlineStyles(request.getResourceResolver(), out.toString(StandardCharsets.UTF_8.name()),
                        StyleMergerMode.getByValue(configuration.stylesMergingMode()),
                        HtmlSanitizingMode.getByValue(configuration.htmlSanitizingMode()));
        servletResponse.setContentType("text/html");
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter pw = servletResponse.getWriter();
        pw.write(htmlWithInlineStyles);
    }

    @Override
    public void destroy() {
        // do nothing
    }

    void setRequestResponseFactory(RequestResponseFactory requestResponseFactory) {
        this.requestResponseFactory = requestResponseFactory;
    }

    void setRequestProcessor(SlingRequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    void setStylesInlinerService(StylesInlinerService stylesInlinerService) {
        this.stylesInlinerService = stylesInlinerService;
    }

    private boolean hasBeenProcessed(SlingHttpServletRequest request) {
        try {
            Object parameter = request.getAttribute(PROCESSED_ATTRIBUTE);
            if (Objects.isNull(parameter)) {
                return false;
            }
            if (parameter.getClass().isAssignableFrom(Boolean.class)) {
                return (boolean) parameter;
            }
            return Boolean.parseBoolean(String.valueOf(parameter));
        } catch (Throwable e) {
            return false;
        }
    }

    private StylesInlinerContextAwareConfiguration getConfiguration(Resource resource) {
        StylesInlinerContextAwareConfiguration fallback = new StylesInlinerContextAwareConfiguration() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return StylesInlinerContextAwareConfiguration.class;
            }

            @Override
            public String stylesMergingMode() {
                return null;
            }

            @Override
            public String htmlSanitizingMode() {
                return null;
            }
        };
        try {
            ConfigurationBuilder configurationBuilder = resource.adaptTo(ConfigurationBuilder.class);
            if (Objects.isNull(configurationBuilder)) {
                return fallback;
            }
            return configurationBuilder.as(StylesInlinerContextAwareConfiguration.class);
        } catch (Throwable e) {
            LOG.warn("Error retrieving configuration: " + e.getMessage(), e);
        }
        return fallback;
    }

}
