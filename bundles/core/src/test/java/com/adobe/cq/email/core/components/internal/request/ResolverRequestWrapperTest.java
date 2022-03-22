/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
class ResolverRequestWrapperTest {

    private final AemContext ctx = new AemContext();
    private final EmptyHttpServletRequest emptyRequest = new EmptyHttpServletRequest();
    private final static String URI_STRING = "https://www.emailtest.dev";

    @BeforeEach
    void setUp() {
        ctx.load().json("/content/TestPage.json", "/content");
    }

    @Test
    void constructorTest() {
        ResolverRequestWrapper wrapper = new ResolverRequestWrapper(emptyRequest, URI_STRING);
        assertNull(wrapper.getRequestURL());
    }

    @Test
    void getServerNameTest() {
        ResolverRequestWrapper wrapper = new ResolverRequestWrapper(emptyRequest, URI_STRING);
        assertEquals(wrapper.getServerName(), "www.emailtest.dev");
    }

    @Test
    void getServerPortTest() {
        ResolverRequestWrapper wrapper = new ResolverRequestWrapper(emptyRequest, URI_STRING);
        assertEquals(wrapper.getServerPort(), -1);
    }

    @Test
    void getSchemeTest() {
        ResolverRequestWrapper wrapper = new ResolverRequestWrapper(emptyRequest, URI_STRING);
        assertEquals(wrapper.getScheme(), "https");
    }

    @Test
    void getPathInfoTest() {
        ResolverRequestWrapper wrapper = new ResolverRequestWrapper(emptyRequest, URI_STRING);
        assertNull(wrapper.getPathInfo());
    }

    @Test
    void invalidUri() {
        ResolverRequestWrapper wrapper = new ResolverRequestWrapper(emptyRequest, "123#?&$4_no_uri");
        assertNull(wrapper.getServerName());
    }

}
