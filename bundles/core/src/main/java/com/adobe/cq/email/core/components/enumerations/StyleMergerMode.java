package com.adobe.cq.email.core.components.enumerations;

import java.util.Arrays;

public enum StyleMergerMode {
    PROCESS_SPECIFICITY,
    IGNORE_SPECIFICITY,
    ALWAYS_APPEND;

    public static StyleMergerMode getByName(String name) {
        return Arrays.stream(StyleMergerMode.values()).filter(v -> v.name().equals(name)).findFirst()
                .orElse(StyleMergerMode.PROCESS_SPECIFICITY);
    }
}
