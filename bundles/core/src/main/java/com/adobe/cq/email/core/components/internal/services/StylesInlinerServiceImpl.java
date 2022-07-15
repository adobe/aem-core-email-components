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

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.components.configs.StylesInlinerConfig;
import com.adobe.cq.email.core.components.constants.StylesInlinerConstants;
import com.adobe.cq.email.core.components.exceptions.StylesInlinerException;
import com.adobe.cq.email.core.components.pojo.HtmlInlinerConfiguration;
import com.adobe.cq.email.core.components.pojo.StyleSpecificity;
import com.adobe.cq.email.core.components.pojo.StyleToken;
import com.adobe.cq.email.core.components.services.StylesInlinerService;
import com.adobe.cq.email.core.components.util.HtmlAttributeInliner;
import com.adobe.cq.email.core.components.util.HtmlSanitizer;
import com.adobe.cq.email.core.components.util.StyleExtractor;
import com.adobe.cq.email.core.components.util.StyleMerger;
import com.adobe.cq.email.core.components.util.StyleSpecificityFactory;
import com.adobe.cq.email.core.components.util.StyleTokenFactory;
import com.adobe.cq.email.core.components.util.StyleTokenizer;
import com.adobe.cq.email.core.components.util.WrapperDivRemover;
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
@Designate(ocd = StylesInlinerConfig.class)
public class StylesInlinerServiceImpl implements StylesInlinerService {
    private static final Logger LOG = LoggerFactory.getLogger(StylesInlinerServiceImpl.class.getName());
    private static final StyleSpecificity STYLE_SPECIFICITY = new StyleSpecificity(1, 0, 0, 0);
    @Reference
    private RequestResponseFactory requestResponseFactory;

    @Reference
    private SlingRequestProcessor requestProcessor;

    private StylesInlinerConfig stylesInlinerConfig;

    /**
     * Activate method
     *
     * @param stylesInlinerConfig the {@link StylesInlinerConfig}
     */
    @Activate
    public void activate(final StylesInlinerConfig stylesInlinerConfig) {
        this.stylesInlinerConfig = Optional.ofNullable(stylesInlinerConfig).orElse(defaultStylesInlinerConfig());

    }

    @Override
    public String getHtmlWithInlineStylesJson(ResourceResolver resourceResolver, String content, String charset) {
        JsonObject jsonObject = parse(content, charset);
        if (Objects.isNull(jsonObject)) {
            return getHtmlWithInlineStyles(resourceResolver, content);
        }
        try {
            String html = jsonObject.getString("html");
            String htmlWithInlineStyles = getHtmlWithInlineStyles(resourceResolver, html);
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            jsonObject.forEach(jsonObjectBuilder::add);
            jsonObjectBuilder.add("html", htmlWithInlineStyles);
            return jsonObjectBuilder.build().toString();
        } catch (Throwable e) {
            throw new StylesInlinerException("An error occurred during execution: " + e.getMessage(), e);
        }
    }

