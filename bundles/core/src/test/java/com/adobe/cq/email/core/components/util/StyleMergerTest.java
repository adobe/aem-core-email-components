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

import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.pojo.StyleSpecificity;
import com.adobe.cq.email.core.components.pojo.StyleToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StyleMergerTest {

    @Test
    void nullStyleMergerMode() {
        StyleToken elementStyle = create("background-color: #ccc; font-family: 'Timmana', \"Gill Sans\", sans-serif;", true);
        StyleToken styleToken = create("font-size: 20px; color: #004488; Margin: 0px;", false);
        assertEquals("font-size: 20px; color: #004488; Margin: 0px; background-color: #ccc; font-family: " +
                        "'Timmana', 'Gill Sans', sans-serif;",
                StyleTokenFactory.getInlinablePropertiesIgnoringNesting(StyleMerger.merge(elementStyle, styleToken)));
    }

    @Test
    void nullStyleToken() {
        StyleToken elementStyle = create("font-size: 20px; color: #004488; Margin: 0px;", true);
        assertEquals("font-size: 20px; color: #004488; Margin: 0px;",
                StyleTokenFactory.getInlinablePropertiesIgnoringNesting(StyleMerger.merge(elementStyle, null)));
    }

    @Test
    void nullElementStyleToken() {
        StyleToken styleToken = create("font-size: 20px; color: #004488; Margin: 0px;", false);
        assertEquals("font-size: 20px; color: #004488; Margin: 0px;",
                StyleTokenFactory.getInlinablePropertiesIgnoringNesting(StyleMerger.merge(null, styleToken)));
    }

    @Test
    void invalidStyleToken() {
        StyleToken styleToken = create("table.layout { width: 100% !important; }", false);
        assertEquals(null,
                StyleTokenFactory.getInlinablePropertiesIgnoringNesting(StyleMerger.merge(null, styleToken)));
    }

    @Test
    void allDifferentProperties() {
        StyleToken elementStyle = create("background-color: #ccc; font-family: 'Timmana', \"Gill Sans\", sans-serif;", true);
        StyleToken styleToken = create("font-size: 20px; color: #004488; Margin: 0px;", false);
        assertEquals("font-size: 20px; color: #004488; Margin: 0px; background-color: #ccc; font-family: " +
                        "'Timmana', 'Gill Sans', sans-serif;",
                StyleTokenFactory.getInlinablePropertiesIgnoringNesting(StyleMerger.merge(elementStyle, styleToken)));
    }

    @Test
    void conflictingNotImportantProperties() {
        StyleToken elementStyle = create("background-color: #ccc; font-family: 'Timmana', \"Gill Sans\", sans-serif;", true);
        StyleToken styleToken = create("background-color: #aaa; font-size: 20px; color: #004488; Margin: 0px;", false);
        assertEquals("background-color: #aaa; font-size: 20px; color: #004488; Margin: 0px; font-family: 'Timmana', 'Gill Sans', " +
                        "sans-serif;",
                StyleTokenFactory.getInlinablePropertiesIgnoringNesting(StyleMerger.merge(elementStyle, styleToken)));
    }

    @Test
    void conflictingImportantProperties() {
        StyleToken elementStyle = create("background-color: #ccc !important; font-family: 'Timmana', \"Gill Sans\", sans-serif;",
                true);
        StyleToken styleToken = create("background-color: #aaa !important; font-size: 20px; color: #004488; Margin: 0px;", false);
        assertEquals(
                "background-color: #ccc !important; font-size: 20px; color: #004488; Margin: 0px; font-family: 'Timmana', 'Gill Sans'," +
                        " sans-serif;",
                StyleTokenFactory.getInlinablePropertiesIgnoringNesting(StyleMerger.merge(elementStyle, styleToken)));
    }

    @Test
    void conflictingImportantPropertyWithHigherSpecificity() {
        StyleToken elementStyle = create("background-color: #ccc !important; font-family: 'Timmana', \"Gill Sans\", sans-serif;",
                true);
        StyleToken styleToken = create("background-color: #aaa; font-size: 20px; color: #004488; Margin: 0px;", false);
        assertEquals(
                "background-color: #ccc !important; font-size: 20px; color: #004488; Margin: 0px; font-family: 'Timmana', 'Gill Sans'," +
                        " sans-serif;",
                StyleTokenFactory.getInlinablePropertiesIgnoringNesting(StyleMerger.merge(elementStyle, styleToken)));
    }

    @Test
    void conflictingImportantPropertyWithLowerSpecificity() {
        StyleToken elementStyle = create("background-color: #ccc; font-family: 'Timmana', \"Gill Sans\", sans-serif;",
                true);
        StyleToken styleToken = create("background-color: #aaa !important; font-size: 20px; color: #004488; Margin: 0px;", false);
        assertEquals(
                "background-color: #aaa !important; font-size: 20px; color: #004488; Margin: 0px; font-family: 'Timmana', 'Gill Sans'," +
                        " sans-serif;",
                StyleTokenFactory.getInlinablePropertiesIgnoringNesting(StyleMerger.merge(elementStyle, styleToken)));
    }

    @Test
    void nestedSelector() {
        StyleToken elementStyle = create("", true);
        StyleToken styleToken = create("td.layout-column { display: block !important; width: 100% !important; }",
                false);
        assertNull(StyleTokenFactory.getInlinablePropertiesIgnoringNesting(StyleMerger.merge(elementStyle, styleToken)));
    }

    @Test
    void samePropertyMultipleTimes() {
        StyleToken elementStyle = create("", true);
        StyleToken styleToken = create("display: block; display: inline-block", false);
        assertEquals("display: block;",
                StyleTokenFactory.getInlinablePropertiesIgnoringNesting(StyleMerger.merge(elementStyle, styleToken)));
    }

    private StyleToken create(String properties, boolean styleAttribute) {
        StyleToken styleToken = StyleTokenFactory.create("tag1");
        StyleTokenFactory.addProperties(styleToken, properties);
        if (styleAttribute) {
            styleToken.setSpecificity(new StyleSpecificity(1, 0, 0, 0));
        } else {
            styleToken.setSpecificity(StyleSpecificityFactory.getSpecificity("tag1"));

        }
        return styleToken;
    }

}
