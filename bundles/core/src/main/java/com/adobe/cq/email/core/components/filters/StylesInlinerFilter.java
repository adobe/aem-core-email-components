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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.components.services.StylesInlinerService;

@Component(
        service = Filter.class,
        property = {
            "service.ranking:Integer=-2502",
            "sling.filter.extensions=json",
            "sling.filter.extensions=html",
            "sling.filter.pattern=/content/campaigns/.*",
            "sling.filter.scope=include",
            "sling.filter.selectors=campaign",
            "sling.filter.selectors=content"
        }
)
public class StylesInlinerFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(StylesInlinerFilter.class);
    static final String RESOURCE_TYPE = "core/email/components/page";

    private static final List<String> CONTENT_TYPES = new ArrayList<>();
    static {
        CONTENT_TYPES.add("text/");
        CONTENT_TYPES.add("application/json");
        CONTENT_TYPES.add("application/xml");
        CONTENT_TYPES.add("application/xhtml+xml");
    }


    @Reference
    private transient StylesInlinerService stylesInlinerService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        InlinerResponseWrapper wrapper = new InlinerResponseWrapper((HttpServletResponse) response);

        boolean touched = false;
        if (filterChain == null) {
            LOG.error("Filterchain is null.");
        } else {
            filterChain.doFilter(request, wrapper);
            touched = process(request, response, wrapper);
        }
        if (!touched) {
            response.getWriter().write(wrapper.getResponseAsString());
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }

    /**
     * Moved to separate method to better test this code.
     * @param request   Current request
     * @param response  current response
     * @param wrapper   our inliner wrapper object
     * @return  true, if we processed response object
     * @throws IOException  could happen
     */
    protected boolean process(ServletRequest request, ServletResponse response, InlinerResponseWrapper wrapper) throws IOException {
        boolean touched = false;
        if (request instanceof SlingHttpServletRequest && isValidContent(response)) {
            long startTime = System.currentTimeMillis();
            SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
            Resource resource = slingRequest.getResource();
            if (StringUtils.startsWith(resource.getPath(), "/content/campaigns/") && resource.isResourceType("core/email/components/page")) {
                LOG.trace("Resource: {}", slingRequest.getResource().getPath());
                LOG.trace("ResourceType: {}", slingRequest.getResource().getResourceType());
                String content = null;
                if (wrapper.getResponseAsString() != null) {
                    content = wrapper.getResponseAsString();
                }
                String replacedContent = stylesInlinerService.getHtmlWithInlineStyles(slingRequest.getResourceResolver(), content);
                LOG.trace("Replaced content. New response: {}.", replacedContent);
                response.getWriter().write(replacedContent);
                response.getWriter().close();
                touched = true;
                LOG.debug("Processing time: {} ms.", System.currentTimeMillis() - startTime);
            } else {
                LOG.trace("Path {} is not processed since it is not a campaign path.", resource.getPath());
            }
        } else {
            LOG.debug("Request is not a SlingHttpServletRequest or content type {} is not valid.", response.getContentType());
        }
        return touched;
    }

    /**
     * Check if response has the right content type.
     * @param response  response object
     * @return          true if correct - we do not process digital files
     */
    private boolean isValidContent(ServletResponse response) {
        String contentType = response.getContentType();
        boolean returnValue = false;
        if (StringUtils.isNotEmpty(contentType)) {
            for (String configEntry : CONTENT_TYPES) {
                if (StringUtils.startsWith(contentType, configEntry)) {
                    returnValue = true;
                    LOG.trace("Content type {} is valid because of config entry {}.", contentType, configEntry);
                    break;
                }
            }
        }
        if (!returnValue) {
            LOG.trace("Content type {} is not valid. Return false.", contentType);
        }
        return returnValue;
    }
}
