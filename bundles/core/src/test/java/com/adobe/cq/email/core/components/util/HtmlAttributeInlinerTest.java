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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        Element img = document.selectFirst("img");
        assertNotNull(img);
        HtmlAttributeInliner.process(img, null, getDefault());
        assertTrue(img.attr("width").isEmpty());
    }

    @Test
    void emptyStyleToken() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        Element img = document.selectFirst("img");
        assertNotNull(img);
        HtmlAttributeInliner.process(img, styleToken, getDefault());
        assertTrue(img.attr("width").isEmpty());
    }

    @Test
    void nullHtmlInlinerConfiguration() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        Element img = document.selectFirst("img");
        assertNotNull(img);
        HtmlAttributeInliner.process(img, styleToken, null);
        assertTrue(img.attr("width").isEmpty());
    }

    @Test
    void invalidHtmlInlinerConfiguration() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        List<HtmlInlinerConfiguration> htmlInlinerConfigurations = getDefault();
        assertNotNull(htmlInlinerConfigurations);
        htmlInlinerConfigurations.get(0).setHtmlAttributeName(null);
        Element img = document.selectFirst("img");
        assertNotNull(img);
        HtmlAttributeInliner.process(img, styleToken, htmlInlinerConfigurations);
        assertTrue(img.attr("width").isEmpty());
    }

    @Test
    void exceptionThrown() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        List<HtmlInlinerConfiguration> htmlInlinerConfigurations = getDefault();
        assertNotNull(htmlInlinerConfigurations);
        htmlInlinerConfigurations.get(0).setCssPropertyOutputRegEx("?/|\\");
        Element img = document.selectFirst("img");
        assertNotNull(img);
        HtmlAttributeInliner.process(img, styleToken, htmlInlinerConfigurations);
        assertTrue(img.attr("width").isEmpty());
    }

    @Test
    void success_WidthInPixels_NotExistingAttribute() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        Element img = document.selectFirst("img");
        assertNotNull(img);
        HtmlAttributeInliner.process(img, styleToken, getDefault());
        assertEquals("500", img.attr("width"));
    }

    @Test
    void success_WidthInCapitalPixels_NotExistingAttribute() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500PX");
        Element img = document.selectFirst("img");
        assertNotNull(img);
        HtmlAttributeInliner.process(img, styleToken, getDefault());
        assertEquals("500", img.attr("width"));
    }

    @Test
    void success_MultipleHtmlInlinerConfigurationsForSameElement_NotExistingAttribute() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        StyleTokenFactory.addProperties(styleToken, "height: 400px");
        StyleTokenFactory.addProperties(styleToken, "text-align: right");
        Element img = document.selectFirst("img");
        assertNotNull(img);
        List<HtmlInlinerConfiguration> htmlInlinerConfigurations = new ArrayList<>();
        htmlInlinerConfigurations.add(HtmlInlinerConfiguration.parse(HtmlInlinerConfiguration.IMG_WIDTH_DEFAULT));
        htmlInlinerConfigurations.add(HtmlInlinerConfiguration.parse("{\"elementType\":\"img\",\"cssPropertyRegEx\":\"height\"," +
                "\"cssPropertyOutputRegEx\":\"[0-9]+(?=px)|[0-9]+(?=PX)" +
                "|[0-9]+[%]\",\"htmlAttributeName\":\"height\",\"overrideIfAlreadyExisting\":true}"));
        htmlInlinerConfigurations.add(HtmlInlinerConfiguration.parse("{\"elementType\":\"img\",\"cssPropertyRegEx\":\"text-align\"," +
                "\"cssPropertyOutputRegEx\":\".*\",\"htmlAttributeName\":\"align\",\"overrideIfAlreadyExisting\":true}"));
        HtmlAttributeInliner.process(img, styleToken, htmlInlinerConfigurations);
        assertEquals("500", img.attr("width"));
        assertEquals("400", img.attr("height"));
        assertEquals("right", img.attr("align"));
    }

    @Test
    void success_PercentageWidth_NotExistingAttribute() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 75%");
        Element img = document.selectFirst("img");
        assertNotNull(img);
        HtmlAttributeInliner.process(img, styleToken, getDefault());
        assertEquals("75%", img.attr("width"));
    }

    @Test
    void success_OverrideExistingAttribute() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_AND_WIDTH_ON_IMAGE_ELEMENT_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        Element img = document.selectFirst("img");
        assertNotNull(img);
        HtmlAttributeInliner.process(img, styleToken, getDefault());
        assertEquals("500", img.attr("width"));
    }

    @Test
    void success_DoNotOverrideExistingAttribute() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(PAGE_WITH_IMAGE_WIDTH_STYLE_AND_WIDTH_ON_IMAGE_ELEMENT_FILE_PATH));
        StyleToken styleToken = StyleTokenFactory.create("img");
        StyleTokenFactory.addProperties(styleToken, "font-family: 'Timmana', \"Gill Sans\", sans-serif;");
        StyleTokenFactory.addProperties(styleToken, "width: 500px");
        List<HtmlInlinerConfiguration> htmlInlinerConfigurations = getDefault();
        assertNotNull(htmlInlinerConfigurations);
        htmlInlinerConfigurations.get(0).setOverrideIfAlreadyExisting(false);
        Element img = document.selectFirst("img");
        assertNotNull(img);
        HtmlAttributeInliner.process(img, styleToken, htmlInlinerConfigurations);
        assertEquals("128", img.attr("width"));
    }

    private List<HtmlInlinerConfiguration> getDefault() {
        return Collections.singletonList(HtmlInlinerConfiguration.parse(HtmlInlinerConfiguration.IMG_WIDTH_DEFAULT));
    }
}