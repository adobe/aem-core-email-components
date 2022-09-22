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
package com.adobe.cq.email.core.components.internal.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.email.core.components.models.Image;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.adobe.cq.wcm.core.components.models.datalayer.jackson.ComponentDataModelSerializer;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Image component model class
 */
@Model(
    adaptables = { Resource.class, SlingHttpServletRequest.class },
    adapters = { Image.class, ComponentExporter.class },
    resourceType = "core/email/components/image/v1/image",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ImageImpl implements Image {
    static final String DEFAULT_WIDTH_PROPERTY = "defaultWidth";
    static final Long DEFAULT_WIDTH = 1280L;

    @Self
    @Via(type = ResourceSuperType.class)
    protected com.adobe.cq.wcm.core.components.models.Image delegate;

    @Self
    protected SlingHttpServletRequest slingHttpServletRequest;

    @ScriptVariable
    protected Style currentStyle;

    @Inject
    protected ResourceResolver resourceResolver;

    @ValueMapValue
    protected Long fixedWidth;

    @ValueMapValue
    protected boolean scaleToFullWidth;

    @PostConstruct
    protected void initModel() {
        if ((Objects.isNull(fixedWidth) || fixedWidth == 0L) && Objects.nonNull(currentStyle)) {
            this.fixedWidth = currentStyle.get(DEFAULT_WIDTH_PROPERTY, DEFAULT_WIDTH);
        }
    }

    @Override
    public String getWidth() {
        if (scaleToFullWidth) {
            return null;
        }
        if (Objects.nonNull(fixedWidth) && fixedWidth != 0) {
            return fixedWidth.toString();
        }
        if (Objects.isNull(delegate)) {
            return null;
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
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getHeight();
    }

    /**
     * Getter for full width style
     *
     * @return the full width style
     */
    public String getInlineStyle() {
        Map<String, String> styles = new LinkedHashMap<>();

        if (scaleToFullWidth) {
            styles.put("width", "100%");
        } else {
            String width = getWidth();
            if (StringUtils.isNotEmpty(width)) {
                styles.put("width", width + "px");
            }
        }

        if (styles.isEmpty()) {
            return null;
        }

        StringBuilder style = new StringBuilder();
        for (Map.Entry<String, String> entry : styles.entrySet()) {
            style.append(entry.getKey()).append(':').append(entry.getValue()).append(';');
        }
        return style.toString();
    }

    @Override
    public String getSrc() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getSrc();
    }

    @Override
    public String getAlt() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getAlt();
    }

    @Override
    public String getTitle() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getTitle();
    }

    @Override
    public String getUuid() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getUuid();
    }

    @Override
    public Link getImageLink() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getImageLink();
    }

    @Override
    @Deprecated
    public String getLink() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getLink();
    }

    @Override
    public boolean displayPopupTitle() {
        if (Objects.isNull(delegate)) {
            return false;
        }
        return delegate.displayPopupTitle();
    }

    @Override
    @JsonIgnore
    public String getFileReference() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getFileReference();
    }

    @Override
    @JsonIgnore
    @Deprecated
    public String getJson() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getJson();
    }

    @Override
    @NotNull
    public int[] getWidths() {
        if (Objects.isNull(delegate)) {
            return new int[0];
        }
        return delegate.getWidths();
    }

    @Override
    public String getSrcUriTemplate() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getSrcUriTemplate();
    }

    @Override
    public boolean isLazyEnabled() {
        return false;
    }

    @Override
    public String getSrcset() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getSrcset();
    }

    @Override
    public int getLazyThreshold() {
        if (Objects.isNull(delegate)) {
            return 0;
        }
        return delegate.getLazyThreshold();
    }

    @Override
    public List<ImageArea> getAreas() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getAreas();
    }

    @Override
    public boolean isDecorative() {
        if (Objects.isNull(delegate)) {
            return false;
        }
        return delegate.isDecorative();
    }

    @Override
    public String getSmartCropRendition() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getSmartCropRendition();
    }

    @Override
    public boolean isDmImage() {
        if (Objects.isNull(delegate)) {
            return false;
        }
        return delegate.isDmImage();
    }

    @Override
    public ImageData getComponentData() {
        if (Objects.isNull(delegate)) {
            return null;
        }
        return delegate.getComponentData();
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
    public @Nullable ComponentData getData() {
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
