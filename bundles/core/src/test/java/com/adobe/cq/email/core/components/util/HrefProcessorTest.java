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

import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.util.HrefProcessor.Href;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HrefProcessorTest {

    @Test
    void processNull() {
        assertNull(HrefProcessor.process(null));
    }

    @Test
    void processEmpty() {
        assertEquals("", HrefProcessor.process(""));
    }

    @Test
    void processNoEncodedRefs() {
        String text = "<p><a href=\"http://server/footer\" target=\"_blank\">No ACC markup</a></p>";
        assertEquals(text, HrefProcessor.process(text));
    }

    @Test
    void processSuccess() {
        String text = "<p><a href=\"%3C% aaa.bbb%20%&gt;/footer\" target=\"_blank\">Starting ACC markup</a></p>\n" +
                "<p><a href=\"http://server/%3C% aaa.bbb%20%&gt;\" target=\"_blank\">Ending ACC markup</a></p>\n" +
                "<p><a href=\"%3C% aaa.bbb%20%&gt;\" target=\"_blank\">Starting and ending ACC markup</a></p>\n" +
                "<p><a href=\"http:/server/%3C% aaa.bbb%20%&gt;/footer\" target=\"_blank\">ACC markup between normal syntax</a></p>\n" +
                "<p><a href=\"http://server/footer\" target=\"_blank\">No ACC markup</a></p>";
        assertEquals("<p><a href=\"<% aaa.bbb %>/footer\" target=\"_blank\">Starting ACC markup</a></p>\n" +
                        "<p><a href=\"http://server/<% aaa.bbb %>\" target=\"_blank\">Ending ACC markup</a></p>\n" +
                        "<p><a href=\"<% aaa.bbb %>\" target=\"_blank\">Starting and ending ACC markup</a></p>\n" +
                        "<p><a href=\"http:/server/<% aaa.bbb %>/footer\" target=\"_blank\">ACC markup between normal syntax</a></p>\n" +
                        "<p><a href=\"http://server/footer\" target=\"_blank\">No ACC markup</a></p>",
                HrefProcessor.process(text));
    }

    @Test
    void getEncodedHrefsNull() {
        assertTrue(HrefProcessor.getEncodedHrefs(null).isEmpty());
    }

    @Test
    void getEncodedHrefsEmpty() {
        assertTrue(HrefProcessor.getEncodedHrefs(null).isEmpty());
    }

    @Test
    void getEncodedHrefsSuccess() {
        String text = "<p><a href=\"%3C% aaa.bbb%20%&gt;/footer\" target=\"_blank\">Starting ACC markup</a></p>\n" +
                "<p><a href=\"http://server/%3C% aaa.bbb%20%&gt;\" target=\"_blank\">Ending ACC markup</a></p>\n" +
                "<p><a href=\"%3C% aaa.bbb%20%&gt;\" target=\"_blank\">Starting and ending ACC markup</a></p>\n" +
                "<p><a href=\"http:/server/%3C% aaa.bbb%20%&gt;/footer\" target=\"_blank\">ACC markup between normal syntax</a></p>\n" +
                "<p><a href=\"http://server/footer\" target=\"_blank\">No ACC markup</a></p>";
        assertEquals(Arrays.asList(new Href("href=\"%3C% aaa.bbb%20%&gt;/footer\"", "href=\"<% aaa.bbb %>/footer\""),
                        new Href("href=\"http://server/%3C% aaa.bbb%20%&gt;\"", "href=\"http://server/<% aaa.bbb %>\""),
                        new Href("href=\"%3C% aaa.bbb%20%&gt;\"", "href=\"<% aaa.bbb %>\""),
                        new Href("href=\"http:/server/%3C% aaa.bbb%20%&gt;/footer\"", "href=\"http:/server/<% aaa.bbb %>/footer\"")),
                HrefProcessor.getEncodedHrefs(text));
    }

    @Test
    void testHrefsEquals() {
        Href sut = new Href("http://server/footer", "http://server/footer");
        Href expected = new Href("http://server/footer", "http://server/footer");
        assertEquals(expected, sut);
    }

    @Test
    void testHrefsHashcode() {
        Href sut = new Href("http://server/footer", "http://server/footer");
        Href expected = new Href("http://server/footer", "http://server/footer");
        assertEquals(expected.hashCode(), sut.hashCode());
    }
}