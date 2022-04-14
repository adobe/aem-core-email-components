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
package com.adobe.cq.email.core.components;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFileUtils {
    public final static String INTERNAL_CSS_HTML_FILE_PATH = "testpage/internal_css.html";
    public final static String INTERNAL_CSS_JSON_FILE_PATH = "testpage/internal_css.json";
    public final static String EXTERNAL_CSS_FILE_PATH = "testpage/external_css.html";
    public final static String INTERNAL_AND_EXTERNAL_CSS_FILE_PATH = "testpage/internal_and_external_css.html";
    public final static String OUTPUT_FILE_PATH = "testpage/output_without_style.html";
    public final static String STYLE_FILE_PATH = "testpage/style.css";
    public final static String STYLE_AFTER_PROCESSING_FILE_PATH = "testpage/unused_style.css";
    public final static String TO_BE_SANITIZED_FILE_PATH = "testpage/to_be_sanitized.html";
    public final static String WITHOUT_SCRIPTS_FILE_PATH = "testpage/without_scripts.html";
    public final static String SANITIZED_FILE_PATH = "testpage/sanitized.html";

    public static String getFileContent(String path) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(path).toURI())));
    }

    public static void compareRemovingNewLinesAndTabs(String expected, String actual) {
        if (StringUtils.isEmpty(expected) || StringUtils.isEmpty(actual)) {
            assertEquals(expected, actual);
        } else {
            String normalizedExpected = expected.replaceAll("\n", "").replaceAll("\t", "").replaceAll("\\s+", " ").trim();
            String normalizedActual = actual.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\\s+",
                    " ").trim();
            assertEquals(normalizedExpected, normalizedActual);
        }
    }

    public static void compare(String expected, String actual) {
        if (StringUtils.isEmpty(expected) || StringUtils.isEmpty(actual)) {
            assertEquals(expected, actual);
        } else {
            String normalizedExpected = expected.replaceAll("\\s+", " ").trim();
            String normalizedActual = actual.replaceAll("\\s+",
                    " ").trim();
            assertEquals(normalizedExpected, normalizedActual);
        }
    }
}
