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
 * POJO that identifies the style specificity of a CSS rule
 */
public class StyleSpecificity implements Comparable<StyleSpecificity> {
    private int styleAttribute;
    private int id;
    private int classAttribute;
    private int elements;

    public StyleSpecificity(int styleAttribute, int id, int classAttribute, int elements) {
        this.styleAttribute = styleAttribute;
        this.id = id;
        this.classAttribute = classAttribute;
        this.elements = elements;
    }

    /**
     * Getter for the style attribute
     *
     * @return the style attribute
     */
    public int getStyleAttribute() {
        return styleAttribute;
    }

    /**
     * Setter for the style attribute
     *
     * @param styleAttribute the style attribute
     */
    public void setStyleAttribute(int styleAttribute) {
        this.styleAttribute = styleAttribute;
    }

    /**
     * Getter for the id
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for the id
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the class attribute
     *
     * @return the class attribute
     */
    public int getClassAttribute() {
        return classAttribute;
    }

    /**
     * Setter for the class attribute
     *
     * @param classAttribute the class attribute
     */
    public void setClassAttribute(int classAttribute) {
        this.classAttribute = classAttribute;
    }

    /**
     * Getter for the element count
     *
     * @return the element count
     */
    public int getElements() {
        return elements;
    }

    /**
     * Setter for the element count
     *
     * @param elements the element count
     */
    public void setElements(int elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StyleSpecificity that = (StyleSpecificity) o;
        return styleAttribute == that.styleAttribute && id == that.id && classAttribute == that.classAttribute && elements == that.elements;
    }

    @Override
    public int hashCode() {
        return Objects.hash(styleAttribute, id, classAttribute, elements);
    }

    @Override
    public int compareTo(StyleSpecificity o) {
        if (Objects.isNull(o)) {
            return 1;
        }
        if (o.equals(this)) {
            return 0;
        }
        int comp = Integer.compare(getStyleAttribute(), o.getStyleAttribute());
        if (comp != 0) {
            return comp;
        }
        comp = Integer.compare(getId(), o.getId());
        if (comp != 0) {
            return comp;
        }
        comp = Integer.compare(getClassAttribute(), o.getClassAttribute());
        if (comp != 0) {
            return comp;
        }
        return Integer.compare(getElements(), o.getElements());
    }
}
