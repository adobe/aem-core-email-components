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

import java.util.BitSet;

import org.junit.jupiter.api.Test;

import com.google.common.primitives.Bytes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class URITest {

    @Test
    void notEscapedInvalidCharactersUriReference() {
        URI uri = new URI("https://www.emailtest.dev:8080/content/Hello Günter.json?param1=1&param2=2", false);
        assertEquals("https://www.emailtest.dev:8080/content/Hello Günter.json?param1=1&param2=2", uri.getURI());
    }

    @Test
    void notEscapedValidCharactersUriReference() {
        URI uri = new URI("https://www.emailtest.dev:8080/content/Hello.json?param1=1&param2=2", false);
        assertEquals("https://www.emailtest.dev:8080/content/Hello.json?param1=1&param2=2", uri.getURI());
    }

    @Test
    void escapedInvalidCharactersUriReference() {
        assertThrows(URIException.class, () -> new URI("https://www.emailtest.dev:8080/content/Hello Günter.json?param1=1&param2=2", true));
    }

    @Test
    void escapedValidCharactersUriReference() {
        URI uri = new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true);
        assertEquals("https://www.emailtest.dev:8080/content/Hello Günter.json?param1=1&param2=2", uri.getURI());
    }

    @Test
    void prevalidate() {
        URI uri = new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true);
        assertTrue(uri.prevalidate("www.emailtest.dev:8080", new BitSet(256)));
    }

    @Test
    void getScheme() {
        URI uri = new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true);
        assertEquals("https", uri.getScheme());
    }

    @Test
    void getPort() {
        URI uri = new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true);
        assertEquals(8080, uri.getPort());
    }

    @Test
    void getPath() {
        URI uri = new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true);
        assertEquals("/content/Hello Günter.json", uri.getPath());
    }

    @Test
    void getEscapedUriReference() {
        URI uri = new URI("https://www.emailtest.dev:8080/content/Hello Günter.json?param1=1&param2=2", false);
        assertEquals("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", uri.getEscapedURIReference());
    }

    @Test
    void getUriReference() {
        URI uri = new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true);
        assertEquals("https://www.emailtest.dev:8080/content/Hello Günter.json?param1=1&param2=2", uri.getURIReference());
    }

    @Test
    void testEquals() {
        assertEquals(new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true),
                new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true));
    }

    @Test
    void testHashCode() {
        assertEquals(new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true).hashCode(),
                new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true).hashCode());
    }

    @Test
    void testCompareTo() {
        assertEquals(0, new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true).compareTo(
                new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true)));
    }

    @Test
    void testClone() throws CloneNotSupportedException {
        URI uri = new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true);
        assertEquals(uri, uri.clone());
    }
}