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
package com.adobe.cq.email.core.components.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;

public class TeaserLink<T> implements Link<T> {
    private boolean valid;
    private String url;
    private String mappedUrl;
    private String externalizedUrl;
    private Map<String, String> htmlAttributes;
    private T reference;

    @Override
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public @Nullable String getURL() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public @Nullable String getMappedURL() {
        return mappedUrl;
    }

    public void setMappedUrl(String mappedUrl) {
        this.mappedUrl = mappedUrl;
    }

    @Override
    public @Nullable String getExternalizedURL() {
        return externalizedUrl;
    }

    public void setExternalizedUrl(String externalizedUrl) {
        this.externalizedUrl = externalizedUrl;
    }

    @Override
    public @NotNull Map<String, String> getHtmlAttributes() {
        return htmlAttributes;
    }

    public void setHtmlAttributes(Map<String, String> htmlAttributes) {
        this.htmlAttributes = htmlAttributes;
    }

    @Override
    public @Nullable T getReference() {
        return reference;
    }

    public void setReference(T reference) {
        this.reference = reference;
    }

    public static Link create(Link link, String url) {
        if (Objects.isNull(link)) {
            return null;
        }
        TeaserLink teaserLink = new TeaserLink<>();
        teaserLink.setValid(link.isValid());
        Map<String, String> htmlAttributes = new HashMap<>(link.getHtmlAttributes());
        if (StringUtils.isEmpty(url)) {
            teaserLink.setUrl(link.getURL());
            teaserLink.setMappedUrl(link.getMappedURL());
            teaserLink.setExternalizedUrl(link.getExternalizedURL());
        } else {
            teaserLink.setUrl(url);
            teaserLink.setMappedUrl(url);
            teaserLink.setExternalizedUrl(url);
            htmlAttributes.put("href", url);
        }
        teaserLink.setHtmlAttributes(htmlAttributes);
        teaserLink.setReference(link.getReference());
        return teaserLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TeaserLink<?> that = (TeaserLink<?>) o;
        return valid == that.valid && Objects.equals(url, that.url) && Objects.equals(mappedUrl, that.mappedUrl) &&
                Objects.equals(externalizedUrl, that.externalizedUrl) && Objects.equals(htmlAttributes, that.htmlAttributes) &&
                Objects.equals(reference, that.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, url, mappedUrl, externalizedUrl, htmlAttributes, reference);
    }
}
