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

import com.adobe.cq.wcm.core.components.models.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Email page component model class
 */
@ConsumerType
public interface EmailPage {

    /**
     * Retrieves the wcm core page object.
     *
     * @return {@link Page} the wcm core page object
     * @since com.adobe.cq.email.core.components.models 0.4.0; marked <code>default</code> in 0.4.0
     */
    @JsonIgnore
    default Page getPage() {
        return null;
    }

    /**
     * Retrieves the reference URL.
     *
     * @return the refernce URL that is used for building absolute URLs as a {@link String}.
     * @since com.adobe.cq.email.core.components.models 0.4.0; marked <code>default</code> in 0.4.0
     */
    default String getReferenceUrl() {
        return "";
    }

    /**
     * Retrieves the Campaign status header.
     *
     * @return the status header as {@link String}.
     * @since com.adobe.cq.email.core.components.models 0.4.0; marked <code>default</code> in 0.4.0
     */
    default String getStatusHeader() {
        return "";
    }

    /**
     * Retrieves the campaign status message.
     *
     * @return the status message as a {@link String}.
     * @since com.adobe.cq.email.core.components.models 0.4.0; marked <code>default</code> in 0.4.0
     */
    default String getStatusMessage() {
        return "";
    }

    /**
     * Retrieves the campaign alert type.
     *
     * @return the alert type as a {@link String}.
     * @since com.adobe.cq.email.core.components.models 0.4.0; marked <code>default</code> in 0.4.0
     */
    default String getAlertType() {
        return "";
    }
}
