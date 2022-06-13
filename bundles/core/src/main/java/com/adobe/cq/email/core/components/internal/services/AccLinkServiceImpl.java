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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.components.services.AccLinkService;
import com.adobe.cq.email.core.components.services.UrlMapperService;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;

/**
 * {@link AccLinkService} implementation
 */
@Component(service = AccLinkService.class,
           immediate = true,
           scope = ServiceScope.SINGLETON)
@ServiceDescription("ACC link service")
public class AccLinkServiceImpl implements AccLinkService {
    private static final Logger LOG = LoggerFactory.getLogger(AccLinkServiceImpl.class);

    @Reference
    UrlMapperService urlMapperService;

    @Override
    public Link create(ResourceResolver resourceResolver, SlingHttpServletRequest request, String url) {
        if (Objects.isNull(resourceResolver) || Objects.isNull(request) || StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            boolean hasAccMarkup = (url.contains("<%") || url.contains("%>"));
            if (hasAccMarkup) {
                return new AccLink(url);
            }
            String mappedUrl = urlMapperService.getMappedUrl(resourceResolver, request, url);
            if (StringUtils.isEmpty(mappedUrl)) {
                return null;
            }
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (Objects.nonNull(pageManager) && Objects.nonNull(pageManager.getPage(url))) {
                mappedUrl = mappedUrl + ".html";
            }
            return new AccLink<>(mappedUrl);
        } catch (Throwable e) {
            LOG.warn("Error trying to create ACC link: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Minimal {@link Link} implementation for an ACC instance
     *
     * @param <T> reference type (unused)
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static final class AccLink<T> implements Link<T> {

        public static final String ATTR_HREF = "href";

        private final String url;
        private final Map<String, String> htmlAttributes;

        public AccLink(@Nullable String url) {
            this.url = url;
            this.htmlAttributes = buildHtmlAttributes(url);
        }

        /**
         * Getter exposing if link is valid.
         *
         * @return {@code true} only if url is not null
         */
        @Override
        public boolean isValid() {
            return url != null;
        }

        /**
         * Getter for link URL.
         *
         * @return Link URL, can be {@code null} if link is not valid
         */
        @Override
        @JsonIgnore
        public @Nullable String getURL() {
            return url;
        }

        /**
         * Getter for the processed URL.
         *
         * @return Processed link URL, can be {@code null} if link is not valid or no processors are defined
         */
        @Override
        @JsonProperty("url")
        public @Nullable String getMappedURL() {
            return url;
        }

        @Override
        @JsonIgnore
        public @Nullable String getExternalizedURL() {
            return url;
        }

        /**
         * Getter for link HTML attributes.
         *
         * @return {@link Map} of HTML attributes, may include the URL as {@code href}
         */
        @Override
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JsonSerialize
        @JsonProperty("attributes")
        public @NotNull Map<String, String> getHtmlAttributes() {
            return htmlAttributes;
        }

        /**
         * Getter for link reference, if existing.
         *
         * @return Link referenced WCM/DAM entity or {@code null} if link does not point to one
         */
        @Override
        @JsonIgnore
        public @Nullable T getReference() {
            return null;
        }

        /**
         * Builds link HTML attributes.
         *
         * @param url Link URL
         * @return {@link Map} of link attributes
         */
        private static Map<String, String> buildHtmlAttributes(String url) {
            Map<String, String> attributes = new LinkedHashMap<>();
            if (url != null) {
                attributes.put(ATTR_HREF, url);
            }
            return ImmutableMap.copyOf(attributes);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AccLink<?> accLink = (AccLink<?>) o;
            return Objects.equals(url, accLink.url) && Objects.equals(htmlAttributes, accLink.htmlAttributes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url, htmlAttributes);
        }
    }
}
