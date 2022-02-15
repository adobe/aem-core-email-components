package com.adobe.cq.email.core.components.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.config.StylesInlinerConfig;
import com.adobe.cq.email.core.components.enumerations.StyleMergerMode;
import com.adobe.cq.email.core.components.services.StylesInlinerService;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.drew.lang.Charsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StylesInlinerServletTest {
    private static final String INPUT = "TEST_PAGE";
    private static final String OUTPUT_PROCESSING_CSS_SPECIFICITY = "TEST_PAGE_WITH_INLINED_CSS_USING_SPECIFICITY";
    private static final String OUTPUT_IGNORING_CSS_SPECIFICITY = "TEST_PAGE_WITH_INLINED_CSS_WITHOUT_USING_SPECIFICITY";
    private static final String OUTPUT_ALWAYS_APPENDING_CSS_PROPERTIES = "TEST_PAGE_WITH_INLINED_CSS_ALWAYS_APPENDING_CSS_PROPERTIES";


    @Mock
    RequestResponseFactory requestResponseFactory;

    @Mock
    SlingRequestProcessor requestProcessor;

    @Mock
    StylesInlinerService stylesInlinerService;

    @Mock
    SlingHttpServletRequest request;
    @Mock
    SlingHttpServletResponse resp;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    PrintWriter printWriter;

    private StylesInlinerServlet sut;

    @BeforeEach
    void setUp() throws IOException {
        this.sut = new StylesInlinerServlet();
        this.sut.setRequestResponseFactory(requestResponseFactory);
        this.sut.setRequestProcessor(requestProcessor);
        this.sut.setStylesInlinerService(
                stylesInlinerService
        );
        Resource resource = mock(Resource.class);
        when(request.getResource()).thenReturn(resource);
        when(resource.getPath()).thenReturn("TEST_PATH");
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(requestResponseFactory.createRequest(eq("GET"), eq("TEST_PATH.html"), anyMap())).thenReturn(httpServletRequest);
        doAnswer(i -> {
            OutputStream outputStream = (OutputStream) i.getArguments()[0];
            IOUtils.write(INPUT, outputStream, Charsets.UTF_8);
            return mock(HttpServletResponse.class);
        }).when(requestResponseFactory).createResponse(any());
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resp.getWriter()).thenReturn(printWriter);
        doAnswer(i -> {
            StyleMergerMode styleMergerMode = i.getArgument(2);
            if (StyleMergerMode.IGNORE_SPECIFICITY.equals(styleMergerMode)) {
                return OUTPUT_IGNORING_CSS_SPECIFICITY;
            }
            if (StyleMergerMode.ALWAYS_APPEND.equals(styleMergerMode)) {
                return OUTPUT_ALWAYS_APPENDING_CSS_PROPERTIES;
            }
            return OUTPUT_PROCESSING_CSS_SPECIFICITY;
        }).when(stylesInlinerService).getHtmlWithInlineStyles(eq(resourceResolver), eq(INPUT),
                any(StyleMergerMode.class));
    }

    @Test
    void noConfig() throws ServletException, IOException {
        sut.doGet(request, resp);
        verify(printWriter).write(eq(OUTPUT_PROCESSING_CSS_SPECIFICITY));
    }

    @Test
    void notExistingStyleMergerMode() throws Exception {
        StylesInlinerConfig stylesInlinerConfig = new StylesInlinerConfig();
        stylesInlinerConfig.setStylesMergingMode("UNKNOWN_MODE");
        this.sut.setStylesInlinerConfig(stylesInlinerConfig);
        sut.doGet(request, resp);
        verify(printWriter).write(eq(OUTPUT_PROCESSING_CSS_SPECIFICITY));
    }

    @Test
    void processingCssSpecificity() throws Exception {
        StylesInlinerConfig stylesInlinerConfig = new StylesInlinerConfig();
        stylesInlinerConfig.setStylesMergingMode(StyleMergerMode.PROCESS_SPECIFICITY.name());
        this.sut.setStylesInlinerConfig(stylesInlinerConfig);
        sut.doGet(request, resp);
        verify(printWriter).write(eq(OUTPUT_PROCESSING_CSS_SPECIFICITY));
    }

    @Test
    void ignoringCssSpecificity() throws Exception {
        StylesInlinerConfig stylesInlinerConfig = new StylesInlinerConfig();
        stylesInlinerConfig.setStylesMergingMode(StyleMergerMode.IGNORE_SPECIFICITY.name());
        this.sut.setStylesInlinerConfig(stylesInlinerConfig);
        sut.doGet(request, resp);
        verify(printWriter).write(eq(OUTPUT_IGNORING_CSS_SPECIFICITY));
    }

    @Test
    void alwaysAppendingCssProperties() throws Exception {
        StylesInlinerConfig stylesInlinerConfig = new StylesInlinerConfig();
        stylesInlinerConfig.setStylesMergingMode(StyleMergerMode.ALWAYS_APPEND.name());
        this.sut.setStylesInlinerConfig(stylesInlinerConfig);
        sut.doGet(request, resp);
        verify(printWriter).write(eq(OUTPUT_ALWAYS_APPENDING_CSS_PROPERTIES));
    }
}