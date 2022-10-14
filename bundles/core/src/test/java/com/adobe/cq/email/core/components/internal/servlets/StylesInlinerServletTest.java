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
package com.adobe.cq.email.core.components.internal.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.internal.util.InlinerResponseWrapper;
import com.adobe.cq.email.core.components.services.StylesInlinerService;
import com.day.cq.wcm.api.WCMMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StylesInlinerServletTest {
    private static final String INPUT = "TEST_PAGE";
    private static final String OUTPUT = "TEST_PAGE_WITH_INLINED_CSS";

    @Mock
    StylesInlinerService stylesInlinerService;
    @Mock
    Resource resource;
    @Mock
    SlingHttpServletRequest request;
    @Mock RequestPathInfo requestPathInfo;
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
        this.sut.stylesInlinerService = stylesInlinerService;

        when(request.getResource()).thenReturn(resource);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[] { StylesInlinerServlet.INLINE_STYLES_SELECTOR });
        when(resp.getWriter()).thenReturn(printWriter);
        when(stylesInlinerService.getHtmlWithInlineStyles(eq(resourceResolver), eq(INPUT))).thenReturn(OUTPUT);
        when(resp.getCharacterEncoding()).thenReturn("utf-8");
    }

    @Test
    void success() throws Exception {
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getRequestDispatcher(any(Resource.class), any(RequestDispatcherOptions.class))).thenReturn(dispatcher);
        doAnswer(inv -> {
            SlingHttpServletRequest dispatchedRequest = inv.getArgument(0);
            HttpServletResponse dispatchedResponse = inv.getArgument(1);

            assertEquals(WCMMode.DISABLED, WCMMode.fromRequest(dispatchedRequest));
            assertTrue(dispatchedResponse instanceof InlinerResponseWrapper);

            dispatchedResponse.getWriter().write(INPUT);
            return null;
        }).when(dispatcher).forward(any(), any());

        sut.doGet(request, resp);
        verify(printWriter).write(eq(OUTPUT));
    }

}
