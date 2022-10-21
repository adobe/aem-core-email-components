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
package com.adobe.cq.email.core.commons.editor.dialog.segmenteditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

@Model(adaptables = {SlingHttpServletRequest.class})
public class Editor {

    public static final String CUSTOM_VALUE = "custom";

    private static final String NN_DEFINED_CONDITIONS = "definedConditions";
    private static final String PN_NAME = "name";
    private static final String PN_CONDITION = "condition";
    private static final String DEFAULT_VALUE = "default";
    private static final String CUSTOM_NAME = "Custom";
    private static final String DEFAULT_NAME = "Default";

    @Self
    private SlingHttpServletRequest request;

    private Resource container;
    private I18n i18n;
    private List<SegmentItem> items;
    private List<Condition> conditions;


    @PostConstruct
    private void initModel() {
        i18n = new I18n(request);
        readChildren();
        readConditions();
    }

    private void readChildren() {
        items = new ArrayList<>();
        String containerPath = request.getRequestPathInfo().getSuffix();
        if (StringUtils.isNotEmpty(containerPath)) {
            ResourceResolver resolver = request.getResourceResolver();
            container = resolver.getResource(containerPath);
            if (container != null) {
                ComponentManager componentManager = request.getResourceResolver().adaptTo(ComponentManager.class);
                if (componentManager != null){
                    for (Resource resource : container.getChildren()) {
                        if (resource != null) {
                            Component component = componentManager.getComponentOfResource(resource);
                            if (component != null) {
                                items.add(new SegmentItem(request, resource));
                            }
                        }
                    }
                }
            }
        }
    }

    private void readConditions() {
        conditions = new ArrayList<>();
        conditions.add(new Condition(i18n.get(DEFAULT_NAME), DEFAULT_VALUE));
        String suffix = request.getRequestPathInfo().getSuffix();
        if (StringUtils.isNotEmpty(suffix)) {
            ResourceResolver resourceResolver = request.getResourceResolver();
            ContentPolicyManager contentPolicyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
            Resource contentResource = resourceResolver.getResource(suffix);
            if (contentPolicyManager != null && contentResource != null) {
                ContentPolicy policy = contentPolicyManager.getPolicy(contentResource);
                if (policy != null) {
                    Resource conditionsResource = resourceResolver.getResource(policy.getPath() + "/" + NN_DEFINED_CONDITIONS);
                    if (conditionsResource != null) {
                        for (Resource next : conditionsResource.getChildren()) {
                            ValueMap valueMap = next.getValueMap();
                            String name = valueMap.get(PN_NAME, String.class);
                            String value = valueMap.get(PN_CONDITION, String.class);
                            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)) {
                                conditions.add(new Condition(name, value));
                            }
                        }
                    }
                }
            }
        }
        conditions.add(new Condition(i18n.get(CUSTOM_NAME), CUSTOM_VALUE));
    }

    /**
     * Retrieves the child items associated with this segment editor.
     *
     * @return a list of child items
     */
    public List<SegmentItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Retrieves the container resource associated with this children editor.
     *
     * @return the container resource, or {@code null} if no container can be found
     */
    public Resource getContainer() {
        return container;
    }

    /**
     * Retrieves the available conditions for the segment editor which are defined in the content policy.
     * @return a list of {@link Condition}'s
     */
    public List<Condition> getConditions() {
        return Collections.unmodifiableList(conditions);
    }
}
