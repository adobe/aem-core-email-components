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
package com.adobe.cq.email.core.components.internal.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.email.core.components.models.EmailPage;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {EmailPage.class}, resourceType = EmailPageImpl.RESOURCE_TYPE)
public class EmailPageImpl implements EmailPage {

    /**
     * The resource type.
     */
    public static final String RESOURCE_TYPE = "core/email/components/page/v1/page";

    /**
     * Property name of the pre header
     */
    protected static final String PN_PRE_HEADER = "preheader";

    /**
     * The page pre header if set, null if not.
     */
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL, name = PN_PRE_HEADER)
    @Nullable
    private String preHeader;

    @Override
    @Nullable
    public String getPreHeader() {
        return preHeader;
    }
}
