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

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TeaserListItemTest {
    @Test
    void testCreateNullListItem() {
        assertNull(TeaserListItem.create(null, null));
    }

    @Test
    void testCreate() {
        String id = "ID";
        ComponentData data = mock(ComponentData.class);
        String appliedCssClasses = "APPLIED_CSS_CLASSES";
        String exportedType = "EXPORTED_TYPE";
        String url = "/url";
        String title = "TITLE";
        String description = "DESCRIPTION";
        Calendar lastModified = Calendar.getInstance();
        lastModified.clear();
        lastModified.set(2022, 6, 13, 10, 0, 55);
        String path = "PATH";
        String name = "NAME";
        Resource teaserResource = mock(Resource.class);
        String linkUrl = "/another/link/url";
        ListItem listItem = TeaserListItem.create(
                new InternalListItem(id, data, appliedCssClasses, exportedType, null, url, title, description, lastModified, path, name,
                        teaserResource), linkUrl);
        assertNotNull(listItem);
        assertEquals(id, listItem.getId());
        assertEquals(data, listItem.getData());
        assertEquals(appliedCssClasses, listItem.getAppliedCssClasses());
        assertNull(listItem.getLink());
        assertEquals(url, listItem.getURL());
        assertEquals(title, listItem.getTitle());
        assertEquals(description, listItem.getDescription());
        assertEquals(lastModified, listItem.getLastModified());
        assertEquals(path, listItem.getPath());
        assertEquals(name, listItem.getName());
        assertEquals(teaserResource, listItem.getTeaserResource());
    }

    @Test
    void testEquals() {
        String id = "ID";
        ComponentData data = mock(ComponentData.class);
        String appliedCssClasses = "APPLIED_CSS_CLASSES";
        String exportedType = "EXPORTED_TYPE";
        String url = "/url";
        String title = "TITLE";
        String description = "DESCRIPTION";
        Calendar lastModified = Calendar.getInstance();
        lastModified.clear();
        lastModified.set(2022, 6, 13, 10, 0, 55);
        String path = "PATH";
        String name = "NAME";
        Resource teaserResource = mock(Resource.class);
        String linkUrl = "/another/link/url";
        ListItem listItem1 = TeaserListItem.create(
                new InternalListItem(id, data, appliedCssClasses, exportedType, null, url, title, description, lastModified, path, name,
                        teaserResource), linkUrl);
        ListItem listItem2 = TeaserListItem.create(
                new InternalListItem(id, data, appliedCssClasses, exportedType, null, url, title, description, lastModified, path, name,
                        teaserResource), linkUrl);
        assertEquals(listItem1, listItem2);
    }

    @Test
    void testHashCode() {
        String id = "ID";
        ComponentData data = mock(ComponentData.class);
        String appliedCssClasses = "APPLIED_CSS_CLASSES";
        String exportedType = "EXPORTED_TYPE";
        String url = "/url";
        String title = "TITLE";
        String description = "DESCRIPTION";
        Calendar lastModified = Calendar.getInstance();
        lastModified.clear();
        lastModified.set(2022, Calendar.JUNE, 13, 10, 0, 55);
        String path = "PATH";
        String name = "NAME";
        Resource teaserResource = mock(Resource.class);
        String linkUrl = "/another/link/url";
        ListItem listItem1 = TeaserListItem.create(
                new InternalListItem(id, data, appliedCssClasses, exportedType, null, url, title, description, lastModified, path, name,
                        teaserResource), linkUrl);
        ListItem listItem2 = TeaserListItem.create(
                new InternalListItem(id, data, appliedCssClasses, exportedType, null, url, title, description, lastModified, path, name,
                        teaserResource), linkUrl);
        assertNotNull(listItem1);
        assertNotNull(listItem2);
        assertEquals(listItem1.hashCode(), listItem2.hashCode());
    }

    private static class InternalListItem implements ListItem {
        private final String id;
        private final ComponentData data;
        private final String appliedCssClasses;
        private final String exportedType;
        private final Link link;
        private final String url;
        private final String title;
        private final String description;
        private final Calendar lastModified;
        private final String path;
        private final String name;
        private final Resource teaserResource;

        private InternalListItem(String id, ComponentData data, String appliedCssClasses, String exportedType, Link link, String url,
                                 String title, String description, Calendar lastModified, String path, String name,
                                 Resource teaserResource) {
            this.id = id;
            this.data = data;
            this.appliedCssClasses = appliedCssClasses;
            this.exportedType = exportedType;
            this.link = link;
            this.url = url;
            this.title = title;
            this.description = description;
            this.lastModified = lastModified;
            this.path = path;
            this.name = name;
            this.teaserResource = teaserResource;
        }

        @Override
        public @Nullable Link getLink() {
            return link;
        }

        @Override
        public @Nullable String getURL() {
            return url;
        }

        @Override
        public @Nullable String getTitle() {
            return title;
        }

        @Override
        public @Nullable String getDescription() {
            return description;
        }

        @Override
        public @Nullable Calendar getLastModified() {
            return lastModified;
        }

        @Override
        public @Nullable String getPath() {
            return path;
        }

        @Override
        public @Nullable String getName() {
            return name;
        }

        @Override
        public @Nullable Resource getTeaserResource() {
            return teaserResource;
        }

        @Override
        public @Nullable String getId() {
            return id;
        }

        @Override
        public @Nullable ComponentData getData() {
            return data;
        }

        @Override
        public @Nullable String getAppliedCssClasses() {
            return appliedCssClasses;
        }

        @Override
        public @NotNull String getExportedType() {
            return exportedType;
        }

    }
}