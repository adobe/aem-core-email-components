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
package com.adobe.cq.email.core.components.internal.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.email.core.components.internal.models.EmailPageImpl;
import com.adobe.cq.email.core.components.internal.util.InlinerResponseWrapper;
import com.adobe.cq.email.core.components.services.StylesInlinerService;
import com.day.cq.wcm.api.WCMMode;

/**
 * This servlet will take the AEM page as input, makes the styles inline
 * and returns the html with inline styles in response.
 */
@Component(service = {Servlet.class},
           immediate = true)
@SlingServletResourceTypes(
        resourceTypes = EmailPageImpl.RESOURCE_TYPE,
        selectors = StylesInlinerServlet.INLINE_STYLES_SELECTOR,
        extensions = "html")
public class StylesInlinerServlet extends SlingSafeMethodsServlet {

    /**
     * Selector for style inliner servlet
     */
    public static final String INLINE_STYLES_SELECTOR = "inline-styles";

    @Reference
    StylesInlinerService stylesInlinerService;

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
        // prepare forward request and response
        String selectors = Arrays.stream(request.getRequestPathInfo().getSelectors())
            .filter(selector -> !INLINE_STYLES_SELECTOR.equals(selector))
            .collect(Collectors.joining("."));
        InlinerResponseWrapper responseWrapper = new InlinerResponseWrapper(resp);
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        options.setReplaceSelectors(selectors);
        WCMMode.DISABLED.toRequest(request);

        // forward request
        RequestDispatcher dispatcher = request.getRequestDispatcher(request.getResource(), options);
        if (dispatcher == null) {
            throw new ServletException("Cannot forward request, dispatcher null");
        }
        dispatcher.forward(request, responseWrapper);

        // inline styles
        String content = responseWrapper.getResponseAsString();
        content = stylesInlinerService.getHtmlWithInlineStyles(request.getResourceResolver(), content);
        PrintWriter pw = resp.getWriter();
        pw.write(content);
    }

}
