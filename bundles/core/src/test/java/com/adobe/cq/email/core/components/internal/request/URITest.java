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