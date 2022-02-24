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
package com.adobe.cq.email.core.components.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.engine.SlingRequestProcessor;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.email.core.components.config.StylesInlinerContextAwareConfiguration;
import com.adobe.cq.email.core.components.constants.StylesInlinerConstants;
import com.adobe.cq.email.core.components.enumerations.HtmlSanitizingMode;
import com.adobe.cq.email.core.components.enumerations.StyleMergerMode;
import com.adobe.cq.email.core.components.services.StylesInlinerService;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.WCMMode;

/**
 * This servlet will take the AEM page as input, makes the styles inline
 * and returns the html with inline styles in response.
 */
@Component(service = {Servlet.class}, immediate = true)
@SlingServletResourceTypes(
        resourceTypes = "core/email/components/email-page",
        selectors = StylesInlinerConstants.INLINE_STYLES_SELECTOR,
        extensions = "html")
public class StylesInlinerServlet extends SlingSafeMethodsServlet {

    @Reference
    private transient RequestResponseFactory requestResponseFactory;

    @Reference
    private transient SlingRequestProcessor requestProcessor;

    @Reference
    private transient StylesInlinerService stylesInlinerService;

    /**
     * This method gets the AEM page, uses the Styles Inliner Service to convert the AEM page html with css classes
     * into html with inline styles.
     *
     * @param request the sling request
     * @param resp    the sling response
     */
    @Override
    protected void doGet(final SlingHttpServletRequest request,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> params = new HashMap<>();
        Resource resource = request.getResource();
        String pagePath = resource.getPath();
        HttpServletRequest req = requestResponseFactory.createRequest("GET", pagePath + ".html",
                params);
        WCMMode.DISABLED.toRequest(req);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpServletResponse response = requestResponseFactory.createResponse(out);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        requestProcessor.processRequest(req, response, request.getResourceResolver());
        StylesInlinerContextAwareConfiguration configuration = getConfiguration(resource);
        String htmlWithInlineStyles =
                stylesInlinerService.getHtmlWithInlineStyles(request.getResourceResolver(), out.toString(StandardCharsets.UTF_8.name()),
                        configuration.stylesMergingMode(), configuration.htmlSanitizingMode());
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter pw = resp.getWriter();
        pw.write(htmlWithInlineStyles);
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

    private StylesInlinerContextAwareConfiguration getConfiguration(Resource resource) {
        StylesInlinerContextAwareConfiguration fallback = new StylesInlinerContextAwareConfiguration() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return StylesInlinerContextAwareConfiguration.class;
            }

            @Override
            public StyleMergerMode stylesMergingMode() {
                return StyleMergerMode.PROCESS_SPECIFICITY;
            }

            @Override
            public HtmlSanitizingMode htmlSanitizingMode() {
                return HtmlSanitizingMode.FULL;
            }
        };
        try {
            ConfigurationBuilder configurationBuilder = resource.adaptTo(ConfigurationBuilder.class);
            if (Objects.isNull(configurationBuilder)) {
                return fallback;
            }
            return configurationBuilder.as(StylesInlinerContextAwareConfiguration.class);
        } catch (Throwable e) {
            log("Error retrieving configuration: " + e.getMessage(), e);
        }
        return fallback;
    }

}
