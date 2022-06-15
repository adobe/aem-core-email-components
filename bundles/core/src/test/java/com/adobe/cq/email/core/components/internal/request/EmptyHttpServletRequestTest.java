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
package com.adobe.cq.email.core.components.internal.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpUpgradeHandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmptyHttpServletRequestTest {

    private EmptyHttpServletRequest sut = new EmptyHttpServletRequest();

    @Test
    public void getAttribute() {
        assertNull(sut.getAttribute("NAME"));
    }

    @Test
    public void getAttributeNames() {
        assertNull(sut.getAttributeNames());
    }

    @Test
    public void getCharacterEncoding() {
        assertNull(sut.getCharacterEncoding());
    }

    @Test
    public void setCharacterEncoding() throws UnsupportedEncodingException {
        sut.setCharacterEncoding("ENV");
    }

    @Test
    public void getContentLength() {
        assertEquals(0, sut.getContentLength());
    }

    @Test
    public void getContentType() {
        assertNull(sut.getContentType());
    }

    @Test
    public void getInputStream() throws IOException {
        assertNull(sut.getInputStream());
    }

    @Test
    public void getParameter() {
        assertNull(sut.getParameter("NAME"));
    }

    @Test
    public void getParameterNames() {
        assertNull(sut.getParameterNames());
    }

    @Test
    public void getParameterValues() {
        assertNull(sut.getParameterValues("NAME"));
    }

    @Test
    public void getParameterMap() {
        assertNull(sut.getParameterMap());
    }

    @Test
    public void getProtocol() {
        assertNull(sut.getProtocol());
    }

    @Test
    public void getScheme() {
        assertNull(sut.getScheme());
    }

    @Test
    public void getServerName() {
        assertNull(sut.getServerName());
    }

    @Test
    public void getServerPort() {
        assertEquals(0, sut.getServerPort());
    }

    @Test
    public void getReader() throws IOException {
        assertNull(sut.getReader());
    }

    @Test
    public void getRemoteAddr() {
        assertNull(sut.getRemoteAddr());
    }

    @Test
    public void getRemoteHost() {
        assertNull(sut.getRemoteHost());
    }

    @Test
    public void setAttribute() {
        sut.setAttribute("NAME", new Object());
    }

    @Test
    public void removeAttribute() {
        sut.removeAttribute("NAME");
    }

    @Test
    public void getLocale() {
        assertNull(sut.getLocale());
    }

    @Test
    public void getLocales() {
        assertNull(sut.getLocales());
    }

    @Test
    public void isSecure() {
        assertFalse(sut.isSecure());
    }

    @Test
    public void getRequestDispatcher() {
        assertNull(sut.getRequestDispatcher("PATH"));
    }

    @Test
    public void getRealPath() {
        assertNull(sut.getRealPath("PATH"));
    }

    @Test
    public void getRemotePort() {
        assertEquals(0, sut.getRemotePort());
    }

    @Test
    public void getLocalName() {
        assertNull(sut.getLocalName());
    }

    @Test
    public void getLocalAddr() {
        assertNull(sut.getLocalAddr());
    }

    @Test
    public void getLocalPort() {
        assertEquals(0, sut.getLocalPort());
    }

    @Test
    public void getAuthType() {
        assertNull(sut.getAuthType());
    }

    @Test
    public void getCookies() {
        assertNull(sut.getCookies());
    }

    @Test
    public void getDateHeader() {
        assertEquals(0, sut.getDateHeader("NAME"));
    }

    @Test
    public void getHeader() {
        assertNull(sut.getHeader("NAME"));
    }

    @Test
    public void getHeaders() {
        assertNull(sut.getHeaders("NAME"));
    }

    @Test
    public void getHeaderNames() {
        assertNull(sut.getHeaderNames());
    }

    @Test
    public void getIntHeader() {
        assertEquals(0, sut.getIntHeader("NAME"));
    }

    @Test
    public void getMethod() {
        assertNull(sut.getMethod());
    }

    @Test
    public void getPathInfo() {
        assertNull(sut.getPathInfo());
    }

    @Test
    public void getPathTranslated() {
        assertNull(sut.getPathTranslated());
    }

    @Test
    public void getContextPath() {
        assertNull(sut.getContextPath());
    }

    @Test
    public void getQueryString() {
        assertNull(sut.getQueryString());
    }

    @Test
    public void getRemoteUser() {
        assertNull(sut.getRemoteUser());
    }

    @Test
    public void isUserInRole() {
        assertFalse(sut.isUserInRole("ROLE"));
    }

    @Test
    public void getUserPrincipal() {
        assertNull(sut.getUserPrincipal());
    }

    @Test
    public void getRequestedSessionId() {
        assertNull(sut.getRequestedSessionId());
    }

    @Test
    public void getRequestURI() {
        assertNull(sut.getRequestURI());
    }

    @Test
    public void getRequestURL() {
        assertNull(sut.getRequestURL());
    }

    @Test
    public void getServletPath() {
        assertNull(sut.getServletPath());
    }

    @Test
    public void getSessionWithArg() {
        assertNull(sut.getSession(true));
    }

    @Test
    public void getSession() {
        assertNull(sut.getSession());
    }

    @Test
    public void isRequestedSessionIdValid() {
        assertFalse(sut.isRequestedSessionIdValid());
    }

    @Test
    public void isRequestedSessionIdFromCookie() {
        assertFalse(sut.isRequestedSessionIdFromCookie());
    }

    @Test
    public void isRequestedSessionIdFromURL() {
        assertFalse(sut.isRequestedSessionIdFromURL());
    }

    @Test
    public void isRequestedSessionIdFromUrl() {
        assertFalse(sut.isRequestedSessionIdFromUrl());
    }

    @Test
    public void upgrade() throws IOException, ServletException {
        assertNull(sut.upgrade(HttpUpgradeHandler.class));
    }

    @Test
    public void changeSessionId() {
        assertNull(sut.changeSessionId());
    }

    @Test
    public void getDispatcherType() {
        assertNull(sut.getDispatcherType());
    }

    @Test
    public void startAsync() throws IllegalStateException {
        assertNull(sut.startAsync());
    }

    @Test
    public void startAsyncWithArgs() throws IllegalStateException {
        assertNull(sut.startAsync(null, null));
    }

    @Test
    public void isAsyncStarted() {
        assertFalse(sut.isAsyncStarted());
    }

    @Test
    public void isAsyncSupported() {
        assertFalse(sut.isAsyncSupported());
    }

    @Test
    public void getParts() {
        assertNull(sut.getParts());
    }

    @Test
    public void getPart() {
        assertNull(sut.getPart("PART"));
    }

    @Test
    public void authenticate() {
        assertFalse(sut.authenticate(null));
    }

    @Test
    public void getAsyncContext() {
        assertNull(sut.getAsyncContext());
    }

    @Test
    public void getContentLengthLong() {
        assertEquals(0, sut.getContentLengthLong());
    }

    @Test
    public void getServletContext() {
        assertNull(sut.getServletContext());
    }

    @Test
    public void login() {
        sut.login("FIRST_ARG", "SECOND_ARG");
    }

    @Test
    public void logout() {
        sut.logout();
    }
}