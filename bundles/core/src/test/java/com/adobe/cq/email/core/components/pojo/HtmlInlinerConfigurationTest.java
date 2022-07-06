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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlInlinerConfigurationTest {

    @Test
    void parseSuccess() {
        HtmlInlinerConfiguration parsed = HtmlInlinerConfiguration.parse(HtmlInlinerConfiguration.IMG_WIDTH_DEFAULT);
        assertNotNull(parsed);
        assertTrue(parsed.isValid());
    }

    @Test
    void parseError() {
        assertNull(HtmlInlinerConfiguration.parse("invalid_json"));
    }

    @Test
    void testEquals() {
        HtmlInlinerConfiguration first = new HtmlInlinerConfiguration();
        first.setElementType("img");
        first.setCssPropertyRegEx("width");
        first.setCssPropertyOutputRegEx("some_random_regex");
        first.setHtmlAttributeName("img");
        first.setOverrideIfAlreadyExisting(true);
        HtmlInlinerConfiguration second = new HtmlInlinerConfiguration();
        second.setElementType("img");
        second.setCssPropertyRegEx("width");
        second.setCssPropertyOutputRegEx("some_random_regex");
        second.setHtmlAttributeName("img");
        second.setOverrideIfAlreadyExisting(true);
        assertEquals(first, second);
    }

    @Test
    void testHashCode() {
        HtmlInlinerConfiguration first = new HtmlInlinerConfiguration();
        first.setElementType("img");
        first.setCssPropertyRegEx("width");
        first.setCssPropertyOutputRegEx("some_random_regex");
        first.setHtmlAttributeName("img");
        first.setOverrideIfAlreadyExisting(true);
        HtmlInlinerConfiguration second = new HtmlInlinerConfiguration();
        second.setElementType("img");
        second.setCssPropertyRegEx("width");
        second.setCssPropertyOutputRegEx("some_random_regex");
        second.setHtmlAttributeName("img");
        second.setOverrideIfAlreadyExisting(true);
        assertEquals(first.hashCode(), second.hashCode());
    }
}