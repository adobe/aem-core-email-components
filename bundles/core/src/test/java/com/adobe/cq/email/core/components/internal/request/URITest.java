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
package com.adobe.cq.email.core.components.internal.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class URITest {

    @Test
    void notEscapedInvalidCharactersUriReference() {
        URI uri = new URI("https://www.emailtest.dev/content/Hello Günter.json", false);
        assertEquals("https://www.emailtest.dev/content/Hello Günter.json", uri.getURI());
    }

    @Test
    void notEscapedValidCharactersUriReference() {
        URI uri = new URI("https://www.emailtest.dev/content/Hello.json", false);
        assertEquals("https://www.emailtest.dev/content/Hello.json", uri.getURI());
    }

    @Test
    void escapedInvalidCharactersUriReference() {
        assertThrows(URIException.class, () -> new URI("https://www.emailtest.dev/content/Hello Günter.json", true));
    }

    @Test
    void escapedValidCharactersUriReference() {
        URI uri = new URI("https://www.emailtest.dev/content/Hello.json", true);
        assertEquals("https://www.emailtest.dev/content/Hello.json", uri.getURI());
    }

    @Test
    void testEquals() {
        assertEquals(new URI("https://www.emailtest.dev/content/Hello.json", true), new URI("https://www.emailtest.dev/content/Hello" +
                ".json", true));
    }

    @Test
    void testHashCode() {
        assertEquals(new URI("https://www.emailtest.dev/content/Hello.json", true).hashCode(),
                new URI("https://www.emailtest.dev/content/Hello" +
                        ".json", true).hashCode());
    }

    @Test
    void testCompareTo() {
        assertEquals(0, new URI("https://www.emailtest.dev/content/Hello.json", true).compareTo(new URI("https://www.emailtest" +
                ".dev/content/Hello" +
                ".json", true)));
    }

    @Test
    void testClone() throws CloneNotSupportedException {
        URI uri = new URI("https://www.emailtest.dev/content/Hello.json", true);
        assertEquals(uri, uri.clone());
    }
}