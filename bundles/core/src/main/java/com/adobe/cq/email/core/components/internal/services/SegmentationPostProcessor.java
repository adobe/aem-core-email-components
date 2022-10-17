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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.email.core.components.internal.models.SegmentationImpl;

@Component(service = SlingPostProcessor.class,
           property = {
                   Constants.SERVICE_RANKING + ":Integer=" + Integer.MAX_VALUE
           })
public class SegmentationPostProcessor implements SlingPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SegmentationPostProcessor.class);
    private static final String PARAM_ORDERED_CHILDREN = "itemOrder";
    private static final String PARAM_COPIED_CHILDREN = "copiedItems";
    private static final String PARAM_DELETED_CHILDREN = "deletedItems";

    @Override
    public void process(SlingHttpServletRequest request, List<Modification> modifications) throws Exception {
        ResourceResolver resourceResolver = request.getResource().getResourceResolver();
        ArrayList<Modification> addedModifications = new ArrayList<Modification>();
        if (accepts(request, resourceResolver)) {
            Resource container = request.getResource();
            try {
                handleCopies(container, request, resourceResolver, addedModifications);
                handleOrder(container, request);
                handleDelete(container, request, resourceResolver, addedModifications);
            } catch (RepositoryException e) {
                LOGGER.error("Could not order items of the container at {}", container.getPath(), e);
            }
        }
        modifications.addAll(addedModifications);
    }

    protected void handleCopies(Resource container, SlingHttpServletRequest request, ResourceResolver resourceResolver,
                                List<Modification> addedModifications)
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
                        addedModifications.add(Modification.onCopied(copyFromChild.getPath(), copyToChild.getPath()));
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

    protected void handleDelete(Resource container, SlingHttpServletRequest request, ResourceResolver resolver,
                                List<Modification> addedModifications)
            throws PersistenceException, RepositoryException {
        String[] deletedChildrenNames = StringUtils.split(request.getParameter(PARAM_DELETED_CHILDREN), ",");
        if (deletedChildrenNames != null && deletedChildrenNames.length > 0) {
            for (String childName : deletedChildrenNames) {
                Resource child = container.getChild(childName);
                if (child != null) {
                    String deletedPath = child.getPath();
                    resolver.delete(child);
                    addedModifications.add(Modification.onDeleted(deletedPath));
                }
            }
        }
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
