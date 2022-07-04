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

/**
 * POJO that contains the behaviour of the HTML inliner utility class
 */
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

    /**
     * Getter for the target HTML element type
     *
     * @return the target HTML element type
     */
    public String getElementType() {
        return elementType;
    }

    /**
     * Setter for the target HTML element type
     *
     * @param elementType the target HTML element type
     */
    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    /**
     * Getter for the regular expression to match the specific CSS property
     *
     * @return the regular expression to match the specific CSS property
     */
    public String getCssPropertyRegEx() {
        return cssPropertyRegEx;
    }

    /**
     * Setter for the regular expression to match the specific CSS property
     *
     * @param cssPropertyRegEx the regular expression to match the specific CSS property
     */
    public void setCssPropertyRegEx(String cssPropertyRegEx) {
        this.cssPropertyRegEx = cssPropertyRegEx;
    }

    /**
     * Getter for the regular expression to extract the value to be used for htmlAttributeName
     *
     * @return the regular expression to extract the value to be used for htmlAttributeName
     */
    public String getCssPropertyOutputRegEx() {
        return cssPropertyOutputRegEx;
    }

    /**
     * Setter for the regular expression to extract the value to be used for htmlAttributeName
     *
     * @param cssPropertyOutputRegEx the regular expression to extract the value to be used for htmlAttributeName
     */
    public void setCssPropertyOutputRegEx(String cssPropertyOutputRegEx) {
        this.cssPropertyOutputRegEx = cssPropertyOutputRegEx;
    }

    /**
     * Getter for the target HTML attribute to be created
     *
     * @return the target HTML attribute to be created
     */
    public String getHtmlAttributeName() {
        return htmlAttributeName;
    }

    /**
     * Setter for the target HTML attribute to be created
     *
     * @param htmlAttributeName the target HTML attribute to be created
     */
    public void setHtmlAttributeName(String htmlAttributeName) {
        this.htmlAttributeName = htmlAttributeName;
    }

    /**
     * Getter for overrideIfAlreadyExisting property: returns true if the htmlAttributeName should be overridden if already existing, false
     * otherwise
     *
     * @return true if the htmlAttributeName should be overridden if already existing, false otherwise
     */
    public boolean isOverrideIfAlreadyExisting() {
        return overrideIfAlreadyExisting;
    }

    /**
     * Setter for overrideIfAlreadyExisting property: true if the htmlAttributeName should be overridden if already existing, false
     * otherwise
     *
     * @param overrideIfAlreadyExisting true if the htmlAttributeName should be overridden if already existing, false otherwise
     */
    public void setOverrideIfAlreadyExisting(boolean overrideIfAlreadyExisting) {
        this.overrideIfAlreadyExisting = overrideIfAlreadyExisting;
    }

    /**
     * Returns true if the current {@link HtmlInlinerConfiguration} is actually valid, false otherwise
     *
     * @return true if is valid, false otherwise
     */
    public boolean isValid() {
        return StringUtils.isNotEmpty(elementType) && StringUtils.isNotEmpty(cssPropertyRegEx) &&
                StringUtils.isNotEmpty(cssPropertyOutputRegEx) && StringUtils.isNotEmpty(htmlAttributeName);
    }

    /**
     * Parses a json object to a {@link HtmlInlinerConfiguration}
     *
     * @param json the json object
     * @return the {@link HtmlInlinerConfiguration} if json is in the right format, null otherwise
     */
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
