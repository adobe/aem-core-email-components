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

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.services.UrlMapperService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageModelTest {

    @Mock
    Image delegate;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    UrlMapperService urlMapperService;
    @Mock
    Style currentStyle;
    @Mock
    ResourceResolver resourceResolver;

    private ImageModel sut;

    @BeforeEach
    void setUp() {
        this.sut = new ImageModel();
        this.sut.delegate = delegate;
        this.sut.slingHttpServletRequest = slingHttpServletRequest;
        this.sut.urlMapperService = urlMapperService;
        this.sut.currentStyle = currentStyle;
        this.sut.resourceResolver = resourceResolver;
    }

    @Test
    void initModel() {
        Long fixedWidth = 1400L;
        when(currentStyle.get(eq(ImageModel.DEFAULT_WIDTH_PROPERTY), eq(ImageModel.DEFAULT_WIDTH))).thenReturn(fixedWidth);
        this.sut.initModel();
        assertEquals(fixedWidth, sut.getFixedWidth());
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
    void getHeight_ScaleToFullWidth() {
        this.sut.scaleToFullWidth = true;
        assertNull(sut.getHeight());
    }

    @Test
    void getHeight_FixedWidth() {
        this.sut.fixedWidth = 400L;
        assertNull(
                sut.getHeight());
    }

    @Test
    void getHeight_Delegate() {
        when(delegate.getHeight()).thenReturn("600px");
        assertEquals("600px", sut.getHeight());
    }

    @Test
    void getRole_Decorative() {
        when(delegate.isDecorative()).thenReturn(true);
        assertEquals("presentation", sut.getRole());
    }

    @Test
    void getRole_NotDecorative() {
        when(delegate.isDecorative()).thenReturn(false);
        assertNull(sut.getRole());
    }

    @Test
    void getFullWidthStyle_ScaleToFullWidth() {
        this.sut.scaleToFullWidth = true;
        assertEquals("100%", sut.getFullWidthStyle());
    }

    @Test
    void getFullWidthStyle_NoScaleToFullWidth() {
        this.sut.scaleToFullWidth = false;
        this.sut.fixedWidth = 500L;
        assertEquals("500px", sut.getFullWidthStyle());
    }

    @Test
    void getSrc() {
        String src = "/content/path/image.jpg";
        String imageAbsoluteUrl = "https://domain.com" + src;
        when(delegate.getSrc()).thenReturn(src);
        when(urlMapperService.getMappedUrl(any(), any(), eq(src))).thenReturn(imageAbsoluteUrl);
        assertEquals(imageAbsoluteUrl, sut.getSrc());
    }

    @Test
    void getFixedWidth() {
        Long fixedWidth = 1500L;
        this.sut.fixedWidth = fixedWidth;
        assertEquals(fixedWidth, sut.getFixedWidth());
    }

    @Test
    void isScaleToFullWidth() {
        this.sut.scaleToFullWidth = true;
        assertTrue(sut.isScaleToFullWidth());
    }

    @Test
    void getAlt() {
        when(delegate.getAlt()).thenReturn("ALT");
        assertEquals("ALT", sut.getAlt());
    }

    @Test
    void getTitle() {
        when(delegate.getTitle()).thenReturn("TITLE");
        assertEquals("TITLE", sut.getTitle());
    }

    @Test
    void getUuid() {
        when(delegate.getUuid()).thenReturn("UUID");
        assertEquals("UUID", sut.getUuid());
    }

    @Test
    void getImageLink() {
        Link link = Mockito.mock(Link.class);
        when(delegate.getImageLink()).thenReturn(link);
        assertEquals(link, sut.getImageLink());
    }

    @Test
    void getLink() {
        when(delegate.getLink()).thenReturn("LINK");
        assertEquals("LINK", sut.getLink());
    }

    @Test
    void displayPopupTitle() {
        when(delegate.displayPopupTitle()).thenReturn(true);
        assertTrue(sut.displayPopupTitle());
    }

    @Test
    void getFileReference() {
        when(delegate.getFileReference()).thenReturn("FILE_REFERENCE");
        assertEquals("FILE_REFERENCE", sut.getFileReference());
    }

    @Test
    void getJson() {
        when(delegate.getJson()).thenReturn("JSON");
        assertEquals("JSON", sut.getJson());
    }

    @Test
    void getWidths() {
        int[] widths = {1, 2, 3, 4};
        when(delegate.getWidths()).thenReturn(widths);
        assertArrayEquals(widths, sut.getWidths());
    }

    @Test
    void getSrcUriTemplate() {
        when(delegate.getSrcUriTemplate()).thenReturn("SRC_URI_TEMPLATE");
        assertEquals("SRC_URI_TEMPLATE", sut.getSrcUriTemplate());
    }

    @Test
    void isLazyEnabled() {
        assertFalse(sut.isLazyEnabled());
    }

    @Test
    void getSrcset() {
        when(delegate.getSrcset()).thenReturn("SRCSET");
        assertEquals("SRCSET", sut.getSrcset());
    }

    @Test
    void getLazyThreshold() {
        when(delegate.getLazyThreshold()).thenReturn(42);
        assertEquals(42, sut.getLazyThreshold());
    }

    @Test
    void getAreas() {
        ArrayList<ImageArea> areas = new ArrayList<>();
        when(delegate.getAreas()).thenReturn(areas);
        assertEquals(areas, sut.getAreas());
    }

    @Test
    void isDecorative() {
        when(delegate.isDecorative()).thenReturn(true);
        assertTrue(sut.isDecorative());
    }

    @Test
    void getSmartCropRendition() {
        when(delegate.getSmartCropRendition()).thenReturn("SMART_CROP_RENDITION");
        assertEquals("SMART_CROP_RENDITION", sut.getSmartCropRendition());
    }

    @Test
    void isDmImage() {
        when(delegate.isDmImage()).thenReturn(true);
        assertTrue(sut.isDmImage());
    }

    @Test
    void getComponentData() {
        ImageData imageData = Mockito.mock(ImageData.class);
        when(delegate.getComponentData()).thenReturn(imageData);
        assertEquals(imageData, sut.getComponentData());
    }

    @Test
    void getId() {
        when(delegate.getId()).thenReturn("ID");
        assertEquals("ID", sut.getId());
    }

    @Test
    void getData() {
        ComponentData componentData = Mockito.mock(ComponentData.class);
        when(delegate.getData()).thenReturn(componentData);
        assertEquals(componentData, sut.getData());
    }

    @Test
    void getAppliedCssClasses() {
        when(delegate.getAppliedCssClasses()).thenReturn("APPLIED_CSS_CLASSES");
        assertEquals("APPLIED_CSS_CLASSES", sut.getAppliedCssClasses());
    }

    @Test
    void getExportedType() {
        when(delegate.getExportedType()).thenReturn("EXPORTED_TYPE");
        assertEquals("EXPORTED_TYPE", sut.getExportedType());
    }

    private void setValue(String field, Object value) {
        try {
            Field declaredField = ImageModel.class.getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(sut, value);
        } catch (Throwable e) {
            throw new RuntimeException("Error setting private field " + field + " value " + value + ": " + e.getMessage(), e);
        }
    }
}