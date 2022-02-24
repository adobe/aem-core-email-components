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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.components.constants.StylesInlinerConstants;
import com.adobe.cq.email.core.components.enumerations.HtmlSanitizingMode;
import com.adobe.cq.email.core.components.enumerations.StyleMergerMode;
import com.adobe.cq.email.core.components.exceptions.StylesInlinerException;
import com.adobe.cq.email.core.components.pojo.StyleSpecificity;
import com.adobe.cq.email.core.components.pojo.StyleToken;
import com.adobe.cq.email.core.components.services.StylesInlinerService;
import com.adobe.cq.email.core.components.util.HtmlSanitizer;
import com.adobe.cq.email.core.components.util.StyleExtractor;
import com.adobe.cq.email.core.components.util.StyleMerger;
import com.adobe.cq.email.core.components.util.StyleSpecificityFactory;
import com.adobe.cq.email.core.components.util.StyleTokenFactory;
import com.adobe.cq.email.core.components.util.StyleTokenizer;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;

import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.COMMENTS_REGEX;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.HEAD_TAG;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.NEW_LINE;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.STYLE_ATTRIBUTE;
import static com.adobe.cq.email.core.components.constants.StylesInlinerConstants.STYLE_TAG;

/**
 * This is a service which converts html with css classes into html with inline styles.
 */
@Component(service = StylesInlinerService.class)
@ServiceDescription("Styles Inliner Service")
public class StylesInlinerServiceImpl implements StylesInlinerService {

    @Reference
    private RequestResponseFactory requestResponseFactory;

    @Reference
    private SlingRequestProcessor requestProcessor;

    private static final Logger LOG = LoggerFactory.getLogger(StylesInlinerServiceImpl.class.getName());

    private static final StyleSpecificity STYLE_SPECIFICITY = new StyleSpecificity(1, 0, 0, 0);

    @Override
    public String getHtmlWithInlineStyles(ResourceResolver resourceResolver, String html, StyleMergerMode styleMergerMode,
                                          HtmlSanitizingMode htmlSanitizingMode) {
        try {
            Document doc = Jsoup.parse(html);
            doc.outputSettings().prettyPrint(false);
            List<String> styles = StyleExtractor.extract(doc, requestResponseFactory, requestProcessor, resourceResolver);
            List<StyleToken> styleTokens = new ArrayList<>();
            List<StyleToken> unInlinableStyleTokens = new ArrayList<>();
            StringBuilder styleSb = new StringBuilder();
            for (String allRules : styles) {
                String rules = allRules
                        .replaceAll(NEW_LINE, "") // remove newlines
                        .replaceAll(COMMENTS_REGEX, "") // remove comments
                        .trim();
                for (StyleToken styleToken : StyleTokenizer.tokenize(rules)) {
                    populateStylesToBeApplied(styleToken, doc, styleTokens, unInlinableStyleTokens);
                }
            }
            HtmlSanitizer.sanitizeDocument(htmlSanitizingMode, doc);
            applyStyles(doc, styleTokens, styleMergerMode);
            writeStyleTag(doc, styleSb, unInlinableStyleTokens);
            return doc.outerHtml();
        } catch (Throwable e) {
            throw new StylesInlinerException("An error occured during execution: " + e.getMessage(), e);
        }
    }

    /**
     * This method populates the styles to be applied for each element based on the style rules
     *
     * @param styleToken             the style token
     * @param doc                    the jsoup document which holds the html
     * @param styleTokens            the style tokens to be applied
     * @param unInlinableStyleTokens the un-inlinable style tokens
     */
    private void populateStylesToBeApplied(StyleToken styleToken, Document doc,
                                           List<StyleToken> styleTokens,
                                           List<StyleToken> unInlinableStyleTokens) {
        if (styleToken.isMediaQuery() || styleToken.isPseudoSelector()) {
            unInlinableStyleTokens.add(styleToken);
            return;
        }
        List<String> cssSelectors = styleToken.getSplittedSelectors();
        for (String cssSelector : cssSelectors) {
            try {
                Elements selectedElements = doc.select(cssSelector);
                if (selectedElements.isEmpty()) {
                    unInlinableStyleTokens.add(create(cssSelector, styleToken));
                    continue;
                }
                boolean updated = false;
                for (StyleToken alreadyAdded : styleTokens) {
                    if (alreadyAdded.getSelector().equals(cssSelector)) {
                        StyleTokenFactory.addProperties(alreadyAdded, StyleTokenFactory.getAllProperties(styleToken));
                        updated = true;
                    }
                }
                if (!updated) {
                    styleTokens.add(create(cssSelector, styleToken));
                }
            } catch (IllegalArgumentException | Selector.SelectorParseException e) {
                LOG.error(String.format("An error occurred while processing style tokens: %s", e.getMessage()), e);
            }
        }

    }

    @NotNull
    private StyleToken create(String cssSelector, StyleToken parent) {
        StyleToken styleToken = StyleTokenFactory.create(cssSelector);
        StyleTokenFactory.addProperties(styleToken, StyleTokenFactory.getAllProperties(parent));
        styleToken.setSpecificity(StyleSpecificityFactory.getSpecificity(cssSelector));
        styleToken.setMediaQuery(parent.isMediaQuery());
        styleToken.setPseudoSelector(parent.isPseudoSelector());
        styleToken.setNested(parent.isNested());
        return styleToken;
    }

    private void applyStyles(Document document, List<StyleToken> styleTokens, StyleMergerMode styleMergerMode) {
        for (StyleToken styleToken : styleTokens) {
            String elementSelector = styleToken.getSelector();
            for (Element elementToApply : document.select(elementSelector)) {
                if (null == elementToApply) {
                    LOG.warn("Failed to find {}", elementSelector);
                    continue;
                }
                String currentElementStyle = elementToApply.attr(STYLE_ATTRIBUTE);
                StyleToken currentElement = StyleTokenFactory.create(elementSelector);
                currentElement.setSpecificity(STYLE_SPECIFICITY);
                StyleTokenFactory.addProperties(currentElement, currentElementStyle);
                String style = StyleMerger.merge(currentElement, styleToken, styleMergerMode);
                if (StringUtils.isNotEmpty(style)) {
                    elementToApply.attr(StylesInlinerConstants.STYLE_ATTRIBUTE, style);
                }
            }
        }
    }

    private void writeStyleTag(Document doc, StringBuilder styleSb,
                               List<StyleToken> unusedStyleTokens) {
        if (Objects.isNull(unusedStyleTokens) || unusedStyleTokens.isEmpty()) {
            return;
        }
        Element style = new Element(STYLE_TAG);
        style.attr("type", "text/css");
        doc.select(HEAD_TAG).get(0).appendChild(style);
        for (StyleToken styleToken : unusedStyleTokens) {
            styleSb.append(StyleTokenFactory.toCss(styleToken)).append("\n\t\t");
        }
        style.html("\n\t\t" + styleSb);
    }

    void setRequestResponseFactory(RequestResponseFactory requestResponseFactory) {
        this.requestResponseFactory = requestResponseFactory;
    }

    void setRequestProcessor(SlingRequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }
}
