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
import com.adobe.cq.wcm.core.components.models.Button;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ButtonModelTest {
    private static final String URL = "/generic/relative/url";

    @Mock
    Button delegate;
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

    private ButtonModel sut;

    @BeforeEach
    void setUp() {
        this.sut = new ButtonModel();
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
    void getButtonLink_Delegate() {
        Link link = mock(Link.class);
        when(delegate.getButtonLink()).thenReturn(link);
        assertEquals(link, this.sut.getButtonLink());
    }

    @Test
    void getButtonLink_NullDelegate() {
        this.sut.delegate = null;
        assertNull(this.sut.getButtonLink());
    }

    @Test
    void getLink_Delegate() {
        when(delegate.getLink()).thenReturn("LINK");
        assertEquals("LINK", this.sut.getLink());
    }

    @Test
    void getLink_NullDelegate() {
        this.sut.delegate = null;
        assertNull(this.sut.getLink());
    }

    @Test
    void getIcon_Delegate() {
        when(delegate.getIcon()).thenReturn("ICON");
        assertEquals("ICON", this.sut.getIcon());
    }

    @Test
    void getIcon_NullDelegate() {
        this.sut.delegate = null;
        assertNull(this.sut.getIcon());
    }

    @Test
    void getAccessibilityLabel_Delegate() {
        when(delegate.getAccessibilityLabel()).thenReturn("ACCESSIBILITY_LABEL");
        assertEquals("ACCESSIBILITY_LABEL", this.sut.getAccessibilityLabel());
    }

    @Test
    void getAccessibilityLabel_NullDelegate() {
        this.sut.delegate = null;
        assertNull(this.sut.getAccessibilityLabel());
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