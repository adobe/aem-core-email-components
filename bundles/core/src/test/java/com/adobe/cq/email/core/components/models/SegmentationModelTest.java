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
import com.day.cq.wcm.api.components.ComponentManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins.CORE_COMPONENTS;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class SegmentationModelTest {

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
    }

    @Test
    void testSegmentationModel() {
        ctx.currentResource("/content/test-page/jcr:content/root/container/col-0/segmentation");
        ComponentManager componentManager = ctx.request().getResourceResolver().adaptTo(ComponentManager.class);
        underTest = ctx.request().adaptTo(Tabs.class);
        assertEquals("item_1655139752725", underTest.getActiveItem());
        assertNull(underTest.getAccessibilityLabel());
        assertNull(underTest.getAppliedCssClasses());
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
}
