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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    void processExceptionThrown() {
        HrefProcessor.process("<a>invalid HTML");
    }

    @Test
    void processSuccess() {
        String text = "<p><a href=\"%3C% aaa.bbb%20%&gt;/footer\" target=\"_blank\">Starting ACC markup</a></p>\n" +
                "<p><a href=\"http://server/%3C% aaa.bbb%20%&gt;\" target=\"_blank\">Ending ACC markup</a></p>\n" +
                "<p><a href=\"%3C% aaa.bbb%20%&gt;\" target=\"_blank\">Starting and ending ACC markup</a></p>\n" +
                "<p><a href=\"http:/server/%3C% aaa.bbb%20%&gt;/footer\" target=\"_blank\">ACC markup between normal syntax</a></p>\n" +
                "<p><a href=\"http://server/footer\" target=\"_blank\">No ACC markup</a></p>";
        assertEquals("<p><a href=\"<% aaa.bbb %>/footer\" target=\"_blank\" x-cq-linkchecker=\"skip\">Starting ACC markup</a></p> \n" +
                        "<p><a href=\"http://server/<% aaa.bbb %>\" target=\"_blank\" x-cq-linkchecker=\"skip\">Ending ACC markup</a></p> \n" +
                        "<p><a href=\"<% aaa.bbb %>\" target=\"_blank\" x-cq-linkchecker=\"skip\">Starting and ending ACC markup</a></p> \n" +
                        "<p><a href=\"http:/server/<% aaa.bbb %>/footer\" target=\"_blank\" x-cq-linkchecker=\"skip\">ACC markup between normal syntax</a></p> \n" +
                        "<p><a href=\"http://server/footer\" target=\"_blank\">No ACC markup</a></p>",
                HrefProcessor.process(text));
    }
}