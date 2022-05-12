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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * {@link HttpServletRequest} implementation used for absolute URL retrieval
 */
public class ResolverRequestWrapper extends HttpServletRequestWrapper {

    private final URI uri;

    /**
     *
     * C'tor.
     *
     * @param request
     *            original request (with wrong host settings)
     * @param uriString
     *            uri string that should be resolved
     * @throws URIException
     *             if the uri object cannot be created
     */
    public ResolverRequestWrapper(HttpServletRequest request, String uriString) throws URIException {
        super(request);
        uri = new URI(uriString, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getScheme() {
        return uri.getScheme();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerName() {
        try {
            return uri.getHost();
        } catch (URIException ex) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServerPort() {
        return uri.getPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPathInfo() {
        try {
            return uri.getPath();
        } catch (URIException ex) {
            return "";
        }
    }
}
