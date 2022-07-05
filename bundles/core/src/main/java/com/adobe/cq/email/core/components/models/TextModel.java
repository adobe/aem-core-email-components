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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.email.core.components.util.HrefProcessor;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.Text;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.jackson.ComponentDataModelSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Text component model class
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = {Text.class, ComponentExporter.class},
       resourceType = "core/email/components/text/v1/text",
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TextModel implements Text {
    @Self
    @Via(type = ResourceSuperType.class)
    protected Text delegate;

    @Override
    public String getText() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        String text = delegate.getText();
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        return HrefProcessor.process(text);
    }

    @Override
    public boolean isRichText() {
        if (Objects.isNull(delegate)) {
            return false;
        }
        return delegate.isRichText();
    }

    @Override
    @Nullable
    public String getId() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getId();
    }

    @Override
    @JsonSerialize(using = ComponentDataModelSerializer.class)
    @JsonProperty("dataLayer")
    @Nullable
    public ComponentData getData() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getData();
    }

    @Override
    @JsonProperty("appliedCssClassNames")
    @Nullable
    public String getAppliedCssClasses() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getAppliedCssClasses();
    }

    @Override
    @NotNull
    public String getExportedType() {
        if (Objects.isNull(delegate)) {
            return "";
        }
        return delegate.getExportedType();
    }
}
