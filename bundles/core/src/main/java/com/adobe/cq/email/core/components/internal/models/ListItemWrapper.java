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

import java.util.Calendar;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.jackson.ComponentDataModelSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

class ListItemWrapper implements ListItem {

    private final ListItem delegate;

    ListItemWrapper(ListItem item) {
        this.delegate = item;
    }

    @Override public @Nullable Link getLink() {
        return delegate.getLink();
    }

    @Override @Deprecated @Nullable public String getURL() {
        return delegate.getURL();
    }

    @Override @Nullable public String getTitle() {
        return delegate.getTitle();
    }

    @Override @Nullable public String getDescription() {
        return delegate.getDescription();
    }

    @Override public @Nullable Calendar getLastModified() {
        return delegate.getLastModified();
    }

    @Override @Nullable public String getPath() {
        return delegate.getPath();
    }

    @Override @Nullable public String getName() {
        return delegate.getName();
    }

    @Override public @Nullable Resource getTeaserResource() {
        return delegate.getTeaserResource();
    }

    @Override @Nullable public String getId() {
        return delegate.getId();
    }

    @Override @JsonSerialize(using = ComponentDataModelSerializer.class) @JsonProperty("dataLayer")
    public @Nullable ComponentData getData() {
        return delegate.getData();
    }

    @Override @JsonProperty("appliedCssClassNames") @Nullable public String getAppliedCssClasses() {
        return delegate.getAppliedCssClasses();
    }

    @Override @NotNull public String getExportedType() {
        return delegate.getExportedType();
    }
}
