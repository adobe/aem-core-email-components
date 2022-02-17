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
package com.adobe.cq.email.core.components.config;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.enumerations.StyleMergerMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StylesInlinerConfigTest {

    @Test
    void activateNullCfg() {
        StylesInlinerConfig sut = new StylesInlinerConfig();
        sut.activate(null);
        assertEquals(sut.getStylesMergingMode(), StyleMergerMode.PROCESS_SPECIFICITY.name());
    }

    @Test
    void activateEmptyCfg() {
        StylesInlinerConfig sut = new StylesInlinerConfig();
        sut.activate(new StylesInlinerConfig.Cfg() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return StylesInlinerConfig.Cfg.class;
            }

            @Override
            public String stylesMergingMode() {
                return "";
            }
        });
        assertEquals(sut.getStylesMergingMode(), StyleMergerMode.PROCESS_SPECIFICITY.name());
    }

    @Test
    void activateValidCfg() {
        StylesInlinerConfig sut = new StylesInlinerConfig();
        sut.activate(new StylesInlinerConfig.Cfg() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return StylesInlinerConfig.Cfg.class;
            }

            @Override
            public String stylesMergingMode() {
                return StyleMergerMode.IGNORE_SPECIFICITY.name();
            }
        });
        assertEquals(sut.getStylesMergingMode(), StyleMergerMode.IGNORE_SPECIFICITY.name());
    }
}