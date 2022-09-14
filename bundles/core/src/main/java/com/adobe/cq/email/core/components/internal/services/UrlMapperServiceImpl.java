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

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.components.internal.request.EmptyHttpServletRequest;
import com.adobe.cq.email.core.components.internal.request.ResolverRequestWrapper;
import com.adobe.cq.email.core.components.services.UrlMapperService;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.WCMMode;

/**
 * {@link UrlMapperService} implementation
 */
@Component(service = UrlMapperService.class,
           immediate = true,
           scope = ServiceScope.SINGLETON)
@ServiceDescription("URL mapper service")
public class UrlMapperServiceImpl implements UrlMapperService {
    private static final Logger LOG = LoggerFactory.getLogger(UrlMapperServiceImpl.class.getName());
    @Reference
    Externalizer externalizer;

    @Override
    public String getMappedUrl(ResourceResolver resourceResolver, SlingHttpServletRequest request, String contentPath) {
        if (Objects.isNull(resourceResolver) || Objects.isNull(request) || StringUtils.isEmpty(contentPath)) {
            LOG.warn("Invalid parameters: resourceResolver={}, request={}, contentPath={}; returning contentPath", resourceResolver,
                    request, contentPath);
            return contentPath;
        }
        String fromResourceResolver = getFromResourceResolver(resourceResolver, request, contentPath);
        if (fromResourceResolver != null && fromResourceResolver.startsWith("http")) {
            return fromResourceResolver;
        }
        String fromExternalizer = getFromExternalizer(resourceResolver, contentPath, request);
        if (fromExternalizer != null) {
            return fromExternalizer;
        }
        LOG.warn("Absolute URL not retrieved successfully: returning contentPath {}", contentPath);
        return contentPath;
    }

    @Nullable
    private String getFromResourceResolver(ResourceResolver resourceResolver, SlingHttpServletRequest request, String contentPath) {
        try {
            String prefix = request.getRequestURL().toString().replace(request.getRequestURI(), "");
            EmptyHttpServletRequest emptyRequest = new EmptyHttpServletRequest();
            ResolverRequestWrapper wrapper = new ResolverRequestWrapper(emptyRequest, prefix);
            String mappedUrl = resourceResolver.map(wrapper, contentPath);
            if (StringUtils.isNotEmpty(mappedUrl) && !mappedUrl.equals(contentPath)) {
                return mappedUrl;
            }
        } catch (Throwable e) {
            LOG.warn("Error retrieving absolute URL from resource resolver: {} ", e.getMessage(), e);
        }
        return null;
    }

    @Nullable
    private String getFromExternalizer(ResourceResolver resourceResolver, String contentPath, SlingHttpServletRequest request) {
        try {
            String externalizerMode = WCMMode.DISABLED.equals(WCMMode.fromRequest(request)) ? Externalizer.PUBLISH : Externalizer.LOCAL;
            String externalLink = externalizer.externalLink(resourceResolver, externalizerMode, contentPath);
            if (StringUtils.isNotEmpty(externalLink)) {
                return externalLink;
            }
        } catch (Throwable e) {
            LOG.warn("Error retrieving absolute URL from externalizer: {}", e.getMessage(), e);
        }
        return null;
    }
}
