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

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class URITest {

    @Test
    void testIPv4Address() throws URIException {
        URI uri = new URI("http://10.0.1.10:8830", false);
        assertTrue(uri.isIPv4address());
        uri = new URI("http://10.0.1.10:8830/04-1.html", false);
        assertTrue(uri.isIPv4address());
        uri = new URI("/04-1.html", false);
        assertFalse(uri.isIPv4address());
        uri = new URI("http://10.0.1.10:8830/04-1.html", false);
        assertTrue(uri.isIPv4address());
        uri = new URI("http://10.0.1.10:8830/04-1.html", false);
        assertTrue(uri.isIPv4address());
        uri = new URI("http://host.org/04-1.html", false);
        assertFalse(uri.isIPv4address());
    }

    @Test
    void testUrl() throws URIException {
        URI url = new URI("http://jakarta.apache.org", false);
        assertEquals(-1, url.getPort()); // URI itself has no knowledge of default ports.
        assertEquals("http", url.getScheme());

        url = new URI("https://jakarta.apache.org", false);
        assertEquals(-1, url.getPort()); // URI itself has no knowledge of default ports.
        assertEquals("https", url.getScheme());
    }

    @Test
    void testTestURIAuthorityString() throws Exception {
        URI url = new URI("ftp://user:password@localhost", false);
        assertEquals("ftp://user:password@localhost", url.toString());
    }

    @Test
    void testVariousCharacters() throws Exception {
        verifyInvalidURI("http://authority:123/path/path?query&name=val ue");
        verifyInvalidURI("http://authority:123/path/path?query&na me=value");
        verifyInvalidURI("http://authority:123/path/path?qu ery&name=value");
        verifyInvalidURI("http://authority:123/path/pa th?query&name=value");
        verifyInvalidURI("http://authority:123/pa th/path?query&name=value");
        verifyInvalidURI("http://authority:12 3/path/path?query&name=value");
        verifyInvalidURI("http://autho rity:123/path/path?query&name=value");
        verifyInvalidURI("htt p://authority:123/path/path?query&name=value");
    }

    private void verifyInvalidURI(String uri) {
        try {
            new URI(uri, true);
            throw new RuntimeException("should have thrown URIException");
        } catch (URIException e) {
            /* expected */
        }
    }

    @Test
    void testRelativeWithDoubleSlash() throws Exception {
        URI rel = new URI("foo//bar//baz", true);
        assertEquals("foo//bar//baz", rel.toString());
    }

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
    void getHost() {
        URI uri = new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true,
                StandardCharsets.UTF_8.toString());
        assertEquals("www.emailtest.dev", uri.getHost());

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
    void getUriReferenceWithSpecifiedCharset() {
        URI uri = new URI("https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2", true,
                StandardCharsets.UTF_8.toString());
        assertEquals("https://www.emailtest.dev:8080/content/Hello Günter.json?param1=1&param2=2", uri.getURIReference());
    }

    @Test
    void getRawUri() {
        String sss = "https://www.emailtest.dev:8080/content/Hello%20G%C3%BCnter.json?param1=1&param2=2";
        URI uri = new URI(sss, true);
        assertEquals(sss, new String(uri.getRawURI()));
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