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
package com.adobe.cq.email.core.components.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.commons.link.Link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeaserLinkTest {

    @Test
    void testCreateNullLink() {
        assertNull(TeaserLink.create(null, null));
    }

    @Test
    void testCreateNullUrl() {
        Map<String, String> htmlAttributes = Collections.singletonMap("key", "value");
        Object reference = new Object();
        Link link = TeaserLink.create(
                new InternalLink(true, "/url", "/mapped/url", "/externalized/url", htmlAttributes,
                        reference), null);
        assertNotNull(link);
        assertTrue(link.isValid());
        assertEquals("/url", link.getURL());
        assertEquals("/mapped/url", link.getMappedURL());
        assertEquals("/externalized/url", link.getExternalizedURL());
        assertEquals(htmlAttributes, link.getHtmlAttributes());
        assertEquals(reference, link.getReference());
    }

    @Test
    void testCreate() {
        String url = "/another/link/url";
        Object reference = new Object();
        Link link = TeaserLink.create(
                new InternalLink(true, "/url", "/mapped/url", "/externalized/url", Collections.singletonMap("key", "value"),
                        reference), url);
        assertNotNull(link);
        assertTrue(link.isValid());
        assertEquals(url, link.getURL());
        assertEquals(url, link.getMappedURL());
        assertEquals(url, link.getExternalizedURL());
        Map<String, String> htmlAttributes = new HashMap<>();
        htmlAttributes.put("key", "value");
        htmlAttributes.put("href", url);
        assertEquals(htmlAttributes, link.getHtmlAttributes());
        assertEquals(reference, link.getReference());
    }

    @Test
    void testEquals() {
        String url = "/another/link/url";
        Object reference = new Object();
        Link link1 = TeaserLink.create(
                new InternalLink(true, "/url", "/mapped/url", "/externalized/url", Collections.singletonMap("key", "value"),
                        reference), url);
        Link link2 = TeaserLink.create(
                new InternalLink(true, "/url", "/mapped/url", "/externalized/url", Collections.singletonMap("key", "value"),
                        reference), url);
        assertEquals(link1, link2);
    }

    @Test
    void testHashCode() {
        String url = "/another/link/url";
        Object reference = new Object();
        Link link1 = TeaserLink.create(
                new InternalLink(true, "/url", "/mapped/url", "/externalized/url", Collections.singletonMap("key", "value"),
                        reference), url);
        Link link2 = TeaserLink.create(
                new InternalLink(true, "/url", "/mapped/url", "/externalized/url", Collections.singletonMap("key", "value"),
                        reference), url);
        assertNotNull(link1);
        assertNotNull(link2);
        assertEquals(link1.hashCode(), link2.hashCode());
    }

    private static class InternalLink implements Link {
        private final boolean valid;
        private final String url;
        private final String mappedUrl;
        private final String externalizedUrl;
        private final Map<String, String> htmlAttributes;
        private final Object reference;

        private InternalLink(boolean valid, String url, String mappedUrl, String externalizedUrl, Map<String, String> htmlAttributes,
                             Object reference) {
            this.valid = valid;
            this.url = url;
            this.mappedUrl = mappedUrl;
            this.externalizedUrl = externalizedUrl;
            this.htmlAttributes = htmlAttributes;
            this.reference = reference;
        }

        @Override
        public boolean isValid() {
            return valid;
        }

        @Override
        public @Nullable String getURL() {
            return url;
        }

        @Override
        public @Nullable String getMappedURL() {
            return mappedUrl;
        }

        @Override
        public @Nullable String getExternalizedURL() {
            return externalizedUrl;
        }

        @Override
        public @NotNull Map<String, String> getHtmlAttributes() {
            return htmlAttributes;
        }

        @Nullable
        @Override
        public Object getReference() {
            return reference;
        }
    }
}