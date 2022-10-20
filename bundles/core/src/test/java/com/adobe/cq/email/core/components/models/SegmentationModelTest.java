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

import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.models.Tabs;
import com.adobe.cq.wcm.core.components.testing.MockExternalizerFactory;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.LiveStatus;
import com.day.cq.wcm.api.components.ComponentManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins.CORE_COMPONENTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SegmentationModelTest {

    @Mock
    LiveRelationshipManager liveRelationshipManager;

    @Mock
    LiveRelationship liveRelationship;

    @Mock
    LiveStatus liveStatus;

    private final AemContext ctx = new AemContextBuilder()
        .beforeSetUp(context -> {
            SlingModelFilter mockSlingModelFilter = Mockito.mock(SlingModelFilter.class);
            context.registerService(Externalizer.class, MockExternalizerFactory.getExternalizerService());
            context.registerService(SlingModelFilter.class, mockSlingModelFilter);
        })
        .plugin(CORE_COMPONENTS)
        .build();

    private Tabs underTest;

    @BeforeEach
    void setUp() {
        ctx.load().json("/segmentation/TestPage.json", "/content");
        ctx.load().json("/segmentation/TestApps.json", "/apps/core");
        ctx.registerService(LiveRelationshipManager.class, liveRelationshipManager);
    }

    @Test
    void testSegmentationModel() {
        ctx.currentResource("/content/test-page/jcr:content/root/container/col-0/segmentation");
        underTest = ctx.request().adaptTo(Tabs.class);
        assertEquals("item_1655139752725", underTest.getActiveItem());
        assertNull(underTest.getAccessibilityLabel());
        assertNull(underTest.getAppliedCssClasses());
        assertNull(underTest.getBackgroundStyle());
        assertNotNull(underTest.getExportedItems());
        assertEquals("tabs-4adb8faf29", underTest.getId());
        assertEquals("core/wcm/components/tabs/v1/tabs", underTest.getExportedType());
        assertNull(underTest.getData());
        assertNotNull(underTest.getExportedItemsOrder());
        SegmentationItem segmentationItem = underTest.getItems().stream()
            .map(SegmentationItem.class::cast)
            .filter(item -> "item_1655139922180".equals(item.getName())).findFirst().get();
        assertNotNull(segmentationItem);
        assertEquals("", segmentationItem.getClosingACCMarkup());
        assertEquals("<% if (person.age <= 21) { %>", segmentationItem.getOpeningACCMarkup());
        assertNull(segmentationItem.getAppliedCssClasses());
        assertNull(segmentationItem.getData());
        assertNull(segmentationItem.getLastModified());
        assertNull(segmentationItem.getDescription());

        assertEquals("tabs-4adb8faf29-item-36bea02a56", segmentationItem.getId());
        assertEquals("core/email/components/title/v1/title", segmentationItem.getExportedType());
        assertNull(segmentationItem.getLink());
        assertEquals("/content/test-page/jcr:content/root/container/col-0/segmentation/item_1655139922180", segmentationItem.getPath());
        assertEquals("Children", segmentationItem.getTitle());
        assertNull(segmentationItem.getURL());
        assertNull(segmentationItem.getTeaserResource());
    }

    @Test
    void testSingleDefaultSegment() {
        ctx.currentResource("/content/test-page/jcr:content/root/container/col-0/segmentation-single-default");
        underTest = ctx.request().adaptTo(Tabs.class);
        SegmentationItem segmentationItem = underTest.getItems().stream()
            .map(SegmentationItem.class::cast)
            .filter(item -> "item_1655139752725".equals(item.getName())).findFirst().get();
        assertEquals(StringUtils.EMPTY, segmentationItem.getOpeningACCMarkup());
        assertEquals(StringUtils.EMPTY, segmentationItem.getClosingACCMarkup());
    }

    @Test
    void testSingleGhostSegment() throws WCMException {
        Resource resource = ctx.currentResource("/content/test-page/jcr:content/root/container/col-0/segmentation-single-ghost");
        MockSlingHttpServletRequest request = ctx.request();
        request.setAttribute(WCMMode.REQUEST_ATTRIBUTE_NAME, WCMMode.EDIT);
        when(liveRelationshipManager.getLiveRelationship(any(Resource.class), anyBoolean())).thenReturn(liveRelationship);
        when(liveRelationship.getStatus()).thenReturn(liveStatus);
        when(liveStatus.isCancelled()).thenReturn(true);
        when(liveRelationship.getSourcePath()).thenReturn("/content/test-page/jcr:content/root/container/col-0/segmentation-single" +
                "-default/item_1655139752725");
        SegmentationModel underTest = request.adaptTo(SegmentationModel.class);
        SegmentationItem segmentationItem = underTest.getSegmentationItems().stream()
            .map(SegmentationItem.class::cast)
            .filter(item -> "item_1655139752725".equals(item.getName())).findFirst().get();
        assertEquals("Default", segmentationItem.getTitle());
    }



    @Test
    void testSingleCustomSegment() {
        ctx.currentResource("/content/test-page/jcr:content/root/container/col-0/segmentation-single-custom");
        underTest = ctx.request().adaptTo(Tabs.class);
        SegmentationItem segmentationItem = underTest.getItems().stream()
            .map(SegmentationItem.class::cast)
            .filter(item -> "item_1655139752725".equals(item.getName())).findFirst().get();
        assertEquals("<% if (person.gender = m and person.age == 21) { %>", segmentationItem.getOpeningACCMarkup());
        assertEquals("<% } %>", segmentationItem.getClosingACCMarkup());
    }

    @Test
    void testReverseSegment() {
        ctx.currentResource("/content/test-page/jcr:content/root/container/col-0/segmentation-reverse-order");
        underTest = ctx.request().adaptTo(Tabs.class);
        SegmentationItem segmentationItem = underTest.getItems().stream()
            .map(SegmentationItem.class::cast)
            .findFirst().get();
        assertEquals("item_1655205745101", segmentationItem.getName());
    }
}
