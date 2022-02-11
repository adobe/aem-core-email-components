package com.adobe.cq.email.core.components;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFileUtils {
    public final static String INTERNAL_CSS_FILE_PATH = "testpage/internal_css.html";
    public final static String EXTERNAL_CSS_FILE_PATH = "testpage/external_css.html";
    public final static String INTERNAL_AND_EXTERNAL_CSS_FILE_PATH = "testpage/internal_and_external_css.html";
    public final static String OUTPUT_FILE_PATH = "testpage/output_without_style.html";
    public final static String STYLE_FILE_PATH = "testpage/style.css";
    public final static String STYLE_AFTER_PROCESSING_FILE_PATH = "testpage/unused_style.css";

    public static String getFileContent(String path) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(path).toURI())));
    }

    public static void compare(String expected, String actual) {
        assertEquals(expected.replaceAll("\\s+", " ").trim(), actual.replaceAll("\\s+",
                " ").trim());
    }
}
