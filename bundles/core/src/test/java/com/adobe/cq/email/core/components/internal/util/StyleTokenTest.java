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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.internal.util.StyleSpecificity;
import com.adobe.cq.email.core.components.internal.util.StyleToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StyleTokenTest {

    private StyleToken sut;

    @BeforeEach
    void setUp() {
        this.sut = new StyleToken();
    }

    @Test
    public void getSelector() {
        String selector = "SELECTOR";
        sut.setSelector(selector);
        assertEquals(selector, sut.getSelector());
    }

    @Test
    public void getSplitSelectors() {
        assertTrue(sut.getJsoupSelectors().isEmpty());
    }

    @Test
    public void getProperties() {
        assertTrue(sut.getProperties().isEmpty());
    }

    @Test
    public void getSpecificity() {
        StyleSpecificity specificity = new StyleSpecificity(0, 1, 0, 0);
        sut.setSpecificity(specificity);
        assertEquals(specificity, sut.getSpecificity());
    }

    @Test
    public void isMediaQuery() {
        sut.setMediaQuery(true);
        assertTrue(sut.isMediaQuery());
    }

    @Test
    public void isPseudoSelector() {
        sut.setPseudoSelector(true);
        assertTrue(sut.isPseudoSelector());
    }

    @Test
    public void isNested() {
        sut.setNested(true);
        assertTrue(sut.isNested());
    }

    @Test
    public void testEquals() {
        String selector = "SELECTOR";
        StyleSpecificity specificity = new StyleSpecificity(0, 1, 0, 0);
        sut.setSelector(selector);
        sut.setSpecificity(specificity);
        sut.setMediaQuery(true);
        StyleToken expected = new StyleToken();
        expected.setSelector(selector);
        expected.setSpecificity(specificity);
        expected.setMediaQuery(true);
        assertEquals(expected, sut);
    }

    @Test
    public void testHashCode() {
        String selector = "SELECTOR";
        StyleSpecificity specificity = new StyleSpecificity(0, 1, 0, 0);
        sut.setSelector(selector);
        sut.setSpecificity(specificity);
        sut.setMediaQuery(true);
        StyleToken expected = new StyleToken();
        expected.setSelector(selector);
        expected.setSpecificity(specificity);
        expected.setMediaQuery(true);
        assertEquals(expected.hashCode(), sut.hashCode());
    }
}
