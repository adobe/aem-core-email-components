package com.adobe.cq.email.core.components.util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import com.adobe.cq.email.core.components.TestFileUtils;

class WrapperDivRemoverTest {
    @Test
    void test() throws URISyntaxException, IOException {
        String html = TestFileUtils.getFileContent(TestFileUtils.WRAPPER_DIV_REMOVAL_INPUT_FILE_PATH);
        Document document = Jsoup.parse(html);
        WrapperDivRemover.removeWrapperDivs(document, new String[]{"aem-Grid", "aem-GridColumn"});
    }
}