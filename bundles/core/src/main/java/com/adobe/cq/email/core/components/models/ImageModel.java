/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.adobe.cq.wcm.core.components.models.datalayer.jackson.ComponentDataModelSerializer;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = {Image.class, ComponentExporter.class},
       resourceType = "core/email/components/image",
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageModel implements Image {
    private static final String DEFAULT_WIDTH_PROPERTY = "defaultWidth";
    private static final Long DEFAULT_WIDTH = 1280L;

    @Self
    @Via(type = ResourceSuperType.class)
    private Image delegate;

    @ScriptVariable
    protected Style currentStyle;

    @ValueMapValue(name = "fixedWidth", injectionStrategy = InjectionStrategy.OPTIONAL)
    private Long fixedWidth;

    @ValueMapValue(name = "scaleToFullWidth", injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean scaleToFullWidth;

    @PostConstruct
    protected void initModel() {
        if (Objects.isNull(fixedWidth)) {
            this.fixedWidth = currentStyle.get(DEFAULT_WIDTH_PROPERTY, DEFAULT_WIDTH);
        }
        this.scaleToFullWidth = false;
    }

    @Override
    public String getWidth() {
        if (scaleToFullWidth) {
            return null;
        }
        if (Objects.nonNull(fixedWidth) && fixedWidth != 0) {
            return fixedWidth + "px";
        }
        return delegate.getWidth();
    }

    @Override
    public String getHeight() {
        if (scaleToFullWidth) {
            return null;
        }
        if (Objects.nonNull(fixedWidth) && fixedWidth != 0) {
            return null;
        }
        return delegate.getHeight();
    }

    public Long getFixedWidth() {
        return fixedWidth;
    }

    public boolean isScaleToFullWidth() {
        return scaleToFullWidth;
    }

    public String getRole() {
        if (isDecorative()) {
            return "presentation";
        }
        return null;
    }

    public String getFullWidthStyle() {
        if (scaleToFullWidth) {
            return "100%";
        }
        return "unset";
    }

    @Override
    public String getSrc() {
        return delegate.getSrc();
    }

    @Override
    public String getAlt() {
        return delegate.getAlt();
    }

    @Override
    public String getTitle() {
        return delegate.getTitle();
    }

    @Override
    public String getUuid() {
        return delegate.getUuid();
    }

    @Override
    public Link getImageLink() {
        return delegate.getImageLink();
    }

    @Override
    @Deprecated
    public String getLink() {
        return delegate.getLink();
    }

    @Override
    public boolean displayPopupTitle() {
        return delegate.displayPopupTitle();
    }

    @Override
    @JsonIgnore
    public String getFileReference() {
        return delegate.getFileReference();
    }

    @Override
    @JsonIgnore
    @Deprecated
    public String getJson() {
        return delegate.getJson();
    }

    @Override
    @NotNull
    public int[] getWidths() {
        return delegate.getWidths();
    }

    @Override
    public String getSrcUriTemplate() {
        return delegate.getSrcUriTemplate();
    }

    @Override
    public boolean isLazyEnabled() {
        return delegate.isLazyEnabled();
    }

    @Override
    public String getSrcset() {
        return delegate.getSrcset();
    }

    @Override
    public int getLazyThreshold() {
        return delegate.getLazyThreshold();
    }

    @Override
    public List<ImageArea> getAreas() {
        return delegate.getAreas();
    }

    @Override
    public boolean isDecorative() {
        return delegate.isDecorative();
    }

    @Override
    public String getSmartCropRendition() {
        return delegate.getSmartCropRendition();
    }

    @Override
    public boolean isDmImage() {
        return delegate.isDmImage();
    }

    @Override
    public ImageData getComponentData() {
        return delegate.getComponentData();
    }

    @Override
    @Nullable
    public String getId() {
        return delegate.getId();
    }

    @Override
    @JsonSerialize(using = ComponentDataModelSerializer.class)
    @JsonProperty("dataLayer")
    public @Nullable ComponentData getData() {
        return delegate.getData();
    }

    @Override
    @JsonProperty("appliedCssClassNames")
    @Nullable
    public String getAppliedCssClasses() {
        return delegate.getAppliedCssClasses();
    }

    @Override
    @NotNull
    public String getExportedType() {
        return delegate.getExportedType();
    }

}
