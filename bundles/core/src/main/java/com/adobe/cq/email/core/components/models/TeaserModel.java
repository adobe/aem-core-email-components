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
package com.adobe.cq.email.core.components.models;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;


import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.email.core.components.services.AccLinkService;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.jackson.LinkHtmlAttributesSerializer;
import com.adobe.cq.wcm.core.components.internal.link.LinkImpl;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;

/**
 * Teaser component model class
 */

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = {Teaser.class, ComponentExporter.class},
       resourceType = "core/email/components/teaser/v1/teaser",
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TeaserModel implements Teaser {

    @Self
    @Via(type = ResourceSuperType.class)
    protected Teaser delegate;

    @ScriptVariable(name = "resource")
    protected Resource inheritedResource;

    @Self
    protected SlingHttpServletRequest slingHttpServletRequest;

    @OSGiService
    protected AccLinkService accLinkService;

    @Inject
    protected ResourceResolver resourceResolver;

    Link accLink;

    @PostConstruct
    protected void initModel() {
        if (Objects.nonNull(delegate)) {
            if (Objects.isNull(inheritedResource)) {
                return;
            }
            ValueMap props = inheritedResource.getValueMap();
            String linkURL = props.get("linkURL", String.class);
            accLink = accLinkService.create(resourceResolver, slingHttpServletRequest, linkURL);

        }
    }

    @Override
    public boolean isActionsEnabled() {
        if (Objects.isNull(delegate)) {
            return false;
        }
        return delegate.isActionsEnabled();
    }

    @Override
    public List<ListItem> getActions() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        List<ListItem> actions = delegate.getActions();
        for (int i = 0; i < actions.size(); i++) {
            ListItem action = actions.get(i);
            Link actionURL = action.getLink();
            if (actionURL != null) {
                convert(actionURL,
                        accLinkService.create(resourceResolver, slingHttpServletRequest, processUrl(actionURL.getURL())).getURL());
            }
        }
        return actions;
    }

    private String processUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        String encodedOpeningAccMarkup = URLEncoder.encode("<%");
        String encodedClosingAccMarkup = URLEncoder.encode("%>");
        if (url.contains(encodedOpeningAccMarkup)|| url.contains(encodedClosingAccMarkup)) {
            return URLDecoder.decode(url);
        }
        return url;
    }

    private Link convert(Link link, String url) {
       Link newLink = new LinkImpl<>(url,url,url,link.getReference(),link.getHtmlAttributes());
       return newLink;
    }

    @Override
    public @Nullable Link getLink() {
        return java.util.Optional.ofNullable(accLink).orElse(java.util.Optional.ofNullable(delegate).map(Teaser::getLink).orElse(null));
    }

    @Override
    @Deprecated
    public String getLinkURL() {
        return java.util.Optional
                .ofNullable(accLink).map(Link::getURL).orElse(java.util.Optional.ofNullable(delegate).map(Teaser::getLinkURL).orElse(null));
    }

    @Override
    public Resource getImageResource() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getImageResource();
    }

    @Override
    public boolean isImageLinkHidden() {
        if (Objects.isNull(delegate)) {
            return false;
        }
        return delegate.isImageLinkHidden();
    }

    @Override
    public String getTitle() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getTitle();
    }

    @Override
    public boolean isTitleLinkHidden() {
        if (Objects.isNull(delegate)) {
            return false;
        }
        return delegate.isTitleLinkHidden();
    }

    @Override
    public String getDescription() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getDescription();
    }

    @Override
    public String getTitleType() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getTitleType();
    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static final class LinkImpl<T> implements Link<T> {

        private static final Set<String> ALLOWED_ATTRIBUTES = new HashSet<String>() {
            {
                this.add("target");
                this.add("aria-label");
                this.add("title");
            }
        };
        private final String url;
        private final String mappedUrl;
        private final T reference;
        private final Map<String, String> htmlAttributes;
        private final String externalizedUrl;

        public LinkImpl(@Nullable String url, @Nullable String mappedUrl, @Nullable String externalizedUrl, @Nullable T reference, @Nullable Map<String, String> htmlAttributes) {
            this.url = url;
            this.mappedUrl = mappedUrl;
            this.externalizedUrl = externalizedUrl;
            this.reference = reference;
            this.htmlAttributes = buildHtmlAttributes(url, htmlAttributes);
        }

        public boolean isValid() {
            return this.url != null;
        }

        @JsonIgnore
        @Nullable
        public String getURL() {
            return this.url;
        }

        @JsonProperty("url")
        @Nullable
        public String getMappedURL() {
            return this.mappedUrl;
        }

        @JsonIgnore
        @Nullable
        public String getExternalizedURL() {
            return this.externalizedUrl;
        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JsonSerialize(
                using = LinkHtmlAttributesSerializer.class
        )
        @JsonProperty("attributes")
        @NotNull
        public Map<String, String> getHtmlAttributes() {
            return this.htmlAttributes;
        }

        @JsonIgnore
        @Nullable
        public T getReference() {
            return this.reference;
        }

        private static Map<String, String> buildHtmlAttributes(String linkURL, Map<String, String> htmlAttributes) {
            Map<String, String> attributes = new LinkedHashMap();
            if (linkURL != null) {
                attributes.put("href", linkURL);
            }

            if (htmlAttributes != null) {
                Map<String, String> filteredAttributes = (Map)htmlAttributes.entrySet().stream().filter((e) -> {
                    return ALLOWED_ATTRIBUTES.contains(e.getKey()) && StringUtils.isNotEmpty((CharSequence)e.getValue());
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                attributes.putAll(filteredAttributes);
            }

            return ImmutableMap.copyOf(attributes);
        }
    }

}