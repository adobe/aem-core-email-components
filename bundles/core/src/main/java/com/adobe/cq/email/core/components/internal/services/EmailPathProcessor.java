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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.email.core.components.internal.models.EmailPageImpl;
import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import com.day.cq.wcm.scripting.WCMBindingsConstants;

/**
 * This implementation of a {@link PathProcessor} applies a masking Adobe Campaign expressions contained in the path.
 */
@Component(
    service = { EmailPathProcessor.class, PathProcessor.class },
    property = Constants.SERVICE_RANKING + ":Integer=" + (Integer.MIN_VALUE + 1)
)
public class EmailPathProcessor implements PathProcessor {

    /**
     * Regex pattern matching Adobe Campaign expressions (spaces are not mandatory):
     * <ul>
     * <li><%= variable %>
     * <li><%@ directive %>
     * </ul>
     */
    final static Pattern PATTERN = Pattern.compile("(<%[=@].*?%>)");
    private final static List<String> ACCEPTED_PAGE_TYPES = Collections.singletonList(EmailPageImpl.RESOURCE_TYPE);

    @Reference(target = "(component.name=com.adobe.cq.wcm.core.components.internal.link.DefaultPathProcessor)")
    PathProcessor defaultPathProcessor;

    @Reference
    PageManagerFactory pageManagerFactory;

    public boolean isEmailPageRequest(SlingHttpServletRequest request) {
        SlingBindings bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
        Page currentPage = null;

        if (bindings != null) {
            currentPage = (Page) bindings.get(WCMBindingsConstants.NAME_CURRENT_PAGE);
        }

        if (currentPage == null) {
            PageManager pageManager = pageManagerFactory.getPageManager(request.getResourceResolver());
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource pageResource = resourceResolver.resolve(request, request.getRequestURI());
            currentPage = pageManager.getPage(pageResource.getPath());

            if (currentPage == null) {
                currentPage = pageManager.getContainingPage(request.getResource());
            }
        }

        return Optional.ofNullable(currentPage)
            .map(Page::getContentResource)
            .map(contentResource -> ACCEPTED_PAGE_TYPES.stream().anyMatch(contentResource::isResourceType))
            .orElse(Boolean.FALSE);
    }

    @Override
    public boolean accepts(@NotNull String path, @NotNull SlingHttpServletRequest slingHttpServletRequest) {
        return isEmailPageRequest(slingHttpServletRequest);
    }

    @Override
    @NotNull
    public String sanitize(@NotNull String path, @NotNull SlingHttpServletRequest slingHttpServletRequest) {
        Map<String, String> placeholders = new HashMap<>();
        String maskedPath = mask(path, placeholders);
        maskedPath = defaultPathProcessor.sanitize(maskedPath, slingHttpServletRequest);
        return unmask(maskedPath, placeholders);
    }

    @Override
    @NotNull
    public String map(@NotNull String path, @NotNull SlingHttpServletRequest slingHttpServletRequest) {
        Map<String, String> placeholders = new HashMap<>();
        String maskedPath = mask(path, placeholders);
        maskedPath = defaultPathProcessor.map(maskedPath, slingHttpServletRequest);
        return unmask(maskedPath, placeholders);
    }

    @Override
    @NotNull
    public String externalize(@NotNull String path, @NotNull SlingHttpServletRequest slingHttpServletRequest) {
        Map<String, String> placeholders = new HashMap<>();
        String maskedPath = mask(path, placeholders);
        maskedPath = defaultPathProcessor.externalize(maskedPath, slingHttpServletRequest);
        return unmask(maskedPath, placeholders);
    }

    @Nullable
    @Override
    public Map<String, String> processHtmlAttributes(@NotNull String path, @Nullable Map<String, String> htmlAttributes) {
        return defaultPathProcessor.processHtmlAttributes(path, htmlAttributes);
    }

    static String mask(String original, Map<String, String> placeholders) {
        Matcher matcher = PATTERN.matcher(original);
        String masked = original;
        while (matcher.find()) {
            String expression = matcher.group(1);
            String placeholder = newPlaceholder(masked);
            masked = masked.replaceFirst(Pattern.quote(expression), placeholder);
            placeholders.put(placeholder, expression);
        }
        return masked;
    }

    static String unmask(String masked, Map<String, String> placeholders) {
        String unmasked = masked;
        for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            unmasked = unmasked.replaceFirst(placeholder.getKey(), placeholder.getValue());
        }
        return unmasked;
    }

    private static String newPlaceholder(String str) {
        SecureRandom random = new SecureRandom();
        StringBuilder placeholderBuilder = new StringBuilder(5);

        do {
            placeholderBuilder.setLength(0);
            placeholderBuilder
                .append("_")
                .append(new BigInteger(16, random).toString(16))
                .append("_");
        } while (str.contains(placeholderBuilder));

        return placeholderBuilder.toString();
    }
}
