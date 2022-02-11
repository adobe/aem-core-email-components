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
package com.adobe.cq.email.core.components.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StyleToken {
    private String selector;
    private final List<String> splittedSelectors = new ArrayList<>();
    private final List<String> properties = new ArrayList<>();
    private StyleSpecificity specificity;
    private boolean mediaQuery;
    private boolean pseudoSelector;
    private boolean nested;

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public List<String> getSplittedSelectors() {
        return splittedSelectors;
    }

    public List<String> getProperties() {
        return properties;
    }

    public StyleSpecificity getSpecificity() {
        return specificity;
    }

    public void setSpecificity(StyleSpecificity specificity) {
        this.specificity = specificity;
    }

    public boolean isMediaQuery() {
        return mediaQuery;
    }

    public void setMediaQuery(boolean mediaQuery) {
        this.mediaQuery = mediaQuery;
    }

    public boolean isPseudoSelector() {
        return pseudoSelector;
    }

    public void setPseudoSelector(boolean pseudoSelector) {
        this.pseudoSelector = pseudoSelector;
    }

    public boolean isNested() {
        return nested;
    }

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
                Objects.equals(selector, that.selector) && Objects.equals(splittedSelectors, that.splittedSelectors) &&
                Objects.equals(properties, that.properties) && Objects.equals(specificity, that.specificity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selector, splittedSelectors, properties, specificity, mediaQuery, pseudoSelector, nested);
    }
}
