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
package com.adobe.cq.email.core.components.internal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * POJO that contains details of a tokenized CSS style selector
 */
public class StyleToken {
    private String selector;
    private final List<String> jsoupSelectors = new ArrayList<>();
    private final List<String> properties = new ArrayList<>();
    private final List<StyleToken> childTokens = new ArrayList<>();
    private StyleSpecificity specificity;
    private boolean mediaQuery;
    private boolean pseudoSelector;
    private boolean nested;
    private boolean forceUsage = false;

    /**
     * Getter for the actual CSS selector
     *
     * @return the actual CSS selector
     */
    public String getSelector() {
        return selector;
    }

    /**
     * Setter for the actual CSS selector
     *
     * @param selector the actual CSS selector
     */
    public void setSelector(String selector) {
        this.selector = selector;
    }

    /**
     * Getter for the list of CSS selectors (split if more than one)
     *
     * @return the list of CSS selectors (split if more than one)
     */
    public List<String> getJsoupSelectors() {
        return jsoupSelectors;
    }

    /**
     * Getter for child style tokens
     *
     * @return
     */
    public List<StyleToken> getChildTokens() {
        return childTokens;
    }

    /**
     * Getter for the CSS properties
     *
     * @return the CSS properties
     */
    public List<String> getProperties() {
        return properties;
    }

    /**
     * Getter for the {@link StyleSpecificity}
     *
     * @return the {@link StyleSpecificity}
     */
    public StyleSpecificity getSpecificity() {
        return specificity;
    }

    /**
     * Setter for the {@link StyleSpecificity}
     *
     * @param specificity the {@link StyleSpecificity}
     */
    public void setSpecificity(StyleSpecificity specificity) {
        this.specificity = specificity;
    }

    /**
     * Getter for media query attribute
     *
     * @return true if the current token is a media query, false otherwise
     */
    public boolean isMediaQuery() {
        return mediaQuery;
    }

    /**
     * Setter for media query attribute
     *
     * @param mediaQuery true if the current token is a media query, false otherwise
     */
    public void setMediaQuery(boolean mediaQuery) {
        this.mediaQuery = mediaQuery;
    }

    /**
     * Getter for pseudo selector attribute
     *
     * @return true if the current token is a pseudo selector, false otherwise
     */
    public boolean isPseudoSelector() {
        return pseudoSelector;
    }

    /**
     * Setter for pseudo selector attribute
     *
     * @param pseudoSelector true if the current token is a pseudo selector, false otherwise
     */
    public void setPseudoSelector(boolean pseudoSelector) {
        this.pseudoSelector = pseudoSelector;
    }

    /**
     * Getter for nested attribute
     *
     * @return true if the current token is a nested selector, false otherwise
     */
    public boolean isNested() {
        return nested;
    }

    /**
     * Setter for nested attribute
     *
     * @param nested true if the current token is a nested selector, false otherwise
     */
    public void setNested(boolean nested) {
        this.nested = nested;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StyleToken that = (StyleToken) o;
        return mediaQuery == that.mediaQuery && pseudoSelector == that.pseudoSelector && nested == that.nested &&
                Objects.equals(selector, that.selector) && Objects.equals(jsoupSelectors, that.jsoupSelectors) &&
                Objects.equals(properties, that.properties) && Objects.equals(specificity, that.specificity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selector, jsoupSelectors, properties, specificity, mediaQuery, pseudoSelector, nested);
    }

    public void setForceUsage(boolean forceUsage) {
        this.forceUsage = forceUsage;
    }

    public boolean isForceUsage() {
        return this.forceUsage;
    }
}
