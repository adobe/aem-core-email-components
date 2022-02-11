package com.adobe.cq.email.core.components.util;

import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.pojo.StyleSpecificity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StyleSpecificityFactoryTest {

    @Test
    void getSpecificitySingleElement() {
        StyleSpecificity specificity = StyleSpecificityFactory.getSpecificity("body");
        assertEquals(new StyleSpecificity(0, 0, 0, 1), specificity);
    }

    @Test
    void getSpecificityNestedElement() {
        StyleSpecificity specificity = StyleSpecificityFactory.getSpecificity("table td");
        assertEquals(new StyleSpecificity(0, 0, 0, 2), specificity);
    }

    @Test
    void getSpecificityMultipleElements() {
        StyleSpecificity specificity = StyleSpecificityFactory.getSpecificity("h1, p");
        assertEquals(new StyleSpecificity(0, 0, 0, 2), specificity);
    }

    @Test
    void getSpecificityOnlyElementId() {
        StyleSpecificity specificity = StyleSpecificityFactory.getSpecificity("#red");
        assertEquals(new StyleSpecificity(0, 1, 0, 0), specificity);
    }

    @Test
    void getSpecificityOnlyClassAttribute() {
        StyleSpecificity specificity = StyleSpecificityFactory.getSpecificity(".bordered");
        assertEquals(new StyleSpecificity(0, 0, 1, 0), specificity);
    }

    @Test
    void getSpecificityEverything() {
        StyleSpecificity specificity = StyleSpecificityFactory.getSpecificity("body, table td, #red, .bordered");
        assertEquals(new StyleSpecificity(0, 1, 1, 3), specificity);
    }

}