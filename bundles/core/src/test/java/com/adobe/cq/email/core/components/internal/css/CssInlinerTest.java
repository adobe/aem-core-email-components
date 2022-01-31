package com.adobe.cq.email.core.components.internal.css;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.internal.css.CssInliner.Result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CssInlinerTest {

    @Test
    void nothingToParse() {
        CssInliner sut = new CssInliner();
        assertThrows(CssInliner.CssInlinerException.class, () -> sut.process(null));
    }

    @Test
    void styleInliningSuccess() throws IOException, URISyntaxException {
        CssInliner sut = new CssInliner();
        String expectedOutput =
                new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("css/inputStyle.txt").toURI())));
        assertEquals(expectedOutput, sut.process(getInputHtml()).getInlineStyle());
    }

    @Test
    void getSelector() throws URISyntaxException, IOException {
        CssInliner sut = new CssInliner();
        Result result = sut.process(getInputHtml());
        Map<String, String> selectorMap = result.getSelectorMap();
        assertEquals("font-size: 20px;color: #004488; Margin: 0px;", selectorMap.get("h1"));
    }

    @Test
    void getSelectorWithDuplicateProperties() throws URISyntaxException, IOException {
        CssInliner sut = new CssInliner();
        Result result = sut.process(getInputHtml());
        Map<String, String> selectorMap = result.getSelectorMap();
        assertEquals("display: block; display: inline-block;", selectorMap.get(".blocked"));
    }

    @Test
    void outputHtmlSuccess() throws URISyntaxException, IOException {
        CssInliner sut = new CssInliner();
        String expectedOutput =
                new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("css/output.html").toURI())));
        assertEquals(expectedOutput.replaceAll("\\s+", ""), sut.process(getInputHtml()).getOutputHtml().replaceAll("\\s+", ""));
    }

    @Test
    void styleIsPreservedInOutputHtml() throws URISyntaxException, IOException {
        CssInliner sut = new CssInliner();
        String initialStyleWithCommentedBlock =
                new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("css/initialStyleWithCommentedBlock.txt").toURI())));
        String outputHtml = sut.process(getInputHtml()).getOutputHtml();
        assertTrue(outputHtml.replaceAll("\n", " ").replaceAll(" +", " ").contains(initialStyleWithCommentedBlock));
    }

    private String getInputHtml() throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("css/input.html").toURI())));
    }
}