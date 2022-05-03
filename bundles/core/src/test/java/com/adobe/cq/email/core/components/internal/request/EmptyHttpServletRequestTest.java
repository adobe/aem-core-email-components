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
package com.adobe.cq.email.core.components.internal.request;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmptyHttpServletRequestTest {

    @Test
    void emptyCheck() {
        try {
            EmptyHttpServletRequest sut = new EmptyHttpServletRequest();
            for (Method declaredMethod : EmptyHttpServletRequest.class.getDeclaredMethods()) {
                if (!Modifier.isPublic(declaredMethod.getModifiers())) {
                    continue;
                }
                if (declaredMethod.getName().startsWith("set")) {
                    continue;
                }
                if (declaredMethod.getParameterCount() > 0) {
                    continue;
                }
                Object result = declaredMethod.invoke(sut);
                if (result instanceof Integer) {
                    assertEquals(0, declaredMethod.invoke(sut));
                } else if (result instanceof Long) {
                    assertEquals(0L, declaredMethod.invoke(sut));
                } else if (result instanceof Boolean) {
                    assertFalse((Boolean) declaredMethod.invoke(sut));
                } else {
                    assertNull(declaredMethod.invoke(sut));
                }

            }
        } catch (Throwable e) {
            throw new RuntimeException("Error: " + e.getMessage(), e);
        }
    }
}