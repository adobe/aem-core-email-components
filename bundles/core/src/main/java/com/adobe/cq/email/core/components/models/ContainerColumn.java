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

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.AbstractResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
/**
 * Wrapper {@link org.apache.sling.api.resource.Resource} for a column of the {@link ContainerModel}
 */
public class ContainerColumn extends AbstractResource {

    private String className;
    private Resource wrappedResource;

    public ContainerColumn(String className, Resource wrappedResource) {
        this.className = className;
        this.wrappedResource = wrappedResource;
    }

    @NotNull
    @Override
    public String getPath() {
        return wrappedResource.getPath();
    }

    @NotNull
    @Override
    public String getResourceType() {
        return wrappedResource.getResourceType();
    }

    @Nullable
    @Override
    public String getResourceSuperType() {
        return wrappedResource.getResourceSuperType();
    }

    @NotNull
    @Override
    public ResourceMetadata getResourceMetadata() {
        return wrappedResource.getResourceMetadata();
    }

    @NotNull
    @Override
    public ResourceResolver getResourceResolver() {
        return wrappedResource.getResourceResolver();
    }

    @Nonnull
    public Resource getResource() {
        return wrappedResource;
    }

    public String getClassName() {
        return className;
    }
}
