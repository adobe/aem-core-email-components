
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContainerModelTest {

    @Test
    void layout6() {
        ContainerModel sut = new ContainerModel();
        setUp(sut, "6");
        assertEquals(1, sut.getColumns());
        assertEquals("grid-6", sut.getColClass1());
        assertNull(sut.getColClass2());
        assertNull(sut.getColClass3());
    }

    @Test
    void layout33() {
        ContainerModel sut = new ContainerModel();
        setUp(sut, "3-3");
        assertEquals(2, sut.getColumns());
        assertEquals("grid-3", sut.getColClass1());
        assertEquals("grid-3", sut.getColClass2());
        assertNull(sut.getColClass3());
    }

    @Test
    void layout24() {
        ContainerModel sut = new ContainerModel();
        setUp(sut, "2-4");
        assertEquals(2, sut.getColumns());
        assertEquals("grid-2", sut.getColClass1());
        assertEquals("grid-4", sut.getColClass2());
        assertNull(sut.getColClass3());
    }

    @Test
    void layout42() {
        ContainerModel sut = new ContainerModel();
        setUp(sut, "4-2");
        assertEquals(2, sut.getColumns());
        assertEquals("grid-4", sut.getColClass1());
        assertEquals("grid-2", sut.getColClass2());
        assertNull(sut.getColClass3());
    }

    @Test
    void layout222() {
        ContainerModel sut = new ContainerModel();
        setUp(sut, "2-2-2");
        assertEquals(3, sut.getColumns());
        assertEquals("grid-2", sut.getColClass1());
        assertEquals("grid-2", sut.getColClass2());
        assertEquals("grid-2", sut.getColClass3());
    }

    private void setUp(ContainerModel containerModel,
                       String layout) {
        try {
            Field layoutField = ContainerModel.class.getDeclaredField("layout");
            layoutField.setAccessible(true);
            layoutField.set(containerModel, layout);
            Method initMethod = ContainerModel.class.getDeclaredMethod("initModel");
            initMethod.setAccessible(true);
            initMethod.invoke(containerModel);
        } catch (Throwable e) {
            throw new RuntimeException("Error!");
        }
    }

}