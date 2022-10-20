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

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;

/**
 * Segmentation item class
 */
public class SegmentationItem implements ListItem {

    private final String parentId;
    private final String openingACCMarkup;
    private final String closingACCMarkup;
    private final String title;
    private final ListItem listItem;
    private final Resource itemResource;

    public SegmentationItem(String parentId, Pair<String, String> markup, ListItem listItem, Resource itemResource, String title) {
        this.parentId = parentId;
        this.openingACCMarkup = markup.getLeft();
        this.closingACCMarkup = markup.getRight();
        this.listItem = listItem;
        this.itemResource = itemResource;
        this.title = title;
    }

    @Override
    public @Nullable Link getLink() {
        return listItem.getLink();
    }

    @Override
    public @Nullable String getURL() {
        return listItem.getURL();
    }

    @Override
    public @Nullable String getTitle() {
        return title;
    }

    @Override
    public @Nullable String getDescription() {
        return listItem.getDescription();
    }

    @Override
    public @Nullable Calendar getLastModified() {
        return listItem.getLastModified();
    }

    @Override
    public @Nullable String getPath() {
        return listItem.getPath();
    }

    @Override
    public @Nullable String getName() {
        return listItem.getName();
    }

    @Override
    public @Nullable Resource getTeaserResource() {
        return listItem.getTeaserResource();
    }

    @Override
    public @Nullable String getId() {
        return ComponentUtils.generateId(
            StringUtils.join(parentId, "-", "item"),
            this.itemResource.getPath());
    }

    @Override
    public @Nullable ComponentData getData() {
        return listItem.getData();
    }

    @Override
    public @Nullable String getAppliedCssClasses() {
        return listItem.getAppliedCssClasses();
    }

    @Override
    public @NotNull String getExportedType() {
        return listItem.getExportedType();
    }

    /**
     * Getter for the ACC markup opening tag
     *
     * @return the ACC markup opening tag
     */
    public String getOpeningACCMarkup() {
        return openingACCMarkup;
    }

    /**
     * Getter for the ACC markup closing tag
     *
     * @return the ACC markup closing tag
     */
    public String getClosingACCMarkup() {
        return closingACCMarkup;
    }
}



