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
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonReader;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.services.StylesInlinerConfig;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;

import static com.adobe.cq.email.core.components.TestFileUtils.INTERNAL_CSS_HTML_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.INTERNAL_CSS_JSON_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.INTERNAL_CSS_WITH_IMMEDIATE_CHILDREN_HTML_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.INTERNAL_CSS_WITH_INNER_PSEUDO_HTML_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.MEDIA_STYLE_AFTER_PROCESSING_WITH_INNER_PSEUDO_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.OTHER_STYLE_AFTER_PROCESSING_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.MEDIA_STYLE_AFTER_PROCESSING_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.MEDIA_STYLE_AFTER_PROCESSING_WITH_IMMEDIATE_CHILDREN_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.OTHER_STYLE_AFTER_PROCESSING_WITH_IMMEDIATE_CHILDREN_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.OTHER_STYLE_AFTER_PROCESSING_WITH_INNER_PSEUDO_FILE_PATH;
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
        this.sut.activate(new StylesInlinerConfig() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return StylesInlinerConfig.class;
            }

            @Override
            public String[] wrapperDivClassesToBeRemoved() {
                return new String[]{"aem-Grid", "aem-GridColumn"};
            }

            @Override
            public String[] htmlInlinerConfiguration() {
                return new String[]{HtmlInlinerConfiguration.IMG_WIDTH_DEFAULT};
            }

            @Override
            public String[] skipUsageSelectors() {
                return new String[]{"table.nonExisting", "\\w*?(\\.ExternalClass(?!\\w))(.*)?"};
            }
        });
    }

    @Test
    void success() throws URISyntaxException, IOException {
        String result =
                sut.getHtmlWithInlineStyles(resourceResolver, getFileContent(INTERNAL_CSS_HTML_FILE_PATH));
        Document document = Jsoup.parse(result);
        Elements elements = document.select(StylesInlinerServiceImpl.STYLE_TAG);
        assertEquals(getFileContent(MEDIA_STYLE_AFTER_PROCESSING_FILE_PATH), elements.get(0).getAllElements().get(0).data());
        assertEquals(getFileContent(OTHER_STYLE_AFTER_PROCESSING_FILE_PATH), elements.get(1).getAllElements().get(0).data());
        checkElements(document, "body", Collections.singletonList("font-family: 'Timmana', 'Gill Sans', sans-serif"), Optional.empty());
        checkElements(document, "h1", Arrays.asList("Margin: 0px", "color: #004488", "font-size: 20px"), Optional.empty());
        checkElements(document, "p", Arrays.asList("Margin: 0px", "color: #004488"), Optional.empty());
        checkElements(document, "img", Arrays.asList("-ms-interpolation-mode: bicubic", "border: 10px solid red"), Optional.empty());
        checkElements(document, "table", Arrays.asList("text-align: center !important", "width: 100%"), Optional.empty());
        checkElements(document, "table td", Collections.singletonList("background-color: #ccc"), Optional.empty());
        checkElements(document, ".blocked", Collections.singletonList("display: block"), Optional.empty());
        checkElements(document, ".footer h3", Arrays.asList("border-bottom-width: 12px", "border: 3px solid green", "color: darkgrey"),
                Optional.empty());
        checkElements(document, "h3.example", Arrays.asList("border-bottom-width: 12px", "border: 3px solid green", "color: darkgrey"),
                Optional.empty());
        checkElements(document, "h3.example2", Arrays.asList("border-bottom-width: 12px", "border: 3px solid green", "color: darkgrey"),
                Optional.empty());
    }

    @Test
    void success_CssWithImmediateChildren() throws URISyntaxException, IOException {
        String result =
                sut.getHtmlWithInlineStyles(resourceResolver, getFileContent(INTERNAL_CSS_WITH_IMMEDIATE_CHILDREN_HTML_FILE_PATH));
        Document document = Jsoup.parse(result);
        Elements elements = document.select(StylesInlinerServiceImpl.STYLE_TAG);
        assertEquals(getFileContent(MEDIA_STYLE_AFTER_PROCESSING_WITH_IMMEDIATE_CHILDREN_FILE_PATH),
                elements.get(0).getAllElements().get(0).data());
        assertEquals(getFileContent(OTHER_STYLE_AFTER_PROCESSING_WITH_IMMEDIATE_CHILDREN_FILE_PATH),
                elements.get(1).getAllElements().get(0).data());
        checkElements(document, "body", Collections.singletonList(""), Optional.empty());
        checkElements(document, "h1", Arrays.asList("Margin: 0px", "color: #004488", "font-size: 20px"), Optional.empty());
        checkElements(document, "p", Arrays.asList("Margin: 0px", "color: #004488"), Optional.empty());
        checkElements(document, "img", Arrays.asList("-ms-interpolation-mode: bicubic", "border: 10px solid red"), Optional.empty());
        checkElements(document, "table",
                Arrays.asList("text-align: center !important", "width: 100%"),
                Optional.of(0));
        checkElements(document, "table",
                Arrays.asList("font-family: 'Timmana', 'Gill Sans', sans-serif", "text-align: center !important", "width: 100%"),
                Optional.of(1));
        checkElements(document, "table td", Collections.singletonList("background-color: #ccc"), Optional.empty());
        checkElements(document, ".blocked", Collections.singletonList("display: block"), Optional.empty());
        checkElements(document, ".footer h3", Arrays.asList("border-bottom-width: 12px", "border: 3px solid green", "color: darkgrey"),
                Optional.empty());
        checkElements(document, "h3.example", Arrays.asList("border-bottom-width: 12px", "border: 3px solid green", "color: darkgrey"),
                Optional.empty());
        checkElements(document, "h3.example2", Arrays.asList("border-bottom-width: 12px", "border: 3px solid green", "color: darkgrey"),
                Optional.empty());
    }

    @Test
    void success_CssWithInnerPseudo() throws URISyntaxException, IOException {
        String result =
                sut.getHtmlWithInlineStyles(resourceResolver, getFileContent(INTERNAL_CSS_WITH_INNER_PSEUDO_HTML_FILE_PATH));
        Document document = Jsoup.parse(result);
        Elements elements = document.select(StylesInlinerServiceImpl.STYLE_TAG);
        assertEquals(getFileContent(MEDIA_STYLE_AFTER_PROCESSING_WITH_INNER_PSEUDO_FILE_PATH),
                elements.get(0).getAllElements().get(0).data());
        assertEquals(getFileContent(OTHER_STYLE_AFTER_PROCESSING_WITH_INNER_PSEUDO_FILE_PATH),
                elements.get(1).getAllElements().get(0).data());
        checkElements(document, "p", Arrays.asList("color: red"), Optional.empty());
    }

    private void checkElements(Document document, String cssQuery, List<String> expectedStyles, Optional<Integer> index) {
        Elements elements = document.select(cssQuery);
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if (index.isPresent() && index.get() != i) {
                continue;
            }
            String style = element.attr(StylesInlinerServiceImpl.STYLE_ATTRIBUTE);
            List<String> currentStyles =
                    Arrays.stream(style.split(";")).map(String::trim).sorted().collect(Collectors.toList());
            assertEquals(expectedStyles, currentStyles);
        }
    }

}
