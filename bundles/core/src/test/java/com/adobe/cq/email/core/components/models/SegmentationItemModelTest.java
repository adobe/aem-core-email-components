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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SegmentationItemModelTest {

    @Mock
    Resource resource;
    @Mock
    Resource segmentationComponent;

    private SegmentationItemModel sut;

    @BeforeEach
    void setUp() {
        this.sut = new SegmentationItemModel();
    }

    @Test
    void nullCondition() {
        assertNull(sut.getOpeningACCMarkup());
        assertNull(sut.getClosingACCMarkup());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void existingCondition_RootLevel() {
        mockResources(false, false, false);
        setValue();
        assertEquals("<% if (recipient.age >= 18) { %>", sut.getOpeningACCMarkup());
        assertEquals("<% } %>", sut.getClosingACCMarkup());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void existingCondition_Child_Default() {
        mockResources(true, true, false);
        setValue();
        assertEquals("<% if (recipient.age >= 18) { %>", sut.getOpeningACCMarkup());
        assertEquals("<% } %>", sut.getClosingACCMarkup());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void existingCondition_Child_HasCondition() {
        mockResources(true, false, true);
        setValue();
        assertEquals("<% if (recipient.age >= 18) { %>", sut.getOpeningACCMarkup());
        assertEquals("<% } else if (recipient.age < 18) { %>", sut.getClosingACCMarkup());
    }

    private void setValue() {
        try {
            Field declaredField = SegmentationItemModel.class.getDeclaredField("condition");
            declaredField.setAccessible(true);
            declaredField.set(sut, "recipient.age >= 18");
            Method method = SegmentationItemModel.class.getDeclaredMethod("initModel");
            method.setAccessible(true);
            method.invoke(sut);
        } catch (Throwable e) {
            throw new RuntimeException(
                    "Error setting field " + "condition" + " with value " + "recipient.age >= 18" + ": " + e.getMessage(), e);
        }
    }

    private void mockResources(boolean hasChildren, boolean hasDefault, boolean hasCondition) {
        when(resource.getPath()).thenReturn("/resource-path");
        when(resource.getParent()).thenReturn(segmentationComponent);
        if (hasChildren) {
            List<Resource> children =
                    IntStream.range(0, 5).mapToObj(i -> {
                        Resource child = mock(Resource.class);
                        when(child.getPath()).thenReturn("/resource-path/child/" + i);
                        ValueMap valueMap = mock(ValueMap.class);
                        when(valueMap.get(eq("default"), eq(false))).thenReturn(hasDefault && i == 4);
                        when(valueMap.get(eq("condition"), anyString())).thenReturn(hasCondition ? "recipient.age < 18" : "");
                        when(child.getValueMap()).thenReturn(valueMap);
                        return child;
                    }).collect(Collectors.toList());
            when(segmentationComponent.getChildren()).thenReturn(children);
        }
        this.sut.resource = resource;
    }
}
