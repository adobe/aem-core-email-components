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
    public final static String INTERNAL_CSS_WITH_IMMEDIATE_CHILDREN_HTML_FILE_PATH = "testpage/internal_css_with_immediate_children.html";
    public final static String INTERNAL_CSS_JSON_FILE_PATH = "testpage/internal_css.json";
    public final static String EXTERNAL_CSS_FILE_PATH = "testpage/external_css.html";
    public final static String INTERNAL_AND_EXTERNAL_CSS_FILE_PATH = "testpage/internal_and_external_css.html";
    public final static String PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH = "testpage/page_with_image_width_style.html";
    public final static String PAGE_WITH_IMAGE_WIDTH_STYLE_AND_WIDTH_ON_IMAGE_ELEMENT_FILE_PATH =
            "testpage/page_with_image_width_style_and_width_on_image_element.html";
    public final static String OUTPUT_FILE_PATH = "testpage/output_without_style.html";
    public final static String STYLE_FILE_PATH = "testpage/style.css";
    public final static String STYLE_WITHOUT_LAST_SEMICOLON_FILE_PATH = "testpage/style_without_last_semicolon.css";

    public final static String MEDIA_STYLE_AFTER_PROCESSING_FILE_PATH = "testpage/media_unused_style.css";
    public final static String OTHER_STYLE_AFTER_PROCESSING_FILE_PATH = "testpage/other_unused_style.css";
    public final static String MEDIA_STYLE_AFTER_PROCESSING_WITH_IMMEDIATE_CHILDREN_FILE_PATH =
            "testpage/media_style_with_immediate_children.css";
    public final static String OTHER_STYLE_AFTER_PROCESSING_WITH_IMMEDIATE_CHILDREN_FILE_PATH =
            "testpage/other_style_with_immediate_children.css";
    public final static String TO_BE_SANITIZED_FILE_PATH = "testpage/to_be_sanitized.html";
    public final static String WITHOUT_SCRIPTS_FILE_PATH = "testpage/without_scripts.html";
    public final static String SANITIZED_FILE_PATH = "testpage/sanitized.html";
    public final static String WRAPPER_DIV_REMOVAL_INPUT_FILE_PATH = "wrapper-div-removal/input.html";
    public final static String WRAPPER_DIV_REMOVAL_OUTPUT_DIVS_REMOVED_FILE_PATH = "wrapper-div-removal/output_divs_removed.html";
    public final static String WRAPPER_DIV_REMOVAL_OUTPUT_DIVS_NOT_REMOVED_FILE_PATH = "wrapper-div-removal/output_divs_not_removed.html";
    public static final String INTERNAL_CSS_WITH_INNER_PSEUDO_HTML_FILE_PATH = "testpage/internal_css_with_inner_pseudo.html";
    public final static String MEDIA_STYLE_AFTER_PROCESSING_WITH_INNER_PSEUDO_FILE_PATH =
            "testpage/media_style_with_inner_pseudo.css";
    public final static String OTHER_STYLE_AFTER_PROCESSING_WITH_INNER_PSEUDO_FILE_PATH =
            "testpage/other_style_with_inner_pseudo.css";


    public static String getFileContent(String path) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(path).toURI())));
    }

    public static void compareRemovingNewLinesAndTabs(String expected, String actual) {
        if (StringUtils.isEmpty(expected) || StringUtils.isEmpty(actual)) {
            assertEquals(expected, actual);
        } else {
            String normalizedExpected = expected.replaceAll(System.lineSeparator(), "").replaceAll("\t", "")
                    .replaceAll(">\\s+<", "><").replaceAll("\\s+", " ").trim();
            String normalizedActual =
                    actual.replaceAll(System.lineSeparator(), "").replaceAll("\t", "").replaceAll(">\\s+<", "><").replaceAll("\\s+",
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
