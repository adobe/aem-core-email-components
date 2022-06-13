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

import java.net.URLEncoder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccUrlProcessorTest {

    @Test
    void nullUrl() {
        assertNull(AccUrlProcessor.process(null));
    }

    @Test
    void emptyUrl() {
        assertTrue(AccUrlProcessor.process("").isEmpty());
    }

    @Test
    void noAccMarkup() {
        String url = "/a/url/without/acc/markup";
        assertEquals(url, AccUrlProcessor.process(url));
    }

    @Test
    void accMarkupWithSomethingAtTheBeginning() {
        String url = "/a/url/with/<% acc.markup%>";
        assertEquals(url, AccUrlProcessor.process(URLEncoder.encode(url)));
    }

    @Test
    void accMarkupWithSomethingAtTheEnd() {
        String url = "<% acc.markup%>/with/something";
        assertEquals(url, AccUrlProcessor.process(URLEncoder.encode(url)));
    }

    @Test
    void onlyAccMarkup() {
        String url = "<% acc.markup%>";
        assertEquals(url, AccUrlProcessor.process(URLEncoder.encode(url)));
    }

    @Test
    void onlyAccMarkupWithoutAngularBrackets() {
        String urlWithoutAngularBrackets = "% acc.markup %";
        String url = "<% acc.markup %>";
        assertEquals(url, AccUrlProcessor.process(URLEncoder.encode(urlWithoutAngularBrackets)));
    }
}