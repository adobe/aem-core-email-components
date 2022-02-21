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

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.enumerations.HtmlSanitizingMode;

import static com.adobe.cq.email.core.components.TestFileUtils.SANITIZED_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.TO_BE_SANITIZED_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.WITHOUT_SCRIPTS_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.compare;
import static com.adobe.cq.email.core.components.TestFileUtils.getFileContent;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlSanitizerTest {

    @Test
    void nullHtml() {
        assertNull(HtmlSanitizer.sanitizeDocument(HtmlSanitizingMode.FULL, null));
    }

    @Test
    void emptyHtml() {
        assertTrue(HtmlSanitizer.sanitizeHtml(HtmlSanitizingMode.FULL, "").isEmpty());
    }

    @Test
    void doNotSanitize() throws URISyntaxException, IOException {
        compare(getFileContent(TO_BE_SANITIZED_FILE_PATH),
                HtmlSanitizer.sanitizeHtml(HtmlSanitizingMode.NONE, getFileContent(TO_BE_SANITIZED_FILE_PATH)));
    }

    @Test
    void removeScriptsOnly() throws URISyntaxException, IOException {
        compare(getFileContent(WITHOUT_SCRIPTS_FILE_PATH),
                HtmlSanitizer.sanitizeHtml(HtmlSanitizingMode.REMOVE_SCRIPT_TAGS_ONLY, getFileContent(TO_BE_SANITIZED_FILE_PATH))
        );
    }

    @Test
    void removeAll() throws URISyntaxException, IOException {
        compare(getFileContent(SANITIZED_FILE_PATH),
                HtmlSanitizer.sanitizeHtml(HtmlSanitizingMode.FULL, getFileContent(TO_BE_SANITIZED_FILE_PATH))
        );
    }

    @Test
    void nullSanitizingMode() throws URISyntaxException, IOException {
        compare(getFileContent(SANITIZED_FILE_PATH),
                HtmlSanitizer.sanitizeHtml(null, getFileContent(TO_BE_SANITIZED_FILE_PATH))
        );
    }

}