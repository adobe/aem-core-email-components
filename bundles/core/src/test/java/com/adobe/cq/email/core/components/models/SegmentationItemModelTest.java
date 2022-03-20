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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SegmentationItemModelTest {

    private SegmentationItemModel sut;

    @BeforeEach
    void setUp() {
        this.sut = new SegmentationItemModel();
    }

    @Test
    void getOpeningACCMarkup_NullCondition() {
        Assertions.assertNull(sut.getOpeningACCMarkup());
    }

    @Test
    void getOpeningACCMarkup_ExistingCondition() {
        setValue();
        Assertions.assertEquals("<% if (recipient.age >= 18) { %>", sut.getOpeningACCMarkup());
    }

    @Test
    void getClosingACCMarkup_NullCondition() {
        Assertions.assertNull(sut.getClosingACCMarkup());
    }

    @Test
    void getClosingACCMarkup_ExistingCondition() {
        setValue();
        Assertions.assertEquals("<% } %>", sut.getClosingACCMarkup());
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
}