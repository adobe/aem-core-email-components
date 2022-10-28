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
package com.adobe.cq.email.core.commons.editor.dialog.segmenteditor;

import com.day.cq.wcm.api.policies.ContentPolicyMapping;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class EditorTest {

    private final AemContext ctx = new AemContext();
    private Editor underTest;

    @BeforeEach
    void setUp() {
        ctx.load().json("/editor/TestPage.json", "/content");
        ContentPolicyMapping contentPolicyMapping = ctx.contentPolicyMapping("core/email/components/segmentation/v1/segmentation", ImmutableMap.of());
        Resource resource = ctx.create().resource(contentPolicyMapping.getPolicy().getPath() + "/definedConditions");
        ctx.create().resource(resource, "item0", ImmutableMap.of("condition", "person.gender = w", "name", "woman"));
        ctx.create().resource(resource, "item1", ImmutableMap.of("condition", "person.gender = m", "name", "man"));
        ctx.create().resource(resource, "item2", ImmutableMap.of("condition", "person.age <= 12", "name", "child"));
        ctx.create().resource("/apps/core/email/components/title/v1/title");

    }

    @Test
    void testEditor() {
        ctx.requestPathInfo().setSuffix("/content/test-page/jcr:content/root/container/col-0/segmentation");
        underTest = ctx.request().adaptTo(Editor.class);
        assertEquals(5, underTest.getConditions().size());
        assertTrue(underTest.getConditions().stream()
            .anyMatch(condition -> "child".equals(condition.getName())));
        assertTrue(underTest.getConditions().stream()
            .anyMatch(condition -> "person.gender = w".equals(condition.getValue())));
        assertEquals("/content/test-page/jcr:content/root/container/col-0/segmentation", underTest.getContainer().getPath());
        assertEquals(3, underTest.getItems().size());
        assertTrue(underTest.getItems().stream()
            .map(SegmentItem.class::cast)
            .anyMatch(item -> "default".equals(item.getCondition())));
        assertTrue(underTest.getItems().stream()
            .map(SegmentItem.class::cast)
            .anyMatch(item -> "person.gender = m and person.age == 21".equals(item.getCustomCondition())));
        assertTrue(underTest.getItems().stream()
            .map(SegmentItem.class::cast)
            .anyMatch(item -> !item.isDisabled()));
    }
}
