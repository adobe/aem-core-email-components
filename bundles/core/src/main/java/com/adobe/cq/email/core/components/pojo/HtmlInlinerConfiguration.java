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

import java.io.Serializable;
import java.io.StringReader;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlInlinerConfiguration implements Serializable {
    public static final String IMG_WIDTH_DEFAULT =
            "{\"elementType\":\"img\",\"cssPropertyRegEx\":\"width\",\"cssPropertyOutputRegEx\":\"[0-9]+(?=px)|[0-9]+(?=PX)" +
                    "|[0-9]+[%]\",\"htmlAttributeName\":\"width\",\"overrideIfAlreadyExisting\":true}";
    private static final Logger LOG = LoggerFactory.getLogger(HtmlInlinerConfiguration.class.getName());
    public static final String ELEMENT_TYPE = "elementType";
    public static final String CSS_PROPERTY_REG_EX = "cssPropertyRegEx";
    public static final String CSS_PROPERTY_OUTPUT_REG_EX = "cssPropertyOutputRegEx";
    public static final String HTML_ATTRIBUTE_NAME = "htmlAttributeName";
    public static final String OVERRIDE_IF_ALREADY_EXISTING = "overrideIfAlreadyExisting";

    private String elementType;
    private String cssPropertyRegEx;
    private String cssPropertyOutputRegEx;
    private String htmlAttributeName;
    private boolean overrideIfAlreadyExisting;

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getCssPropertyRegEx() {
        return cssPropertyRegEx;
    }

    public void setCssPropertyRegEx(String cssPropertyRegEx) {
        this.cssPropertyRegEx = cssPropertyRegEx;
    }

    public String getHtmlAttributeName() {
        return htmlAttributeName;
    }

    public String getCssPropertyOutputRegEx() {
        return cssPropertyOutputRegEx;
    }

    public void setCssPropertyOutputRegEx(String cssPropertyOutputRegEx) {
        this.cssPropertyOutputRegEx = cssPropertyOutputRegEx;
    }

    public void setHtmlAttributeName(String htmlAttributeName) {
        this.htmlAttributeName = htmlAttributeName;
    }

    public boolean isOverrideIfAlreadyExisting() {
        return overrideIfAlreadyExisting;
    }

    public void setOverrideIfAlreadyExisting(boolean overrideIfAlreadyExisting) {
        this.overrideIfAlreadyExisting = overrideIfAlreadyExisting;
    }

    public boolean isValid() {
        return StringUtils.isNotEmpty(elementType) && StringUtils.isNotEmpty(cssPropertyRegEx) &&
                StringUtils.isNotEmpty(cssPropertyOutputRegEx) && StringUtils.isNotEmpty(htmlAttributeName);
    }

    public static HtmlInlinerConfiguration parse(String json) {
        try {
            JsonReader jsonReader = Json.createReader(new StringReader(json));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();
            HtmlInlinerConfiguration htmlInlinerConfiguration = new HtmlInlinerConfiguration();
            htmlInlinerConfiguration.setElementType(jsonObject.getString(ELEMENT_TYPE));
            htmlInlinerConfiguration.setCssPropertyRegEx(jsonObject.getString(CSS_PROPERTY_REG_EX));
            htmlInlinerConfiguration.setCssPropertyOutputRegEx(jsonObject.getString(CSS_PROPERTY_OUTPUT_REG_EX));
            htmlInlinerConfiguration.setHtmlAttributeName(jsonObject.getString(HTML_ATTRIBUTE_NAME));
            htmlInlinerConfiguration.setOverrideIfAlreadyExisting(jsonObject.getBoolean(OVERRIDE_IF_ALREADY_EXISTING));
            return htmlInlinerConfiguration;
        } catch (Throwable e) {
            LOG.warn("Error processing JSON " + json + ": " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HtmlInlinerConfiguration that = (HtmlInlinerConfiguration) o;
        return overrideIfAlreadyExisting == that.overrideIfAlreadyExisting && Objects.equals(elementType, that.elementType) &&
                Objects.equals(cssPropertyRegEx, that.cssPropertyRegEx) &&
                Objects.equals(cssPropertyOutputRegEx, that.cssPropertyOutputRegEx) &&
                Objects.equals(htmlAttributeName, that.htmlAttributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementType, cssPropertyRegEx, cssPropertyOutputRegEx, htmlAttributeName, overrideIfAlreadyExisting);
    }

}
