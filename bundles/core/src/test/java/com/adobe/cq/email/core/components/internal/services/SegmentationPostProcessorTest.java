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
package com.adobe.cq.email.core.components.internal.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.internal.models.SegmentationImpl;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.LiveStatus;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SegmentationPostProcessorTest {

    SegmentationPostProcessor segmentationPostProcessor = new SegmentationPostProcessor();

    private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);
    private static final String PARAM_ORDERED_CHILDREN = "itemOrder";
    private static final String PARAM_COPIED_CHILDREN = "copiedItems";
    private static final String PARAM_DELETED_CHILDREN = "deletedItems";

    @Test
    public void testOrderChildren() throws IOException, RepositoryException {
        context.create().resource("/test/dummy/container");
        context.create().resource("/test/dummy/container/child1");
        context.create().resource("/test/dummy/container/child2");
        String[] reorderedChildren = new String[]{"child2","child1"};
        context.request().setParameterMap(ImmutableMap.of(PARAM_ORDERED_CHILDREN,
                String.join(",", reorderedChildren)));
        segmentationPostProcessor.handleOrder(context.currentResource("/test/dummy/container"), context.request());
        Iterable<Resource> childrenIterator = context.currentResource().getChildren();
        List<Resource> childList     = StreamSupport
                .stream(childrenIterator.spliterator(), false)
                .collect(Collectors.toList());
        Iterator<String> expectedIterator = Arrays.asList(reorderedChildren).iterator();
        assertEquals(2, childList.size());
        // compare new order with wishlist :)
        for (Resource resource : childList) {
            assertEquals(resource.getName(), expectedIterator.next(), "Reordering children failed");
        }
    }

    @Test
    public void testCopyChildren() throws IOException {
        Resource container = context.create().resource("/test/dummy/container");
        context.create().resource("/test/dummy/container/child1", ImmutableMap.of("foo", "bar"));
        context.create().resource("/test/dummy/container/child1/child11", ImmutableMap.of("hello", "world"));
        context.create().resource("/test/dummy/container/child2");
        String copiedChildren = "child2:child1";
        context.currentResource(container);
        context.request().setParameterMap(ImmutableMap.of(PARAM_COPIED_CHILDREN, copiedChildren));
        segmentationPostProcessor.handleCopies(container, context.request(), context.resourceResolver(), new ArrayList<>());
        Iterable<Resource> childrenIterator = context.currentResource().getChildren();
        List<Resource> childList     = StreamSupport
                .stream(childrenIterator.spliterator(), false)
                .collect(Collectors.toList());
        assertEquals(2, childList.size());
        Resource child2 = container.getChild("child2");
        assertNotNull(child2);
        assertEquals("bar", child2.getValueMap().get("foo"));
        assertNotNull(child2.getChild("child11"));
    }

    @Test
    public void testDeleteChildren() throws IOException, RepositoryException, WCMException {
        Resource container = context.create().resource("/test/dummy/container");
        context.create().resource("/test/dummy/container/child1");
        context.create().resource("/test/dummy/container/child2");
        context.currentResource(container);
        String deletedChildren = "child2";
        context.request().setParameterMap(ImmutableMap.of(PARAM_DELETED_CHILDREN, deletedChildren));
        segmentationPostProcessor.handleDelete(container, context.request(), context.resourceResolver(), new ArrayList<>());
        Iterable<Resource> childrenIterator = context.currentResource().getChildren();
        List<Resource> childList     = StreamSupport
                .stream(childrenIterator.spliterator(), false)
                .collect(Collectors.toList());
        assertEquals(1, childList.size());
    }

    @Test
    public void testProcess() throws Exception {
        Resource container = context.create().resource("/test/dummy/container", ImmutableMap.of(
                "sling:resourceType", SegmentationImpl.RESOURCE_TYPE
        ));
        context.create().resource("/test/dummy/container/child1");
        String deletedChildren = "child1";
        context.request().setParameterMap(ImmutableMap.of(PARAM_DELETED_CHILDREN, deletedChildren));
        context.currentResource(container);
        segmentationPostProcessor.process(context.request(), new ArrayList<>());
        Iterable<Resource> childrenIterator = context.currentResource().getChildren();
        List<Resource> childList     = StreamSupport
                .stream(childrenIterator.spliterator(), false)
                .collect(Collectors.toList());
        assertEquals(0, childList.size());
    }

}