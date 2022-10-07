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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.components.internal.models.SegmentationImpl;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.crx.JcrConstants;

@Component(service = SlingPostProcessor.class)
public class SegmentationPostProcessor implements SlingPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SegmentationPostProcessor.class);
    private static final String RT_GHOST = "wcm/msm/components/ghost";
    private static final String PARAM_ORDERED_CHILDREN = "itemOrder";
    private static final String PARAM_COPIED_CHILDREN = "copiedItems";
    private static final String PARAM_DELETED_CHILDREN = "deletedItems";

    @Reference
    private transient LiveRelationshipManager liveRelationshipManager;

    @Override
    public void process(SlingHttpServletRequest request, List<Modification> list) throws Exception {
        ResourceResolver resourceResolver = request.getResource().getResourceResolver();
        if (accepts(request, resourceResolver)) {
            Resource container = request.getResource();
            try {
                handleCopies(container, request, resourceResolver);
                handleOrder(container, request);
                handleDelete(container, request, resourceResolver);
            } catch (RepositoryException e) {
                LOGGER.error("Could not order items of the container at {}", container.getPath(), e);
            }
        }
    }

    protected void handleCopies(Resource container, SlingHttpServletRequest request, ResourceResolver resourceResolver)
            throws PersistenceException {
        String[] childCopies = StringUtils.split(request.getParameter(PARAM_COPIED_CHILDREN), ",");
        if (childCopies != null && childCopies.length > 0) {
            for (String copy : childCopies) {
                String to = StringUtils.substringBefore(copy, ":");
                String from = StringUtils.substringAfter(copy, ":");
                if (StringUtils.isNotEmpty(from) && StringUtils.isNotEmpty(to)) {
                    Resource copyFromChild = container.getChild(from);
                    Resource copyToChild = container.getChild(to);
                    if (copyFromChild != null && copyToChild != null) {
                        copyResource(copyFromChild, container, copyToChild.getName(), copyToChild.getValueMap(), resourceResolver);
                    }
                }
            }
        }
    }

    protected void handleOrder(Resource container, SlingHttpServletRequest request) throws RepositoryException {
        String[] orderedChildrenNames = StringUtils.split(request.getParameter(PARAM_ORDERED_CHILDREN), ",");
        if (orderedChildrenNames != null && orderedChildrenNames.length > 0) {
            final Node containerNode = container.adaptTo(Node.class);
            if (containerNode != null) {
                for (int i = orderedChildrenNames.length - 1; i >= 0; i--) {
                    if (i == orderedChildrenNames.length - 1 && containerNode.hasNode(orderedChildrenNames[i])) {
                        containerNode.orderBefore(orderedChildrenNames[i], null);
                    } else if (containerNode.hasNode(orderedChildrenNames[i]) && containerNode.hasNode(orderedChildrenNames[i])) {
                        containerNode.orderBefore(orderedChildrenNames[i], orderedChildrenNames[i + 1]);
                    }
                }
            }
        }
    }

    protected void handleDelete(Resource container, SlingHttpServletRequest request, ResourceResolver resolver)
            throws PersistenceException, WCMException, RepositoryException {
        String[] deletedChildrenNames = StringUtils.split(request.getParameter(PARAM_DELETED_CHILDREN), ",");
        if (deletedChildrenNames != null && deletedChildrenNames.length > 0) {
            for (String childName : deletedChildrenNames) {
                Resource child = container.getChild(childName);
                if (child != null) {
                    // For deleted items that have a live relationship, ensure a ghost is created
                    LiveRelationship liveRelationship = liveRelationshipManager.getLiveRelationship(child, false);
                    if (liveRelationship != null && liveRelationship.getStatus().isSourceExisting()) {
                        liveRelationshipManager.cancelRelationship(resolver, liveRelationship, true, false);
                        Resource parent = child.getParent();
                        String name = child.getName();
                        resolver.delete(child);
                        if (parent != null) {
                            createGhost(parent, name, resolver);
                        }
                    } else {
                        resolver.delete(child);
                    }
                }
            }
        }
    }

    private void createGhost(@NotNull Resource parent, String name, ResourceResolver resolver)
            throws PersistenceException, RepositoryException, WCMException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
        properties.put(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, RT_GHOST);
        resolver.create(parent, name, properties);
    }

    private Resource copyResource(Resource src, Resource destParent, String name, Map<String, Object> properties, ResourceResolver resolver)
            throws PersistenceException {
        if (name == null) {
            name = src.getName();
        }
        Map<String, Object> finalProperties = Stream.of(src.getValueMap(), properties)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, o2) -> o2));

        Resource child = destParent.getChild(name);
        if (child != null) {
            resolver.delete(child);
        }
        child = resolver.create(destParent, name, finalProperties);

        Iterator<Resource> iter = src.getChildren().iterator();

        while (true) {
            Resource n;
            if (!iter.hasNext()) {
                return child;
            }
            n = iter.next();
            copyResource(n, child, null, Collections.emptyMap(), resolver);
        }
    }

    private boolean accepts(SlingHttpServletRequest request, ResourceResolver resolver) {
        return resolver.isResourceType(request.getResource(), SegmentationImpl.RESOURCE_TYPE);
    }
}
