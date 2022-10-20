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
package com.adobe.cq.email.core.components.internal.util;

import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.internal.util.StyleProperty;
import com.adobe.cq.email.core.components.internal.util.StyleSpecificity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StylePropertyTest {

    @Test
    void testPojo() {
        StyleProperty sut = new StyleProperty();
        sut.setName("width");
        sut.setValue("100px");
        sut.setImportant(false);
        sut.setFullProperty("width: 100px");
        sut.setSpecificity(new StyleSpecificity(1, 0, 0, 0));
        assertEquals("width", sut.getName());
        assertEquals("100px", sut.getValue());
        assertFalse(sut.isImportant());
        assertEquals("width: 100px", sut.getFullProperty());
        assertEquals(new StyleSpecificity(1, 0, 0, 0), sut.getSpecificity());
    }

    @Test
    void testEquals() {
        StyleProperty first = new StyleProperty();
        first.setName("width");
        first.setValue("100px");
        first.setImportant(false);
        first.setFullProperty("width: 100px");
        first.setSpecificity(new StyleSpecificity(1, 0, 0, 0));
        StyleProperty second = new StyleProperty();
        second.setName("width");
        second.setValue("100px");
        second.setImportant(false);
        second.setFullProperty("width: 100px");
        second.setSpecificity(new StyleSpecificity(1, 0, 0, 0));
        assertTrue(first.equals(second));
    }

    @Test
    void testHashCode() {
        StyleProperty first = new StyleProperty();
        first.setName("width");
        first.setValue("100px");
        first.setImportant(false);
        first.setFullProperty("width: 100px");
        first.setSpecificity(new StyleSpecificity(1, 0, 0, 0));
        StyleProperty second = new StyleProperty();
        second.setName("width");
        second.setValue("100px");
        second.setImportant(false);
        second.setFullProperty("width: 100px");
        second.setSpecificity(new StyleSpecificity(1, 0, 0, 0));
        assertEquals(first.hashCode(), second.hashCode());
    }
}
