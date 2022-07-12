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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.TestFileUtils;

import static com.adobe.cq.email.core.components.TestFileUtils.compareRemovingNewLinesAndTabs;

class WrapperDivRemoverTest {

    @Test
    void noRemovedDivs() throws URISyntaxException, IOException {
        String html = TestFileUtils.getFileContent(TestFileUtils.WRAPPER_DIV_REMOVAL_INPUT_FILE_PATH);
        Document document = Jsoup.parse(html);
        WrapperDivRemover.removeWrapperDivs(document, new String[]{"not-existing-div-class"});
        compareRemovingNewLinesAndTabs(TestFileUtils.getFileContent(TestFileUtils.WRAPPER_DIV_REMOVAL_OUTPUT_DIVS_NOT_REMOVED_FILE_PATH),
                document.outerHtml());
    }

    @Test
    void nonDivClasses() throws URISyntaxException, IOException {
        String html = TestFileUtils.getFileContent(TestFileUtils.WRAPPER_DIV_REMOVAL_INPUT_FILE_PATH);
        Document document = Jsoup.parse(html);
        WrapperDivRemover.removeWrapperDivs(document, new String[]{"cmp-image__image", "cmp-button"});
        compareRemovingNewLinesAndTabs(TestFileUtils.getFileContent(TestFileUtils.WRAPPER_DIV_REMOVAL_OUTPUT_DIVS_NOT_REMOVED_FILE_PATH),
                document.outerHtml());
    }

    @Test
    void removedDivs() throws URISyntaxException, IOException {
        String html = TestFileUtils.getFileContent(TestFileUtils.WRAPPER_DIV_REMOVAL_INPUT_FILE_PATH);
        Document document = Jsoup.parse(html);
        WrapperDivRemover.removeWrapperDivs(document, new String[]{"responsivegrid", "aem-Grid", "aem-GridColumn", "cmp-title__text"});
        compareRemovingNewLinesAndTabs(TestFileUtils.getFileContent(TestFileUtils.WRAPPER_DIV_REMOVAL_OUTPUT_DIVS_REMOVED_FILE_PATH),
                document.outerHtml());
    }
}