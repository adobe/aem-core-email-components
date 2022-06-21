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

import com.adobe.cq.email.core.components.commons.editor.dialog.segmenteditor.Editor;
import com.adobe.cq.email.core.components.commons.editor.dialog.segmenteditor.SegmentItem;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Tabs;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.wcm.api.components.ComponentManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.stream.StreamSupport;

@Model(adaptables = SlingHttpServletRequest.class, adapters = Tabs.class, resourceType = SegmentationModel.RESOURCE_TYPE)
public class SegmentationModel implements Tabs {

    public static final String RESOURCE_TYPE = "core/email/components/segmentation/v1/segmentation";

    private List<ListItem> items;
    private String activeItemName;

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @Self
    @Via(type = ResourceSuperType.class)
    private Tabs tabs;

    @Override
    public String getActiveItem() {
            if (activeItemName == null) {
                this.activeItemName = Optional.ofNullable(request.getResourceResolver().adaptTo(ComponentManager.class))
                    .flatMap(componentManager -> StreamSupport.stream(resource.getChildren().spliterator(), false)
                        .filter(Objects::nonNull)
                        .filter(res -> Objects.nonNull(componentManager.getComponentOfResource(res)))
                        .filter(res -> res.getValueMap().get(SegmentItem.PN_CONDITION).equals("default"))
                        .findFirst()
                        .map(Resource::getName))
                    .orElse(null);
            }
            return activeItemName;
    }

    @Override
    public String getAccessibilityLabel() {
        return tabs.getAccessibilityLabel();
    }

    @Override
    public @NotNull List<ListItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
            ResourceResolver resourceResolver = request.getResourceResolver();
            List<ListItem> items = tabs.getItems();
            items.sort(new DefaultComparator(getActiveItem()));
            int pos = 1;
            for (ListItem item : items) {
                if (item != null) {
                    boolean isDefault = StringUtils.equals(item.getName(), getActiveItem());
                    String path = item.getPath();
                    if (StringUtils.isNotEmpty(path)) {
                        Resource itemResource = resourceResolver.getResource(path);
                        if (itemResource != null) {
                            ValueMap valueMap = itemResource.getValueMap();
                            String condition = valueMap.get(SegmentItem.PN_CONDITION, String.class);
                            if(StringUtils.equals(condition, Editor.CUSTOM_VALUE)) {
                                condition = valueMap.get(SegmentItem.PN_CUSTOM_SEGMENT_CONDITION, String.class);
                            }
                            if (StringUtils.isNotEmpty(condition)) {
                                this.items.add(new SegmentationItem(formatTag(condition, pos, items.size(), isDefault), item));
                                pos++;
                            }
                        }
                    }
                }
            }
        }
        return items;
    }

    private Pair<String, String> formatTag(String condition, int pos, int total, boolean isDefault) {
        String start = "<%% if (%s) { %%>";
        String middle = "<%% } else if (%s) { %%>";
        String closing = "<% } %>";
        String defaultOpeningSegment = "<% } else { %>";

        // We only have a default, so we can drop the markup
        if (total == 1 && isDefault) {
            return new ImmutablePair<>(StringUtils.EMPTY, StringUtils.EMPTY);
        }

        if (isDefault) {
            return new ImmutablePair<>(defaultOpeningSegment, closing);
        }

        if (pos == 1) {
            return new ImmutablePair<>(String.format(start, condition), (pos == total ? closing : StringUtils.EMPTY));
        } else {
            return new ImmutablePair<>(String.format(middle, condition), (pos == total ? closing : StringUtils.EMPTY));
        }
    }

    @Override
    public @Nullable String getBackgroundStyle() {
        return tabs.getBackgroundStyle();
    }

    @Override
    public @NotNull Map<String, ? extends ComponentExporter> getExportedItems() {
        return tabs.getExportedItems();
    }

    @Override
    public @NotNull String[] getExportedItemsOrder() {
        return tabs.getExportedItemsOrder();
    }

    @Override
    public @Nullable String getId() {
        return tabs.getId();
    }

    @Override
    public @Nullable ComponentData getData() {
        return tabs.getData();
    }

    @Override
    public @Nullable String getAppliedCssClasses() {
        return tabs.getAppliedCssClasses();
    }

    @Override
    public @NotNull String getExportedType() {
        return tabs.getExportedType();
    }

    private static class DefaultComparator implements Comparator<ListItem>, Serializable {

        private final String defaultName;

        public DefaultComparator(String defaultName) {
            this.defaultName = defaultName;
        }

        @Override
        public int compare(ListItem o1, ListItem o2) {
            if (StringUtils.equals(o1.getName(), defaultName)) {
                return 1;
            } else if (StringUtils.equals(o2.getName(), defaultName)) {
                return -1;
            }
            return 0;
        }
    }
}
