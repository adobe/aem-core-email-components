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

import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;

public class TeaserListItem implements ListItem {

    private String id;
    private ComponentData data;
    private String appliedCssClasses;
    private String exportedType;
    private Link link;
    private String url;
    private String title;
    private String description;
    private Calendar lastModified;
    private String path;
    private String name;
    private Resource teaserResource;

    public TeaserListItem() {
        //do nothing
    }

    @Override
    public @Nullable Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    @Override
    public @Nullable String getURL() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public @Nullable String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public @Nullable String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public @Nullable Calendar getLastModified() {
        return lastModified;
    }

    public void setLastModified(Calendar lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public @Nullable String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public @Nullable String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public @Nullable Resource getTeaserResource() {
        return teaserResource;
    }

    public void setTeaserResource(Resource teaserResource) {
        this.teaserResource = teaserResource;
    }

    @Override
    public @Nullable String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public @Nullable ComponentData getData() {
        return data;
    }

    public void setData(ComponentData data) {
        this.data = data;
    }

    @Override
    public @Nullable String getAppliedCssClasses() {
        return appliedCssClasses;
    }

    public void setAppliedCssClasses(String appliedCssClasses) {
        this.appliedCssClasses = appliedCssClasses;
    }

    @Override
    public @NotNull String getExportedType() {
        return exportedType;
    }

    public void setExportedType(String exportedType) {
        this.exportedType = exportedType;
    }

    public static ListItem create(ListItem listItem, String linkUrl) {
        Optional<ListItem> optionalListItem = Optional.ofNullable(listItem);
        TeaserListItem copy = new TeaserListItem();
        copy.setId(optionalListItem.map(ListItem::getId).orElse(null));
        copy.setData(optionalListItem.map(ListItem::getData).orElse(null));
        copy.setAppliedCssClasses(optionalListItem.map(ListItem::getAppliedCssClasses).orElse(null));
        copy.setExportedType(
                optionalListItem.map(ListItem::getExportedType).orElse(null));
        copy.setLink(TeaserLink.create(optionalListItem.map(ListItem::getLink).orElse(null), linkUrl));
        copy.setUrl(optionalListItem.map(ListItem::getURL).orElse(null));
        copy.setTitle(optionalListItem.map(ListItem::getTitle).orElse(null));
        copy.setDescription(optionalListItem.map(ListItem::getDescription).orElse(null));
        copy.setLastModified(
                optionalListItem.map(ListItem::getLastModified).orElse(null));
        copy.setPath(optionalListItem.map(ListItem::getPath).orElse(null));
        copy.setName(optionalListItem.map(ListItem::getName).orElse(null));
        copy.setTeaserResource(optionalListItem.map(ListItem::getTeaserResource).orElse(null));
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TeaserListItem item = (TeaserListItem) o;
        return Objects.equals(id, item.id) && Objects.equals(data, item.data) &&
                Objects.equals(appliedCssClasses, item.appliedCssClasses) &&
                Objects.equals(exportedType, item.exportedType) && Objects.equals(link, item.link) &&
                Objects.equals(url, item.url) && Objects.equals(title, item.title) &&
                Objects.equals(description, item.description) && Objects.equals(lastModified, item.lastModified) &&
                Objects.equals(path, item.path) && Objects.equals(name, item.name) &&
                Objects.equals(teaserResource, item.teaserResource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data, appliedCssClasses, exportedType, link, url, title, description, lastModified, path, name,
                teaserResource);
    }
}
