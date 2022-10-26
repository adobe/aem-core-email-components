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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.commons.editor.dialog.segmenteditor.Editor;
import com.adobe.cq.email.core.commons.editor.dialog.segmenteditor.SegmentItem;
import com.adobe.cq.email.core.components.models.Segmentation;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Tabs;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.ComponentManager;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {SegmentationImpl.class, Segmentation.class},
        resourceType = SegmentationImpl.RESOURCE_TYPE
)
public class SegmentationImpl extends AbstractComponentImpl implements Segmentation {

    public static final String RESOURCE_TYPE = "core/email/components/segmentation/v1/segmentation";
    private static final Logger LOG = LoggerFactory.getLogger(SegmentationImpl.class);
    private static final String PN_PANEL_TITLE = "cq:panelTitle";

    private List<SegmentationItem> items;
    private String activeItemName;

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @OSGiService
    private LiveRelationshipManager liveRelationshipManager;

    @Self
    @Via(type = ResourceSuperType.class)
    private Tabs tabs;

    @Override
    public String getActiveItem() {
        if (activeItemName == null) {
            this.activeItemName = Optional.ofNullable(request.getResourceResolver().adaptTo(ComponentManager.class))
                    .flatMap(componentManager -> StreamSupport.stream(resource.getChildren().spliterator(), false)
                            .filter(Objects::nonNull)
                            .filter(res -> "default".equals(res.getValueMap().get(SegmentItem.PN_CONDITION)))
                            .filter(res -> Objects.nonNull(componentManager.getComponentOfResource(res)))
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

    @NotNull
    @Override
    public List<ListItem> getItems() {
        List<? extends ListItem> items = getSegmentationItems();
        return (List<ListItem>) items;
    }

    @NotNull
    @Override
    public List<SegmentationItem> getSegmentationItems() {
        if (items == null) {
            items = new ArrayList<>();
            ResourceResolver resourceResolver = request.getResourceResolver();
            List<Pair<ListItem, Resource>> items = tabs.getItems()
                .stream()
                .sorted(new DefaultComparator(getActiveItem()))
                .map(item -> {
                    String path = item != null ? item.getPath() : null;
                    Resource itemResource = path != null ? resourceResolver.getResource(path) : null;
                    return Pair.of(item, itemResource);
                })
                .filter(pair -> pair.getRight() != null)
                .collect(Collectors.toList());

            int pos = 1;
            int total =
                    (int) items.stream()
                            .filter(itemResourcePair -> {
                                Resource itemResource = itemResourcePair.getRight();
                                String condition = itemResource.getValueMap().get(SegmentItem.PN_CONDITION, String.class);
                                return !itemResource.isResourceType(SegmentItem.RT_GHOST) || StringUtils.isNotEmpty(condition);
                            }).count();

            for (Pair<ListItem, Resource> itemPair : items) {
                ListItem item = itemPair.getLeft();
                Resource itemResource = itemPair.getRight();
                ValueMap itemProperties = itemResource.getValueMap();
                boolean isDefault = StringUtils.equals(item.getName(), getActiveItem());
                String condition = itemProperties.get(SegmentItem.PN_CONDITION, String.class);

                if (StringUtils.equals(condition, Editor.CUSTOM_VALUE)) {
                    condition = itemProperties.get(SegmentItem.PN_CUSTOM_SEGMENT_CONDITION, String.class);
                }
                WCMMode mode = WCMMode.fromRequest(request);
                if (mode.equals(WCMMode.DISABLED)) {
                    if (StringUtils.isNotEmpty(condition) && !itemResource.isResourceType(SegmentItem.RT_GHOST)) {
                        this.items.add(new SegmentationItemImpl(formatTag(condition, pos, total, isDefault), item, itemResource, item.getTitle()));
                        pos++;
                    }
                } else {
                    try {
                        Resource sourceResource = Optional.ofNullable(liveRelationshipManager.getLiveRelationship(itemResource, false))
                                .filter(liveRelationship -> liveRelationship.getStatus().isCancelled())
                                .flatMap(liveRelationship -> Optional.ofNullable(
                                        resourceResolver.getResource(liveRelationship.getSourcePath())))
                                .orElse(null);
                        if (sourceResource != null) {
                            String title = Optional.ofNullable(sourceResource.getValueMap().get(PN_PANEL_TITLE, String.class))
                                    .orElseGet(() -> this.resource.getValueMap().get(JcrConstants.JCR_TITLE, String.class));
                            this.items.add(new SegmentationItemImpl(new ImmutablePair<>(StringUtils.EMPTY, StringUtils.EMPTY), item,
                                    itemResource, title));
                            continue;
                        }
                    } catch (WCMException e) {
                        LOG.error(e.getMessage());
                    }
                    this.items.add(new SegmentationItemImpl(new ImmutablePair<>(StringUtils.EMPTY, StringUtils.EMPTY), item, itemResource, item.getTitle()));
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

    @Nullable
    @Override
    public String getBackgroundStyle() {
        return tabs.getBackgroundStyle();
    }

    @NotNull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return tabs.getExportedItems();
    }

    @NotNull
    @Override
    public String[] getExportedItemsOrder() {
        return tabs.getExportedItemsOrder();
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

    /**
     * Segmentation item class
     */
    class SegmentationItemImpl implements Segmentation.SegmentationItem, ListItem {

        private final String openingACCMarkup;
        private final String closingACCMarkup;
        private final String title;
        private final ListItem listItem;
        private final Resource itemResource;

        public SegmentationItemImpl(Pair<String, String> markup, ListItem listItem, Resource itemResource, String title) {
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

        @Nullable
        @Override
        public String getId() {
            return ComponentUtils.generateId(
                    StringUtils.join(SegmentationImpl.this.getId(), "-", "item"),
                    this.itemResource.getPath());
        }

        @Override
        public @Nullable String getAppliedCssClasses() {
            return listItem.getAppliedCssClasses();
        }

        @Override
        public @NotNull String getExportedType() {
            return listItem.getExportedType();
        }

        @Override
        public String getProlog() {
            return openingACCMarkup;
        }

        @Override
        public String getEpilogue() {
            return closingACCMarkup;
        }
    }
}
