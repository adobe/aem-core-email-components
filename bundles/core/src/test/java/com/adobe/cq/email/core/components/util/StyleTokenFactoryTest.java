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
package com.adobe.cq.email.core.components.util;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.pojo.StyleToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StyleTokenFactoryTest {

    @Test
    void createEmptySelector() {
        StyleToken expected = new StyleToken();
        expected.setSelector("");
        assertEquals(expected, StyleTokenFactory.create(""));
    }

    @Test
    void createMultipleSelectors() {
        StyleToken expected = new StyleToken();
        expected.setSelector("h1, h2");
        expected.getSplittedSelectors().add("h1");
        expected.getSplittedSelectors().add("h2");
        assertEquals(expected, StyleTokenFactory.create("h1, h2"));
    }

    @Test
    void getAllPropertiesNullToken() {
        assertNull(StyleTokenFactory.getAllProperties(null));
    }

    @Test
    void getAllPropertiesSuccess() {
        StyleToken styleToken = StyleTokenFactory.create("h1, h2");
        StyleTokenFactory.addProperties(styleToken, "display: block !important;width: 100% !important;");
        assertEquals("display: block !important; width: 100% !important;", StyleTokenFactory.getAllProperties(styleToken));
    }

    @Test
    void getInlinablePropertiesNullToken() {
        assertNull(StyleTokenFactory.getInlinableProperties(null));
    }

    @Test
    void getInlinablePropertiesSuccess() {
        StyleToken styleToken = StyleTokenFactory.create("h1, h2");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;font-size: 20px;");
        assertEquals("font-family: 'Timmana', 'Gill Sans', sans-serif; font-size: 20px;",
                StyleTokenFactory.getInlinableProperties(styleToken));
    }

    @Test
    void addPropertiesNullToken() {
        StyleTokenFactory.addProperties(null, "font-family: 'Timmana', \"Gill Sans\", sans-serif;font-size: 20px;");
    }

    @Test
    void addPropertiesNullProperties() {
        StyleToken styleToken = StyleTokenFactory.create("h1, h2");
        StyleTokenFactory.addProperties(styleToken, null);
        assertTrue(styleToken.getProperties().isEmpty());
    }

    @Test
    void addPropertiesEmptyProperties() {
        StyleToken styleToken = StyleTokenFactory.create("h1, h2");
        StyleTokenFactory.addProperties(styleToken, "");
        assertTrue(styleToken.getProperties().isEmpty());
    }

    @Test
    void addPropertiesSingleProperty() {
        StyleToken styleToken = StyleTokenFactory.create("h1, h2");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        assertEquals(Collections.singletonList("font-family: 'Timmana', \"Gill Sans\", sans-serif"), styleToken.getProperties());
    }

    @Test
    void addPropertiesSuccess() {
        StyleToken styleToken = StyleTokenFactory.create("h1, h2");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;font-size: 20px;");
        assertEquals(Arrays.asList("font-family: 'Timmana', \"Gill Sans\", sans-serif", "font-size: 20px"), styleToken.getProperties());
    }
}