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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.rewriter.DefaultTransformer;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.TransformerFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.adobe.cq.email.core.components.internal.models.EmailPageImpl;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;

/**
 * This TransformerFactory adds a transformer globally that handles links (a:href, img:src, etc.) that contain Adobe Campaign expressions.
 * It does that in the following steps:
 * <ul>
 * <li>decode Adobe Campaign expressions in the url using different patterns (html entities, percentage encoding, ...)
 * <li>when the request has the selector campaign.content, externalize the url if it is relative
 * <li>encode the Adobe Campaign expressions using html entities
 * <li>when the url is the href of an a tag and it contains Adobe Campaign expressions, set the x-cq-linkchecker to skip
 * </ul>
 */
@Component(
    service = TransformerFactory.class,
    property = {
        "pipeline.type=" + EmailLinkTransformerFactory.TYPE,
    }
)
public class EmailLinkTransformerFactory implements TransformerFactory {

    public static final String TYPE = "email-link-rewrite";
    private static final Logger LOG = LoggerFactory.getLogger(EmailLinkTransformerFactory.class);
    private static final Pattern SCRIPTLET_PATTERN = Pattern.compile("<%[=@].*?%>");
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
        TAGS.put("img", Arrays.asList("src", "srcset"));
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
    private PageManagerFactory pageManagerFactory;

    @Override
    public TransformerImpl createTransformer() {
        return new TransformerImpl();
    }

    class TransformerImpl extends DefaultTransformer {

        private SlingHttpServletRequest request;
        private ResourceResolver resourceResolver;
        private Optional<LinkManager> linkManager;
        boolean enabled;
        boolean externalizationEnabled;

        @Override
        public void init(ProcessingContext context, ProcessingComponentConfiguration config) throws IOException {
            super.init(context, config);
            request = context.getRequest();
            resourceResolver = request.getResourceResolver();
            PageManager pageManager = pageManagerFactory.getPageManager(resourceResolver);
            Page resourcePage = pageManager.getContainingPage(request.getResource());
            Resource resourcePageContent = resourcePage != null ? resourcePage.getContentResource() : null;
            enabled = resourcePageContent != null && resourcePageContent.isResourceType(EmailPageImpl.RESOURCE_TYPE);
            externalizationEnabled = StringUtils.equals(request.getRequestPathInfo().getSelectorString(), "campaign.content");

            if (externalizationEnabled && LOG.isDebugEnabled()) {
                LOG.debug("email-link-transformer enabled for request {}", request.getRequestURI());
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (!enabled) {
                super.startElement(uri, localName, qName, attributes);
                return;
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

                    if (StringUtils.isEmpty(value)) {
                        continue;
                    }

                    String decodedValue = unescapeScriptlets(value);
                    boolean containsScriptlets = SCRIPTLET_PATTERN.matcher(decodedValue).find();

                    if (mutableAttributes == null) {
                        mutableAttributes = new AttributesImpl(attributes);
                    }

                    if (StringUtils.equals(attr, "srcset")) {
                        // special handling for srcset
                        value = Arrays.stream(StringUtils.split(decodedValue, ','))
                            .filter(StringUtils::isNotEmpty)
                            .map(StringUtils::trim)
                            .map(entry -> StringUtils.split(entry, ' '))
                            .map(parts -> {
                                parts[0] = rewriteLink(parts[0]);
                                return StringUtils.join(parts, ' ');
                            })
                            .collect(Collectors.joining(", "));
                    } else {
                        value = rewriteLink(decodedValue);
                    }

                    value = escapeScriptlets(value);
                    setAttribute(mutableAttributes, index, value);

                    // set x-cq-linkchecker skip for all <a> tags that contain Adobe Campaign scriptlets
                    // otherwise the CQ LinkChecker will render them as invalid
                    if (tag.equals("a") && containsScriptlets) {
                        setAttribute(mutableAttributes, LINK_CHECKER_ATTR, LINK_CHECKER_ATTR_SKIP);
                    }
                }
            }

            super.startElement(uri, localName, qName, mutableAttributes != null ? mutableAttributes : attributes);
        }

        private void setAttribute(AttributesImpl attributes, String name, String value) {
            int index = attributes.getIndex(name);

            if (index >= 0) {
                setAttribute(attributes, index, value);
            } else {
                attributes.addAttribute("", name, name, "CDATA", value);
            }
        }

        private void setAttribute(AttributesImpl attributes, int index, String value) {
            String attrUri = attributes.getURI(index);
            String attrLocalName = attributes.getLocalName(index);
            String attrQName = attributes.getQName(index);
            String attrType = attributes.getType(index);

            attributes.setAttribute(index, attrUri, attrLocalName, attrQName, attrType, value);
        }

        private String rewriteLink(String originalUrl) {
            if (this.linkManager == null) {
                this.linkManager = Optional.ofNullable(request.adaptTo(LinkManager.class));
            }
            if (!this.linkManager.isPresent()) {
                return originalUrl;
            }

            String url = originalUrl;

            if (externalizationEnabled && url.charAt(0) == '/') {
                LinkManager linkManager = this.linkManager.get();
                Link<?> link = linkManager.get(url).build();

                url = link.getMappedURL();

                if (url == null || url.charAt(0) == '/') {
                    // if the ResourceResolver#map() did not return an absolute url we use the externalizer instead
                    url = link.getExternalizedURL();
                }
            }

            return url != null ? url : originalUrl;
        }

        private String unescapeScriptlets(String url) {
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
