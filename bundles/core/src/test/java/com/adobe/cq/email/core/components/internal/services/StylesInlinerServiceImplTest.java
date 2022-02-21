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
package com.adobe.cq.email.core.components.internal.services;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.constants.StylesInlinerConstants;
import com.adobe.cq.email.core.components.enumerations.HtmlSanitizingMode;
import com.adobe.cq.email.core.components.enumerations.StyleMergerMode;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;

import static com.adobe.cq.email.core.components.TestFileUtils.INTERNAL_CSS_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.STYLE_AFTER_PROCESSING_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.compare;
import static com.adobe.cq.email.core.components.TestFileUtils.getFileContent;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StylesInlinerServiceImplTest {

    @Mock
    ResourceResolver resourceResolver;
    @Mock
    RequestResponseFactory requestResponseFactory;
    @Mock
    SlingRequestProcessor requestProcessor;

    private StylesInlinerServiceImpl sut;

    @BeforeEach
    void setUp() {
        this.sut = new StylesInlinerServiceImpl();
        this.sut.setRequestResponseFactory(requestResponseFactory);
        this.sut.setRequestProcessor(requestProcessor);
    }

    @Test
    void success() throws URISyntaxException, IOException {
        String result =
                sut.getHtmlWithInlineStyles(resourceResolver, getFileContent(INTERNAL_CSS_FILE_PATH), StyleMergerMode.PROCESS_SPECIFICITY
                        , HtmlSanitizingMode.FULL);
        Document document = Jsoup.parse(result);
        compare(getFileContent(STYLE_AFTER_PROCESSING_FILE_PATH),
                document.selectFirst(StylesInlinerConstants.STYLE_TAG).getAllElements().get(0).data());
        checkElements(document, "body", "font-family: 'Timmana', 'Gill Sans', sans-serif;");
        checkElements(document, "h1", "font-size: 20px; color: #004488; Margin: 0px;");
        checkElements(document, "p", "color: #004488; Margin: 0px;");
        checkElements(document, "img", "border: 10px solid red; -ms-interpolation-mode: bicubic;");
        checkElements(document, "table", "text-align: center !important; width: 100%;");
        checkElements(document, "table td", "background-color: #ccc;");
        checkElements(document, ".blocked", "display: inline-block;");
        checkElements(document, "h3.example", "border-bottom-width: 12px; border:3px solid green;");
        checkElements(document, "h3.example2", "border:3px solid green; border-bottom-width:12px;");
    }

    private void checkElements(Document document, String cssQuery, String expectedStyle) {
        for (Element element : document.select(cssQuery)) {
            assertEquals(expectedStyle, element.attr(StylesInlinerConstants.STYLE_ATTRIBUTE));
        }
    }

}