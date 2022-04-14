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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonReader;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.constants.StylesInlinerConstants;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;

import static com.adobe.cq.email.core.components.TestFileUtils.INTERNAL_CSS_HTML_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.INTERNAL_CSS_JSON_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.STYLE_AFTER_PROCESSING_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.compare;
import static com.adobe.cq.email.core.components.TestFileUtils.compareRemovingNewLinesAndTabs;
import static com.adobe.cq.email.core.components.TestFileUtils.getFileContent;

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
                sut.getHtmlWithInlineStyles(resourceResolver, getFileContent(INTERNAL_CSS_HTML_FILE_PATH));
        Document document = Jsoup.parse(result);
        compareRemovingNewLinesAndTabs(getFileContent(STYLE_AFTER_PROCESSING_FILE_PATH),
                document.selectFirst(StylesInlinerConstants.STYLE_TAG).getAllElements().get(0).data());
        checkElements(document, "body", Collections.singletonList("font-family: 'Timmana', 'Gill Sans', sans-serif"));
        checkElements(document, "h1", Arrays.asList("Margin: 0px", "color: #004488", "font-size: 20px"));
        checkElements(document, "p", Arrays.asList("Margin: 0px", "color: #004488"));
        checkElements(document, "img", Arrays.asList("-ms-interpolation-mode: bicubic", "border: 10px solid red"));
        checkElements(document, "table", Arrays.asList("text-align: center !important", "width: 100%"));
        checkElements(document, "table td", Collections.singletonList("background-color: #ccc"));
        checkElements(document, ".blocked", Collections.singletonList("display: block"));
        checkElements(document, ".footer h3", Arrays.asList("border-bottom-width: 12px", "border: 3px solid green", "color: darkgrey"));
        checkElements(document, "h3.example", Arrays.asList("border-bottom-width: 12px", "border: 3px solid green", "color: darkgrey"));
        checkElements(document, "h3.example2", Arrays.asList("border-bottom-width: 12px", "border: 3px solid green", "color: darkgrey"));
    }

    @Test
    void succes_Json() throws URISyntaxException, IOException {
        String result =
                sut.getHtmlWithInlineStyles(resourceResolver, getFileContent(INTERNAL_CSS_JSON_FILE_PATH),
                        StandardCharsets.UTF_8.name());
        JsonReader reader = Json.createReader(new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8)));
        String html = reader.readObject().getString("html");
        Document document = Jsoup.parse(html);
        compareRemovingNewLinesAndTabs(getFileContent(STYLE_AFTER_PROCESSING_FILE_PATH),
                document.selectFirst(StylesInlinerConstants.STYLE_TAG).getAllElements().get(0).data());
        checkElements(document, "body", Collections.singletonList("font-family: 'Timmana', 'Gill Sans', sans-serif"));
        checkElements(document, "h1", Arrays.asList("Margin: 0px", "color: #004488", "font-size: 20px"));
        checkElements(document, "p", Arrays.asList("Margin: 0px", "color: #004488"));
        checkElements(document, "img", Arrays.asList("-ms-interpolation-mode: bicubic", "border: 10px solid red"));
        checkElements(document, "table", Arrays.asList("text-align: center !important", "width: 100%"));
        checkElements(document, "table td", Collections.singletonList("background-color: #ccc"));
        checkElements(document, ".blocked", Collections.singletonList("display: block"));
        checkElements(document, ".footer h3", Arrays.asList("border-bottom-width: 12px", "border: 3px solid green", "color: darkgrey"));
        checkElements(document, "h3.example", Arrays.asList("border-bottom-width: 12px", "border: 3px solid green", "color: darkgrey"));
        checkElements(document, "h3.example2", Arrays.asList("border-bottom-width: 12px", "border: 3px solid green", "color: darkgrey"));
    }

    private void checkElements(Document document, String cssQuery, List<String> expectedStyles) {
        for (Element element : document.select(cssQuery)) {
            String style = element.attr(StylesInlinerConstants.STYLE_ATTRIBUTE);
            List<String> currentStyles =
                    Arrays.stream(style.split(";")).map(String::trim).sorted().collect(Collectors.toList());
            Assertions.assertEquals(expectedStyles, currentStyles);
        }
    }

}
