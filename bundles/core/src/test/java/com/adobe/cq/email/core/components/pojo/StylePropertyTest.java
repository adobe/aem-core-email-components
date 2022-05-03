package com.adobe.cq.email.core.components.pojo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StylePropertyTest {

    @Test
    void testPojo() {
        StyleProperty sut = new StyleProperty();
        sut.setName("width");
        sut.setValue("100px");
        sut.setImportant(false);
        sut.setFullProperty("width: 100px");
        sut.setSpecificity(new StyleSpecificity(1, 0, 0, 0));
        assertEquals("width", sut.getName());
        assertEquals("100px", sut.getValue());
        assertFalse(sut.isImportant());
        assertEquals("width: 100px", sut.getFullProperty());
        assertEquals(new StyleSpecificity(1, 0, 0, 0), sut.getSpecificity());
    }

    @Test
    void testEquals() {
        StyleProperty first = new StyleProperty();
        first.setName("width");
        first.setValue("100px");
        first.setImportant(false);
        first.setFullProperty("width: 100px");
        first.setSpecificity(new StyleSpecificity(1, 0, 0, 0));
        StyleProperty second = new StyleProperty();
        second.setName("width");
        second.setValue("100px");
        second.setImportant(false);
        second.setFullProperty("width: 100px");
        second.setSpecificity(new StyleSpecificity(1, 0, 0, 0));
        assertTrue(first.equals(second));
    }

    @Test
    void testHashCode() {
        StyleProperty first = new StyleProperty();
        first.setName("width");
        first.setValue("100px");
        first.setImportant(false);
        first.setFullProperty("width: 100px");
        first.setSpecificity(new StyleSpecificity(1, 0, 0, 0));
        StyleProperty second = new StyleProperty();
        second.setName("width");
        second.setValue("100px");
        second.setImportant(false);
        second.setFullProperty("width: 100px");
        second.setSpecificity(new StyleSpecificity(1, 0, 0, 0));
        assertEquals(first.hashCode(), second.hashCode());
    }
}