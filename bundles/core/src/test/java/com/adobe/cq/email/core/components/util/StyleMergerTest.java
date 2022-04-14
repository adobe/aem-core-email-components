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

class StyleMergerTest {

    @Test
    void nullStyleMergerMode() {
        StyleToken elementStyle = create("background-color: #ccc; font-family: 'Timmana', \"Gill Sans\", sans-serif;", true);
        StyleToken styleToken = create("font-size: 20px; color: #004488; Margin: 0px;", false);
        assertEquals("font-size: 20px; color: #004488; Margin: 0px; background-color: #ccc; font-family: " +
                "'Timmana', 'Gill Sans', sans-serif;", StyleMerger.merge(elementStyle, styleToken));
    }

    @Test
    void nullStyleToken() {
        StyleToken elementStyle = create("font-size: 20px; color: #004488; Margin: 0px;", true);
        testAllModes(elementStyle, null, "font-size: 20px; color: #004488; Margin: 0px;", "font-size: 20px; color: #004488; Margin: 0px;",
                "font-size: 20px; color: #004488; Margin: 0px;");
    }

    @Test
    void nullElementStyleToken() {
        StyleToken styleToken = create("font-size: 20px; color: #004488; Margin: 0px;", false);
        testAllModes(null, styleToken, "font-size: 20px; color: #004488; Margin: 0px;", "font-size: 20px; color: #004488; Margin: 0px;",
                "font-size: 20px; color: #004488; Margin: 0px;");
    }

    @Test
    void invalidStyleToken() {
        StyleToken styleToken = create("table.layout { width: 100% !important; }", false);
        testAllModes(null, styleToken, null, null, null);
    }

    @Test
    void nestedSelector() {
        StyleToken elementStyle = create("", true);
        StyleToken styleToken = create("td.layout-column { display: block !important; width: 100% !important; }",
                false);
        testAllModes(elementStyle, styleToken, null, null, null);
    }

    private void testAllModes(StyleToken elementStyle, StyleToken styleToken, String processSpecificityExpectedResult,
                              String ignoreSpecificityExpectedResult, String alwaysAppend) {
        assertEquals(processSpecificityExpectedResult, StyleMerger.merge(elementStyle, styleToken));
        assertEquals(ignoreSpecificityExpectedResult, StyleMerger.merge(elementStyle, styleToken));
        assertEquals(alwaysAppend, StyleMerger.merge(elementStyle, styleToken));
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
