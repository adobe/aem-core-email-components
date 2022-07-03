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
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.pojo.HtmlInlinerConfiguration;
import com.adobe.cq.email.core.components.pojo.StyleToken;

import static com.adobe.cq.email.core.components.TestFileUtils.PAGE_WITH_IMAGE_WIDTH_STYLE_AND_WIDTH_ON_IMAGE_ELEMENT_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.getFileContent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlAttributeInlinerTest {

    @Test
    void nullDocument() {
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        HtmlAttributeInliner.process(null, styleToken, getDefault());
    }

    @Test
    void nullStyleToken() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        HtmlAttributeInliner.process(document, null, getDefault());
        Element img = document.selectFirst("img");
        assertNotNull(img);
        assertTrue(img.attr("width").isEmpty());
    }

    @Test
    void emptyStyleToken() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        HtmlAttributeInliner.process(document, styleToken, getDefault());
        Element img = document.selectFirst("img");
        assertNotNull(img);
        assertTrue(img.attr("width").isEmpty());
    }

    @Test
    void nullHtmlInlinerConfiguration() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        HtmlAttributeInliner.process(document, styleToken, null);
        Element img = document.selectFirst("img");
        assertNotNull(img);
        assertTrue(img.attr("width").isEmpty());
    }

    @Test
    void invalidHtmlInlinerConfiguration() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        HtmlInlinerConfiguration htmlInlinerConfiguration = getDefault();
        assertNotNull(htmlInlinerConfiguration);
        htmlInlinerConfiguration.setHtmlAttributeName(null);
        HtmlAttributeInliner.process(document, styleToken, htmlInlinerConfiguration);
        Element img = document.selectFirst("img");
        assertNotNull(img);
        assertTrue(img.attr("width").isEmpty());
    }

    @Test
    void exceptionThrown() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        HtmlInlinerConfiguration htmlInlinerConfiguration = getDefault();
        assertNotNull(htmlInlinerConfiguration);
        htmlInlinerConfiguration.setCssPropertyOutputRegEx("?/|\\");
        HtmlAttributeInliner.process(document, styleToken, htmlInlinerConfiguration);
        Element img = document.selectFirst("img");
        assertNotNull(img);
        assertTrue(img.attr("width").isEmpty());
    }

    @Test
    void success_WidthInPixels_NotExistingAttribute() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        HtmlAttributeInliner.process(document, styleToken, getDefault());
        Element img = document.selectFirst("img");
        assertNotNull(img);
        assertEquals("500", img.attr("width"));
    }

    @Test
    void success_WidthInCapitalPixels_NotExistingAttribute() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500PX");
        HtmlAttributeInliner.process(document, styleToken, getDefault());
        Element img = document.selectFirst("img");
        assertNotNull(img);
        assertEquals("500", img.attr("width"));
    }

    @Test
    void success_PercentageWidth_NotExistingAttribute() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 75%");
        HtmlAttributeInliner.process(document, styleToken, getDefault());
        Element img = document.selectFirst("img");
        assertNotNull(img);
        assertEquals("75%", img.attr("width"));
    }

    @Test
    void success_OverrideExistingAttribute() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_AND_WIDTH_ON_IMAGE_ELEMENT_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        HtmlAttributeInliner.process(document, styleToken, getDefault());
        Element img = document.selectFirst("img");
        assertNotNull(img);
        assertEquals("500", img.attr("width"));
    }

    @Test
    void success_DoNotOverrideExistingAttribute() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_AND_WIDTH_ON_IMAGE_ELEMENT_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        HtmlInlinerConfiguration htmlInlinerConfiguration = getDefault();
        assertNotNull(htmlInlinerConfiguration);
        htmlInlinerConfiguration.setOverrideIfAlreadyExisting(false);
        HtmlAttributeInliner.process(document, styleToken, htmlInlinerConfiguration);
        Element img = document.selectFirst("img");
        assertNotNull(img);
        assertEquals("128", img.attr("width"));
    }

    private HtmlInlinerConfiguration getDefault() {
        return HtmlInlinerConfiguration.parse(HtmlInlinerConfiguration.IMG_WIDTH_DEFAULT);
    }
}