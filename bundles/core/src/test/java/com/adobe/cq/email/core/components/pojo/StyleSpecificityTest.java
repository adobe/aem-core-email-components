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
package com.adobe.cq.email.core.components.pojo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StyleSpecificityTest {

    @Test
    void compareToNull() {
        StyleSpecificity sut = new StyleSpecificity(0, 0, 0, 1);
        assertEquals(1, sut.compareTo(null));
    }

    @Test
    void compareToSameValue() {
        StyleSpecificity sut = new StyleSpecificity(0, 0, 0, 0);
        StyleSpecificity target = new StyleSpecificity(0, 0, 0, 0);
        assertEquals(0, sut.compareTo(target));
    }

    @Test
    void compareToLesserStyleAttribute() {
        StyleSpecificity sut = new StyleSpecificity(1, 0, 0, 0);
        StyleSpecificity target = new StyleSpecificity(0, 0, 0, 1);
        assertEquals(1, sut.compareTo(target));
    }

    @Test
    void compareToGreaterStyleAttribute() {
        StyleSpecificity sut = new StyleSpecificity(0, 0, 0, 0);
        StyleSpecificity target = new StyleSpecificity(1, 0, 0, 0);
        assertEquals(-1, sut.compareTo(target));
    }

    @Test
    void compareToLesserId() {
        StyleSpecificity sut = new StyleSpecificity(0, 1, 0, 0);
        StyleSpecificity target = new StyleSpecificity(0, 0, 0, 1);
        assertEquals(1, sut.compareTo(target));
    }

    @Test
    void compareToGreaterId() {
        StyleSpecificity sut = new StyleSpecificity(0, 0, 0, 1);
        StyleSpecificity target = new StyleSpecificity(0, 1, 0, 0);
        assertEquals(-1, sut.compareTo(target));
    }

    @Test
    void compareToLesserClassAttribute() {
        StyleSpecificity sut = new StyleSpecificity(0, 0, 1, 0);
        StyleSpecificity target = new StyleSpecificity(0, 0, 0, 1);
        assertEquals(1, sut.compareTo(target));
    }

    @Test
    void compareToGreaterClassAttribute() {
        StyleSpecificity sut = new StyleSpecificity(0, 0, 0, 1);
        StyleSpecificity target = new StyleSpecificity(0, 0, 1, 0);
        assertEquals(-1, sut.compareTo(target));
    }

    @Test
    void compareToLesserElements() {
        StyleSpecificity sut = new StyleSpecificity(0, 0, 0, 2);
        StyleSpecificity target = new StyleSpecificity(0, 0, 0, 1);
        assertEquals(1, sut.compareTo(target));
    }

    @Test
    void compareToGreaterElements() {
        StyleSpecificity sut = new StyleSpecificity(0, 0, 0, 1);
        StyleSpecificity target = new StyleSpecificity(0, 0, 0, 2);
        assertEquals(-1, sut.compareTo(target));
    }

    @Test
    void hashCalculationSuccess() {
        StyleSpecificity sut = new StyleSpecificity(0, 0, 1, 2);
        assertEquals(923554, sut.hashCode());
    }
}