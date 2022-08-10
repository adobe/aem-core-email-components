
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

import java.util.Iterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.wcm.api.TemplatedResource;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ContainerImplTest {

    private final AemContext ctx = new AemContext();
    private ContainerImpl underTest;

    @BeforeEach
    void setUp() {
        ctx.addModelsForClasses(ContainerImpl.class);
        ctx.load().json("/content/TestPage.json", "/content");
        ctx.load().json("/conf/TestConf.json", "/conf");
    }

    @Test
    void layoutDefault() {
        ctx.currentResource("/content/experiencepage/jcr:content/root/container");
        underTest = ctx.request().adaptTo(ContainerImpl.class);
        assertNotNull(underTest);
        assertEquals(1, underTest.getColumns().size());
        assertEquals("grid-6", underTest.getColumns().get(0).getClassName());
        assertEquals("/content/experiencepage/jcr:content/root/container/col-0", underTest.getColumns().get(0).getResource().getPath());
        assertEquals("wcm/foundation/components/responsivegrid", underTest.getColumns().get(0).getResource().getResourceType());
        assertNull(underTest.getColumns().get(0).getResource().getResourceSuperType());
        assertEquals(new ResourceMetadata(), underTest.getColumns().get(0).getResource().getResourceMetadata());
        assertEquals(ctx.resourceResolver(), underTest.getColumns().get(0).getResource().getResourceResolver());
        assertNotNull(underTest.getColumns().get(0).getResource());
    }

    @Test
    void layout6() {
        ctx.currentResource("/content/experiencepage/jcr:content/root/container-6");
        underTest = ctx.request().adaptTo(ContainerImpl.class);
        assertNotNull(underTest);
        assertEquals(1, underTest.getColumns().size());
        assertEquals("grid-6", underTest.getColumns().get(0).getClassName());
        assertEquals("/content/experiencepage/jcr:content/root/container-6/col-0", underTest.getColumns().get(0).getResource().getPath());
        assertEquals("wcm/foundation/components/responsivegrid", underTest.getColumns().get(0).getResource().getResourceType());
        assertNull(underTest.getColumns().get(0).getResource().getResourceSuperType());
        assertEquals(new ResourceMetadata(), underTest.getColumns().get(0).getResource().getResourceMetadata());
        assertEquals(ctx.resourceResolver(), underTest.getColumns().get(0).getResource().getResourceResolver());
        assertNotNull(underTest.getColumns().get(0).getResource());
    }

    @Test
    void layout33() {
        ctx.currentResource("/content/experiencepage/jcr:content/root/container-33");
        underTest = ctx.request().adaptTo(ContainerImpl.class);
        assertNotNull(underTest);
        assertEquals(2, underTest.getColumns().size());
        assertEquals("grid-3", underTest.getColumns().get(0).getClassName());
        assertEquals("grid-3", underTest.getColumns().get(1).getClassName());
    }

    @Test
    void layout24() {
        ctx.currentResource("/content/experiencepage/jcr:content/root/container-24");
        underTest = ctx.request().adaptTo(ContainerImpl.class);
        assertNotNull(underTest);
        assertEquals(2, underTest.getColumns().size());
        assertEquals("grid-2", underTest.getColumns().get(0).getClassName());
        assertEquals("grid-4", underTest.getColumns().get(1).getClassName());
    }

    @Test
    void layout42() {
        ctx.currentResource("/content/experiencepage/jcr:content/root/container-42");
        underTest = ctx.request().adaptTo(ContainerImpl.class);
        assertNotNull(underTest);
        assertEquals(2, underTest.getColumns().size());
        assertEquals("grid-4", underTest.getColumns().get(0).getClassName());
        assertEquals("grid-2", underTest.getColumns().get(1).getClassName());
    }

    @Test
    void layout222() {
        ctx.currentResource("/content/experiencepage/jcr:content/root/container-222");
        underTest = ctx.request().adaptTo(ContainerImpl.class);
        assertNotNull(underTest);
        assertEquals(3, underTest.getColumns().size());
        assertEquals("grid-2", underTest.getColumns().get(0).getClassName());
        assertEquals("grid-2", underTest.getColumns().get(1).getClassName());
        assertEquals("grid-2", underTest.getColumns().get(2).getClassName());
    }

    @Test
    void templatedResource() {
        Resource resource = ctx.currentResource(
                "/conf/core-email-components-examples/settings/wcm/templates/email-template/structure/jcr:content/root" +
                        "/container");
        Resource templatedResource = new MockTemplatedResource(resource);
        ctx.currentResource(templatedResource);
        new MockSlingHttpServletRequest(ctx.bundleContext());
        underTest = ctx.request().adaptTo(ContainerImpl.class);
        assertNotNull(underTest);
        assertEquals(2, underTest.getColumns().size());
    }

    @Test
    void wrappedResource() {
        Resource resource = ctx.currentResource(
                "/conf/core-email-components-examples/settings/wcm/templates/email-template/structure/jcr:content/root" +
                        "/container");
        Resource templatedResource = new MockTemplatedResource(resource);
        ResourceWrapper resourceWrapper = new ResourceWrapper(templatedResource);
        ctx.currentResource(resourceWrapper);
        underTest = ctx.request().adaptTo(ContainerImpl.class);
        assertNotNull(underTest);
        assertEquals(2, underTest.getColumns().size());
    }



    private static class MockTemplatedResource implements TemplatedResource {
        private final Resource wrappedResource;

        public MockTemplatedResource(Resource wrappedResource) {
            this.wrappedResource = wrappedResource;
        }

        @Override
        public Resource getResource() {
            return wrappedResource;
        }

        @Override
        public @NotNull String getPath() {
            return wrappedResource.getPath();
        }

        @Override
        public @NotNull String getName() {
            return wrappedResource.getName();
        }

        @Override
        public @Nullable Resource getParent() {
            return wrappedResource.getParent();
        }

        @Override
        public @NotNull Iterator<Resource> listChildren() {
            return wrappedResource.listChildren();
        }

        @Override
        public @NotNull Iterable<Resource> getChildren() {
            return wrappedResource.getChildren();
        }

        @Override
        public @Nullable Resource getChild(@NotNull String s) {
            return wrappedResource.getChild(s);
        }

        @Override
        public @NotNull String getResourceType() {
            return wrappedResource.getResourceType();
        }

        @Override
        public @Nullable String getResourceSuperType() {
            return wrappedResource.getResourceSuperType();
        }

        @Override
        public boolean hasChildren() {
            return wrappedResource.hasChildren();
        }

        @Override
        public boolean isResourceType(String s) {
            return wrappedResource.isResourceType(s);
        }

        @Override
        public @NotNull ResourceMetadata getResourceMetadata() {
            return wrappedResource.getResourceMetadata();
        }

        @Override
        public @NotNull ResourceResolver getResourceResolver() {
            return wrappedResource.getResourceResolver();
        }

        @Override
        public @NotNull ValueMap getValueMap() {
            return wrappedResource.getValueMap();
        }

        @Override
        public <AdapterType> @Nullable AdapterType adaptTo(@NotNull Class<AdapterType> aClass) {
            return wrappedResource.adaptTo(aClass);
        }
    }

}
