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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

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
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.email.core.components.services.AccLinkService;
import com.adobe.cq.email.core.components.util.AccUrlProcessor;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;

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
        List<ListItem> newActions = new ArrayList<>();
        for (ListItem action : actions) {
            Link actionLink = action.getLink();
            if (actionLink != null) {
                newActions.add(
                        TeaserListItem.create(action, Optional.ofNullable(accLinkService.create(resourceResolver, slingHttpServletRequest,
                                AccUrlProcessor.process(actionLink.getURL()))).map(Link::getURL).orElse(null)));
            } else {
                newActions.add(TeaserListItem.create(action, null));
            }
        }
        return newActions;
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

}