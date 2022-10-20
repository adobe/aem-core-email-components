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
package com.adobe.cq.email.core.components.internal.models;

import java.util.ArrayList;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.day.cq.wcm.api.designer.Style;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageImplTest {

    @Mock
    Image delegate;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    Style currentStyle;
    @Mock
    ResourceResolver resourceResolver;

    private ImageImpl sut;

    @BeforeEach
    void setUp() {
        this.sut = new ImageImpl();
        this.sut.delegate = delegate;
        this.sut.slingHttpServletRequest = slingHttpServletRequest;
        this.sut.currentStyle = currentStyle;
        this.sut.resourceResolver = resourceResolver;
    }

    @Test
    void initModel() {
        Long fixedWidth = 1400L;
        when(currentStyle.get(eq(ImageImpl.DEFAULT_WIDTH_PROPERTY), eq(ImageImpl.DEFAULT_WIDTH))).thenReturn(fixedWidth);
        this.sut.initModel();
        assertEquals(fixedWidth, sut.fixedWidth);
    }

    @Test
    void getWidth_ScaleToFullWidth() {
        this.sut.scaleToFullWidth = true;
        assertNull(sut.getWidth());
    }

    @Test
    void getWidth_FixedWidth() {
        this.sut.fixedWidth = 400L;
        assertEquals("400", sut.getWidth());
    }

    @Test
    void getWidth_Delegate() {
        when(delegate.getWidth()).thenReturn("600");
        assertEquals("600", sut.getWidth());
    }

    @Test
    void getWidth_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getWidth());
    }

    @Test
    void getHeight_ScaleToFullWidth() {
        this.sut.scaleToFullWidth = true;
        assertNull(sut.getHeight());
    }

    @Test
    void getHeight_FixedWidth() {
        this.sut.fixedWidth = 400L;
        assertNull(sut.getHeight());
    }

    @Test
    void getHeight_Delegate() {
        when(delegate.getHeight()).thenReturn("600px");
        assertEquals("600px", sut.getHeight());
    }

    @Test
    void getHeight_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getHeight());
    }

    @Test
    void getFullWidthStyle_ScaleToFullWidth() {
        this.sut.scaleToFullWidth = true;
        assertEquals("width:100%;", sut.getInlineStyle());
    }

    @Test
    void getFullWidthStyle_NoScaleToFullWidth() {
        this.sut.scaleToFullWidth = false;
        this.sut.fixedWidth = 500L;
        assertEquals("width:500px;", sut.getInlineStyle());
    }

    @Test
    void getFullWidthStyle_NullDelegate() {
        this.sut.delegate = null;
        this.sut.scaleToFullWidth = false;
        assertNull(sut.getInlineStyle());
    }

    @Test
    void getSrc() {
        String src = "/content/path/image.jpg";
        when(delegate.getSrc()).thenReturn(src);
        assertEquals(src, sut.getSrc());
    }

    @Test
    void getSrc_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getSrc());
    }

    @Test
    void getFixedWidth() {
        Long fixedWidth = 1500L;
        this.sut.fixedWidth = fixedWidth;
        assertEquals(fixedWidth, sut.fixedWidth);
    }

    @Test
    void isScaleToFullWidth() {
        this.sut.scaleToFullWidth = true;
        assertTrue(sut.scaleToFullWidth);
    }

    @Test
    void getAlt() {
        when(delegate.getAlt()).thenReturn("ALT");
        assertEquals("ALT", sut.getAlt());
    }

    @Test
    void getAlt_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getAlt());
    }

    @Test
    void getTitle() {
        when(delegate.getTitle()).thenReturn("TITLE");
        assertEquals("TITLE", sut.getTitle());
    }

    @Test
    void getTitle_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getTitle());
    }

    @Test
    void getUuid() {
        when(delegate.getUuid()).thenReturn("UUID");
        assertEquals("UUID", sut.getUuid());
    }

    @Test
    void getUuid_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getUuid());
    }

    @Test
    void getImageLink() {
        Link link = Mockito.mock(Link.class);
        when(delegate.getImageLink()).thenReturn(link);
        assertEquals(link, sut.getImageLink());
    }

    @Test
    void getImageLink_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getImageLink());
    }

    @Test
    void getLink() {
        when(delegate.getLink()).thenReturn("LINK");
        assertEquals("LINK", sut.getLink());
    }

    @Test
    void getLink_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getLink());
    }

    @Test
    void displayPopupTitle() {
        when(delegate.displayPopupTitle()).thenReturn(true);
        assertTrue(sut.displayPopupTitle());
    }

    @Test
    void displayPopupTitle_NullDelegate() {
        this.sut.delegate = null;
        assertFalse(sut.displayPopupTitle());
    }

    @Test
    void getFileReference() {
        when(delegate.getFileReference()).thenReturn("FILE_REFERENCE");
        assertEquals("FILE_REFERENCE", sut.getFileReference());
    }

    @Test
    void getFileReference_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getFileReference());
    }

    @Test
    void getJson() {
        when(delegate.getJson()).thenReturn("JSON");
        assertEquals("JSON", sut.getJson());
    }

    @Test
    void getJson_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getJson());
    }

    @Test
    void getWidths() {
        int[] widths = {1, 2, 3, 4};
        when(delegate.getWidths()).thenReturn(widths);
        assertArrayEquals(widths, sut.getWidths());
    }

    @Test
    void getWidths_NullDelegate() {
        this.sut.delegate = null;
        assertEquals(0, sut.getWidths().length);
    }

    @Test
    void getSrcUriTemplate() {
        when(delegate.getSrcUriTemplate()).thenReturn("SRC_URI_TEMPLATE");
        assertEquals("SRC_URI_TEMPLATE", sut.getSrcUriTemplate());
    }

    @Test
    void getSrcUriTemplate_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getSrcUriTemplate());
    }

    @Test
    void isLazyEnabled() {
        assertFalse(sut.isLazyEnabled());
    }

    @Test
    void isLazyEnabled_NullDelegate() {
        this.sut.delegate = null;
        assertFalse(sut.isLazyEnabled());
    }

    @Test
    void getSrcset() {
        when(delegate.getSrcset()).thenReturn("SRCSET");
        assertEquals("SRCSET", sut.getSrcset());
    }

    @Test
    void getScrset_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getSrcset());
    }

    @Test
    void getLazyThreshold() {
        when(delegate.getLazyThreshold()).thenReturn(42);
        assertEquals(42, sut.getLazyThreshold());
    }

    @Test
    void getLazyThreshold_NullDelegate() {
        this.sut.delegate = null;
        assertEquals(0, sut.getLazyThreshold());
    }

    @Test
    void getAreas() {
        ArrayList<ImageArea> areas = new ArrayList<>();
        when(delegate.getAreas()).thenReturn(areas);
        assertEquals(areas, sut.getAreas());
    }

    @Test
    void getAreas_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getAreas());
    }

    @Test
    void isDecorative() {
        when(delegate.isDecorative()).thenReturn(true);
        assertTrue(sut.isDecorative());
    }

    @Test
    void isDecorative_NullDelegate() {
        this.sut.delegate = null;
        assertFalse(sut.isDecorative());
    }

    @Test
    void getSmartCropRendition() {
        when(delegate.getSmartCropRendition()).thenReturn("SMART_CROP_RENDITION");
        assertEquals("SMART_CROP_RENDITION", sut.getSmartCropRendition());
    }

    @Test
    void getSmartCropRendition_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getSmartCropRendition());
    }

    @Test
    void isDmImage() {
        when(delegate.isDmImage()).thenReturn(true);
        assertTrue(sut.isDmImage());
    }

    @Test
    void isDmImage_NullDelegate() {
        this.sut.delegate = null;
        assertFalse(sut.isDmImage());
    }

    @Test
    void getComponentData() {
        ImageData imageData = Mockito.mock(ImageData.class);
        when(delegate.getComponentData()).thenReturn(imageData);
        assertEquals(imageData, sut.getComponentData());
    }

    @Test
    void getComponentData_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getComponentData());
    }

    @Test
    void getId() {
        when(delegate.getId()).thenReturn("ID");
        assertEquals("ID", sut.getId());
    }

    @Test
    void getId_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getId());
    }

    @Test
    void getData() {
        ComponentData componentData = Mockito.mock(ComponentData.class);
        when(delegate.getData()).thenReturn(componentData);
        assertEquals(componentData, sut.getData());
    }

    @Test
    void getData_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getData());
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
