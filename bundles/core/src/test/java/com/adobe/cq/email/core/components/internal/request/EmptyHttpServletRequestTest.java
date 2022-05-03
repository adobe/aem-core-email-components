package com.adobe.cq.email.core.components.internal.request;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.ModifierSupport;

import com.adobe.cq.email.core.components.filters.StylesInlinerFilter;

import static org.junit.jupiter.api.Assertions.*;


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