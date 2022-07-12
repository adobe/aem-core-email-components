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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.wcm.core.components.models.Text;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TextModelTest {

    @Mock
    private Text delegate;

    private TextModel sut;

    @BeforeEach
    void setUp() {
        this.sut = new TextModel();
        this.sut.delegate = delegate;
    }

    @Test
    void getText_NullValue() {
        when(delegate.getText()).thenReturn(null);
        assertNull(sut.getText());
    }

    @Test
    void getText_EmptyValue() {
        when(delegate.getText()).thenReturn("");
        assertTrue(sut.getText().isEmpty());
    }

    @Test
    void getText_Delegate() {
        String text = "<p>Some text</p>";
        when(delegate.getText()).thenReturn(text);
        assertEquals(text, sut.getText());
    }

    @Test
    void getText_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getText());
    }

    @Test
    void isRichText_Delegate() {
        when(delegate.isRichText()).thenReturn(true);
        assertTrue(sut.isRichText());
    }

    @Test
    void isRichText_NullDelegate() {
        this.sut.delegate = null;
        assertFalse(sut.isRichText());
    }

    @Test
    void getId_Delegate() {
        String id = "ID";
        when(delegate.getId()).thenReturn(id);
        assertEquals(id, sut.getId());
    }

    @Test
    void getId_NullDelegate() {
        this.sut.delegate = null;
        assertNull(sut.getId());
    }

    @Test
    void getData_Delegate() {
        ComponentData data = mock(ComponentData.class);
        when(delegate.getData()).thenReturn(data);
        assertEquals(data, sut.getData());
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