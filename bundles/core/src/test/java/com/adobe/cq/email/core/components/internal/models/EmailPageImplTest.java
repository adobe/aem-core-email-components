/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

import com.adobe.cq.email.core.components.models.EmailPage;
import com.adobe.cq.wcm.core.components.models.Page;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EmailPageImplTest {
    private final AemContext ctx = new AemContext();

    @Mock
    Page page;

    @Mock
    private ModelFactory modelFactory;

    @BeforeEach
    void setUp() {
        ctx.addModelsForClasses(EmailPageImpl.class, EmailPage.class);
        ctx.load().json("/content/TestPage.json", "/content");
        ctx.currentResource("/content/experiencepage/jcr:content");
        lenient().when(modelFactory.getModelFromWrappedRequest(eq(ctx.request()), any(Resource.class), eq(Page.class))).thenReturn(page);

        ctx.registerService(ModelFactory.class, modelFactory, org.osgi.framework.Constants.SERVICE_RANKING, Integer.MAX_VALUE);
    }

    @Test
    void initModel() {
        ctx.currentResource("/content/experiencepage/jcr:content");
        EmailPage emailPage = ctx.request().adaptTo(EmailPageImpl.class);
        // TODO: fix tests (add services, test new methods)
        assertNotNull(emailPage);
        assertNotNull(emailPage.getPage());
        assertEquals(emailPage.getReferenceUrl(), "https://www.test.dev");
    }
}
