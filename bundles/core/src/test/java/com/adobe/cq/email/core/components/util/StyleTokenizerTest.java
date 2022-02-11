package com.adobe.cq.email.core.components.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.pojo.StyleToken;

import static com.adobe.cq.email.core.components.TestFileUtils.STYLE_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.compare;
import static com.adobe.cq.email.core.components.TestFileUtils.getFileContent;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StyleTokenizerTest {

    @Test
    void nullCss() {
        assertTrue(StyleTokenizer.tokenize(null).isEmpty());
    }

    @Test
    void emptyCss() {
        assertTrue(StyleTokenizer.tokenize("").isEmpty());
    }

    @Test
    void success() throws URISyntaxException, IOException {
        List<StyleToken> result = StyleTokenizer.tokenize(getFileContent(STYLE_FILE_PATH));
        StringBuilder builder = new StringBuilder();
        for (StyleToken styleToken : result) {
            builder.append(StyleTokenFactory.toCss(styleToken)).append("\n");
        }
        compare(getFileContent(STYLE_FILE_PATH), builder.toString());
    }

}