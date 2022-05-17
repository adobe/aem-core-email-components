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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.email.core.components.services.AccLinkService;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.Title;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;

/**
 * Title component model class
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = {Title.class, ComponentExporter.class},
       resourceType = "core/email/components/title",
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TitleModel implements Title {

    @Self
    @Via(type = ResourceSuperType.class)
    protected Title delegate;

    @ScriptVariable(name = "resource")
    protected Resource inheritedResource;

    @Self
    protected SlingHttpServletRequest slingHttpServletRequest;

    @OSGiService
    protected AccLinkService accLinkService;

    @Inject
    protected ResourceResolver resourceResolver;

    private Link accLink;

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
    public String getText() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getText();
    }

    @Override
    public String getType() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getType();
    }

    @Override
    public @Nullable Link getLink() {
        return Optional.ofNullable(accLink).orElse(Optional.ofNullable(delegate).orElse(new TitleModel()).getLink());
    }

    @Override
    public String getLinkURL() {
        return Optional.ofNullable(
                accLink).map(Link::getURL).orElse(Optional.ofNullable(delegate).orElse(new TitleModel()).getLinkURL());
    }

    @Override
    public boolean isLinkDisabled() {
        return false;
    }

    @Override
    public @Nullable String getId() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getId();
    }

    @Override
    public @Nullable ComponentData getData() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getData();
    }

    @Override
    public @Nullable String getAppliedCssClasses() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getAppliedCssClasses();
    }

    @Override
    public @NotNull String getExportedType() {
        if (Objects.isNull(delegate)) {
            return "";
        }
        return delegate.getExportedType();
    }

}
