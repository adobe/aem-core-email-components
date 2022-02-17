
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