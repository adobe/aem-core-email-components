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
package com.adobe.cq.email.core.components.internal.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.rewriter.DefaultTransformer;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.TransformerFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This TransformerFactory adds a transformer globally that handles links (a:href, img:src, etc.) that contain Adobe Campaign expressions.
 * <p>
 * It will decode and re-encode all a:href, img:src, tb:background and th:background using html entities when they contain Adobe Campaign
 * expressions. If this is the case for links they will be additionally marked to be skipped by the CQ LinkChecker.
 * <p>
 * When used together with the MCM ContentServlet (campaign.content selector) the links also be externalized.
 */
@Component(
    service = TransformerFactory.class,
    property = {
        "pipeline.mode=global",
        "pipeline.type=email-link-transformer",
        Constants.SERVICE_RANKING + ":Integer=-1000"
    }
)
public class EmailLinkTransformerFactory implements TransformerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(EmailLinkTransformerFactory.class);
    private static final Map<String, List<String>> TAGS;
    private static final String OPENING = "<%";
    private static final String OPENING_ESCAPED = "&lt;%";
    private static final String CLOSING = "%>";
    private static final String CLOSING_ESCAPED = "%&gt;";
    static final String LINK_CHECKER_ATTR = "x-cq-linkchecker";
    static final String LINK_CHECKER_ATTR_SKIP = "skip";

    private static final Map<String, List<String>> ESCAPING;

    static {
        TAGS = new LinkedHashMap<>(4);
        TAGS.put("img", Collections.singletonList("src"));
        TAGS.put("a", Collections.singletonList("href"));
        TAGS.put("td", Collections.singletonList("background"));
        TAGS.put("th", Collections.singletonList("background"));

        // scriptlets may be escaped / encoded in various ways
        // - using html entities (e.g. by htl)
        // - using percentage (e.g. RTE)
        ESCAPING = new LinkedHashMap<>(2);
        ESCAPING.put(OPENING, Arrays.asList("%3C%25", "%3C%", OPENING_ESCAPED));
        ESCAPING.put(CLOSING, Arrays.asList("%25%3E", "%%3E", CLOSING_ESCAPED));
        ESCAPING.put(" ", Collections.singletonList("%20"));
    }

    @Reference
    UrlMapperServiceImpl urlMapperService;

    @Reference
    EmailPathProcessor emailPathProcessor;

    @Override
    public TransformerImpl createTransformer() {
        return new TransformerImpl();
    }

    class TransformerImpl extends DefaultTransformer {

        private SlingHttpServletRequest request;
        private ResourceResolver resourceResolver;
        boolean enabled;
        boolean externalizationEnabled;

        @Override
        public void init(ProcessingContext context, ProcessingComponentConfiguration config) throws IOException {
            super.init(context, config);
            request = context.getRequest();
            resourceResolver = request.getResourceResolver();
            enabled = emailPathProcessor.isEmailPageRequest(request);
            externalizationEnabled = StringUtils.equals(request.getRequestPathInfo().getSelectorString(), "campaign.content");

            if (externalizationEnabled && LOG.isDebugEnabled()) {
                LOG.debug("email-link-transformer enabled for request {}", request.getRequestURI());
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (!enabled) {
                super.startElement(uri, localName, qName, attributes);
            }

            AttributesImpl mutableAttributes = attributes instanceof AttributesImpl ? (AttributesImpl) attributes : null;

            for (Map.Entry<String, List<String>> tagEntry : TAGS.entrySet()) {
                String tag = tagEntry.getKey();

                if (!tag.equals(localName)) {
                    continue;
                }

                for (String attr : tagEntry.getValue()) {
                    int index = attributes.getIndex(attr);

                    if (index < 0) {
                        continue;
                    }

                    String value = attributes.getValue(index);
                    String decodedValue = unescapeScriptlets(value);

                    if (!shouldRewriteLink(decodedValue)) {
                        continue;
                    }

                    if (mutableAttributes == null) {
                        mutableAttributes = new AttributesImpl(attributes);
                    }

                    value = rewriteLink(decodedValue);
                    value = escapeScriptlets(value);
                    setAttribute(mutableAttributes, index, value);

                    // set x-cq-linkchecker skip for all <a> tags that contain Adobe Campaign scriptlets
                    if (tag.equals("a")) {
                        setLinkCheckerSkip(mutableAttributes);
                    }
                }
            }

            super.startElement(uri, localName, qName, mutableAttributes != null ? mutableAttributes : attributes);
        }

        private void setLinkCheckerSkip(AttributesImpl attributes) {
            int index = attributes.getIndex(LINK_CHECKER_ATTR);

            if (index >= 0) {
                setAttribute(attributes, index, LINK_CHECKER_ATTR_SKIP);
            } else {
                attributes.addAttribute("", LINK_CHECKER_ATTR, LINK_CHECKER_ATTR, "CDATA", LINK_CHECKER_ATTR_SKIP);
            }
        }

        private void setAttribute(AttributesImpl attributes, int index, String value) {
            String attrUri = attributes.getURI(index);
            String attrLocalName = attributes.getLocalName(index);
            String attrQName = attributes.getQName(index);
            String attrType = attributes.getType(index);

            attributes.setAttribute(index, attrUri, attrLocalName, attrQName, attrType, value);
        }

        private String rewriteLink(String originalLink) {
            String link = originalLink;
            Map<String, String> placeholders = new LinkedHashMap<>();

            link = EmailPathProcessor.mask(link, placeholders);

            String path = link;
            String fragQuery = "";
            int queryPos = link.indexOf("?");

            if (queryPos > 0) {
                path = link.substring(0, queryPos);
                fragQuery = link.substring(queryPos);
            } else {
                int fragPos = link.indexOf("#");
                if (fragPos > 0) {
                    path = link.substring(0, fragPos);
                    fragQuery = link.substring(fragPos);
                }
            }

            try {
                path = URLDecoder.decode(path, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                LOG.debug("Unsupported encoding: {}", ex.getMessage(), ex);
            }

            if (externalizationEnabled && path.charAt(0) == '/') {
                path = urlMapperService.getMappedUrl(resourceResolver, request, path);
            }

            link = path + fragQuery;
            link = EmailPathProcessor.unmask(link, placeholders);

            LOG.debug("Rewritten link from {} to {}", originalLink, link);

            return link;
        }

        private boolean shouldRewriteLink(String decodedLink) {
            if (StringUtils.isEmpty(decodedLink)) {
                return false;
            }

            return EmailPathProcessor.PATTERN.matcher(decodedLink).find();
        }

        private String unescapeScriptlets(String url) {
            if (StringUtils.isEmpty(url)) {
                return url;
            }

            String decodedHref = url;
            for (Map.Entry<String, List<String>> entry : ESCAPING.entrySet()) {
                String decoded = entry.getKey();
                for (String encoded : entry.getValue()) {
                    decodedHref = decodedHref.replaceAll(encoded, decoded);
                }
            }

            return decodedHref;
        }

        private String escapeScriptlets(String url) {
            return url
                .replaceAll(OPENING, OPENING_ESCAPED)
                .replaceAll(CLOSING, CLOSING_ESCAPED);
        }
    }
}
