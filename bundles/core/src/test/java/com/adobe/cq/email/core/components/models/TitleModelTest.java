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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.services.AccLinkService;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.Title;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TitleModelTest {
    private static final String URL = "/generic/relative/url";

    @Mock
    Title delegate;
    @Mock
    Resource inheritedResource;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    AccLinkService accLinkService;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    ValueMap valueMap;
    @Mock
    Link accLink;

    private TitleModel sut;

    @BeforeEach
    void setUp() {
        this.sut = new TitleModel();
        this.sut.delegate = delegate;
        this.sut.inheritedResource = inheritedResource;
        this.sut.slingHttpServletRequest = slingHttpServletRequest;
        this.sut.accLinkService = accLinkService;
        this.sut.resourceResolver = resourceResolver;
    }

    @Test
    void initModel_NullDelegate() {
        this.sut.delegate = null;
        this.sut.initModel();
        assertNull(this.sut.accLink);
    }

    @Test
    void initModel_NullInheritedResource() {
        this.sut.inheritedResource = null;
        this.sut.initModel();
        assertNull(this.sut.accLink);
    }

    @Test
    void initModel_Success() {
        when(inheritedResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get(eq("linkURL"), eq(String.class))).thenReturn(URL);
        when(accLinkService.create(eq(resourceResolver), eq(slingHttpServletRequest), eq(URL))).thenReturn(accLink);
        this.sut.initModel();
        assertEquals(accLink, this.sut.accLink);
    }

    @Test
    void getText_Delegate() {
        when(delegate.getText()).thenReturn("TEXT");
        assertEquals("TEXT", this.sut.getText());
    }

    @Test
    void getText_NullDelegate() {
        this.sut.delegate = null;
        assertNull(this.sut.getText());
    }

    @Test
    void getType_Delegate() {
        when(delegate.getType()).thenReturn("TYPE");
        assertEquals("TYPE", this.sut.getType());
    }

    @Test
    void getType_NullDelegate() {
        this.sut.delegate = null;
        assertNull(this.sut.getType());
    }

    @Test
    void getLink_Delegate() {
        Link link = mock(Link.class);
        when(delegate.getLink()).thenReturn(link);
        assertEquals(link, this.sut.getLink());
    }

    @Test
    void getLink_NullDelegate() {
        this.sut.delegate = null;
        assertNull(this.sut.getLink());
    }

    @Test
    void getLinkURL_Delegate() {
        when(delegate.getLinkURL()).thenReturn("LINK_URL");
        assertEquals("LINK_URL", this.sut.getLinkURL());
    }

    @Test
    void getLinkURL_NullDelegate() {
        this.sut.delegate = null;
        assertNull(this.sut.getLinkURL());
    }

    @Test
    void isLinkDisabled() {
        assertFalse(this.sut.isLinkDisabled());
    }

    @Test
    void getId_Delegate() {
        when(delegate.getId()).thenReturn("ID");
        assertEquals("ID", this.sut.getId());
    }

    @Test
    void getId_NullDelegate() {
        this.sut.delegate = null;
        assertNull(this.sut.getId());
    }

    @Test
    void getData_Delegate() {
        ComponentData data = mock(ComponentData.class);
        when(delegate.getData()).thenReturn(data);
        assertEquals(data, this.sut.getData());
    }

    @Test
    void getData_NullDelegate() {
        this.sut.delegate = null;
        assertNull(this.sut.getData());
    }

    @Test
    void getAppliedCssClasses() {
        when(delegate.getAppliedCssClasses()).thenReturn("APPLIED_CSS_CLASSES");
        assertEquals("APPLIED_CSS_CLASSES", sut.getAppliedCssClasses());
    }

    @Test
    void getAppliedCssClasses_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getAppliedCssClasses());
    }

    @Test
    void getExportedType() {
        when(delegate.getExportedType()).thenReturn("EXPORTED_TYPE");
        assertEquals("EXPORTED_TYPE", sut.getExportedType());
    }

    @Test
    void getExportedType_NullDelegate() {
        this.sut.delegate = null;
        assertEquals("", sut.getExportedType());
    }
}