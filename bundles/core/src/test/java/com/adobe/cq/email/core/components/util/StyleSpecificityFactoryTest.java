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