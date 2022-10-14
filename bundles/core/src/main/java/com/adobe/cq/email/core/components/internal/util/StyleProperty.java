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

import java.util.Objects;

/**
 * POJO that contains the details of o CSS style property
 */
public class StyleProperty {
    private String name;
    private String value;
    private boolean important;
    private String fullProperty;
    private StyleSpecificity specificity;

    /**
     * Getter for the property name
     *
     * @return the property name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the property name
     *
     * @param name the property name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the property value
     *
     * @return the property value
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter for the property value
     *
     * @param value the property value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Getter for the important attribute value
     *
     * @return the important attribute value
     */
    public boolean isImportant() {
        return important;
    }

    /**
     * Setter for the important attribute value
     *
     * @param important the important attribute value
     */
    public void setImportant(boolean important) {
        this.important = important;
    }

    /**
     * Getter for the full CSS property
     *
     * @return the full CSS property
     */
    public String getFullProperty() {
        return fullProperty;
    }

    /**
     * Setter for the full CSS property
     *
     * @param fullProperty the full CSS property
     */
    public void setFullProperty(String fullProperty) {
        this.fullProperty = fullProperty;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StyleProperty that = (StyleProperty) o;
        return important == that.important && Objects.equals(name, that.name) && Objects.equals(value, that.value) &&
                Objects.equals(fullProperty, that.fullProperty) && Objects.equals(specificity, that.specificity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, important, fullProperty, specificity);
    }
}
