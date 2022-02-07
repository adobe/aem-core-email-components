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

    public int getStyleAttribute() {
        return styleAttribute;
    }

    public void setStyleAttribute(int styleAttribute) {
        this.styleAttribute = styleAttribute;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClassAttribute() {
        return classAttribute;
    }

    public void setClassAttribute(int classAttribute) {
        this.classAttribute = classAttribute;
    }

    public int getElements() {
        return elements;
    }

    public void setElements(int elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof StyleSpecificity)) {
            return false;
        }
        StyleSpecificity other = (StyleSpecificity) obj;
        return other.getStyleAttribute() == getStyleAttribute() && other.getId() == getId() &&
                other.getClassAttribute() == getClassAttribute() && other.getElements() == getElements();
    }

    @Override
    public int hashCode() {
        String specificity =
                String.valueOf(styleAttribute) + String.valueOf(id) + String.valueOf(classAttribute) + String.valueOf(elements);
        return Integer.parseInt(specificity);
    }

    @Override
    public int compareTo(StyleSpecificity o) {
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