    @Override
    public String getHtmlWithInlineStyles(ResourceResolver resourceResolver, String html) {
        try {
            Document doc = Jsoup.parse(html);
            doc.outputSettings().prettyPrint(false);
            List<String> styles = StyleExtractor.extract(doc, requestResponseFactory, requestProcessor, resourceResolver);
            List<StyleToken> styleTokens = new ArrayList<>();
            List<StyleToken> unInlinableStyleTokens = new ArrayList<>();
            StringBuilder styleSb = new StringBuilder();
            for (String allRules : styles) {
                String rules = allRules.replaceAll(NEW_LINE, "") // remove newlines
                        .replaceAll(COMMENTS_REGEX, "") // remove comments
                        .trim();
                for (StyleToken styleToken : StyleTokenizer.tokenize(rules)) {
                    populateStylesToBeApplied(styleToken, doc, styleTokens, unInlinableStyleTokens);
                }
            }
            String stylePlaceholder = "!!!STYLE_PLACEHOLDER_" + new Date().getTime() + "!!!";
            HtmlSanitizer.sanitizeDocument(doc);
            applyStyles(doc, styleTokens, stylesInlinerConfig.htmlInlinerConfiguration());
            processStyle(doc, styleSb, stylePlaceholder, unInlinableStyleTokens);
            WrapperDivRemover.removeWrapperDivs(doc, stylesInlinerConfig.wrapperDivClassesToBeRemoved());
            String outerHtml = doc.outerHtml();
            if (StringUtils.isEmpty(outerHtml)) {
                return outerHtml;
            }
            outerHtml = outerHtml.replace(stylePlaceholder, styleSb.toString());
            return outerHtml.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "");
        } catch (Throwable e) {
            throw new StylesInlinerException("An error occurred during execution: " + e.getMessage(), e);
        }
    }

    private JsonObject parse(String content, String charset) {
        try {
            JsonReader reader = Json.createReader(new ByteArrayInputStream(content.getBytes(charset)));
            return reader.readObject();
        } catch (Throwable e) {
            return null;
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
    private void populateStylesToBeApplied(StyleToken styleToken, Document doc, List<StyleToken> styleTokens,
                                           List<StyleToken> unInlinableStyleTokens) {
        if (styleToken.isMediaQuery() || styleToken.isPseudoSelector()) {
            unInlinableStyleTokens.add(styleToken);
            return;
        }
        List<String> cssSelectors = styleToken.getSplitSelectors();
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

    private void applyStyles(Document document, List<StyleToken> styleTokens, String[] htmlInlinerConfigurations) {
        List<HtmlInlinerConfiguration> htmlInlinerConfigurationList = new ArrayList<>();
        for (String htmlInlinerConfiguration : htmlInlinerConfigurations) {
            HtmlInlinerConfiguration parsed = HtmlInlinerConfiguration.parse(htmlInlinerConfiguration);
            if (Objects.nonNull(parsed)) {
                htmlInlinerConfigurationList.add(parsed);
            }
        }
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
                StyleToken mergedStyleToken = StyleMerger.merge(currentElement, styleToken);
                String style = StyleTokenFactory.getInlinablePropertiesIgnoringNesting(mergedStyleToken);
                if (StringUtils.isNotEmpty(style)) {
                    String tagName = elementToApply.tagName();
                    elementToApply.attr(StylesInlinerConstants.STYLE_ATTRIBUTE, style);
                    HtmlAttributeInliner.process(elementToApply, mergedStyleToken,
                            htmlInlinerConfigurationList.stream().filter(c -> tagName.equals(c.getElementType()))
                                    .collect(Collectors.toList()));
                }
            }
        }
    }

    private void processStyle(Document doc, StringBuilder styleSb, String stylePlaceholder, List<StyleToken> unusedStyleTokens) {
        if (Objects.isNull(unusedStyleTokens) || unusedStyleTokens.isEmpty()) {
            return;
        }
        Element style = new Element(STYLE_TAG);
        style.attr("type", "text/css");
        doc.select(HEAD_TAG).get(0).appendChild(style);
        for (StyleToken styleToken : unusedStyleTokens) {
            styleSb.append(StyleTokenFactory.toCss(styleToken)).append("\n\t\t");
        }
        style.text(stylePlaceholder);
    }

    void setRequestResponseFactory(RequestResponseFactory requestResponseFactory) {
        this.requestResponseFactory = requestResponseFactory;
    }

    void setRequestProcessor(SlingRequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    private StylesInlinerConfig defaultStylesInlinerConfig() {
        return new StylesInlinerConfig() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return StylesInlinerConfig.class;
            }

            @Override
            public String[] wrapperDivClassesToBeRemoved() {
                return new String[]{"aem-Grid", "aem-GridColumn"};
            }

            @Override
            public String[] htmlInlinerConfiguration() {
                return new String[]{HtmlInlinerConfiguration.IMG_WIDTH_DEFAULT};
            }

        };
    }
}
