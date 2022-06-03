
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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ContainerModelTest {

    private final AemContext ctx = new AemContext();
    private ContainerModel underTest;

    @BeforeEach
    void setUp() {
        ctx.addModelsForClasses(ContainerModel.class);
        ctx.load().json("/content/TestPage.json", "/content");
    }

    @Test
    void layout6() {
        Resource resource = ctx.currentResource("/content/experiencepage/jcr:content/root/container-6");
        underTest = ctx.request().adaptTo(ContainerModel.class);
        assertEquals(1, underTest.getColumns().size());
        assertEquals("grid-6", underTest.getColumns().get(0).getClassName());
        assertEquals("/content/experiencepage/jcr:content/root/container-6/col-0", underTest.getColumns().get(0).getPath());
        assertEquals("wcm/foundation/components/responsivegrid", underTest.getColumns().get(0).getResourceType());
        assertNull(underTest.getColumns().get(0).getResourceSuperType());
        assertEquals(new ResourceMetadata(), underTest.getColumns().get(0).getResourceMetadata());
        assertNotNull(underTest.getColumns().get(0).getResource());
    }

    @Test
    void layout33() {
        ctx.currentResource("/content/experiencepage/jcr:content/root/container-33");
        underTest = ctx.request().adaptTo(ContainerModel.class);
        assertEquals(2, underTest.getColumns().size());
        assertEquals("grid-3", underTest.getColumns().get(0).getClassName());
        assertEquals("grid-3", underTest.getColumns().get(1).getClassName());
    }

    @Test
    void layout24() {
        ctx.currentResource("/content/experiencepage/jcr:content/root/container-24");
        underTest = ctx.request().adaptTo(ContainerModel.class);
        assertEquals(2, underTest.getColumns().size());
        assertEquals("grid-2", underTest.getColumns().get(0).getClassName());
        assertEquals("grid-4", underTest.getColumns().get(1).getClassName());
    }

    @Test
    void layout42() {
        ctx.currentResource("/content/experiencepage/jcr:content/root/container-42");
        underTest = ctx.request().adaptTo(ContainerModel.class);
        assertEquals(2, underTest.getColumns().size());
        assertEquals("grid-4", underTest.getColumns().get(0).getClassName());
        assertEquals("grid-2", underTest.getColumns().get(1).getClassName());
    }

    @Test
    void layout222() {
        ctx.currentResource("/content/experiencepage/jcr:content/root/container-222");
        underTest = ctx.request().adaptTo(ContainerModel.class);
        assertEquals(3, underTest.getColumns().size());
        assertEquals("grid-2", underTest.getColumns().get(0).getClassName());
        assertEquals("grid-2", underTest.getColumns().get(1).getClassName());
        assertEquals("grid-2", underTest.getColumns().get(2).getClassName());
    }


}