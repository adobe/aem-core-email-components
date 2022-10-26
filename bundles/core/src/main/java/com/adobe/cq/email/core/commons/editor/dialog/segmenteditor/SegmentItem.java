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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.annotation.versioning.ProviderType;

import com.adobe.cq.wcm.core.components.commons.editor.dialog.childreneditor.Item;

@ProviderType
public class SegmentItem extends Item {

    private String condition;
    private String customCondition;
    private boolean disabled;


    /**
     * Name of the resource property that defines a condition
     */
    public static final String PN_CONDITION = "condition";
    public static final String PN_CUSTOM_SEGMENT_CONDITION = "customSegmentCondition";
    public static final String RT_GHOST = "wcm/msm/components/ghost";

    public SegmentItem(SlingHttpServletRequest request, Resource resource) {
        super(request, resource);
        if (resource != null) {
            ValueMap vm = resource.getValueMap();
            condition = vm.get(PN_CONDITION, String.class);
            customCondition = vm.get(PN_CUSTOM_SEGMENT_CONDITION, String.class);
            disabled = resource.isResourceType(RT_GHOST);
        }
    }

    /**
     * Retrieves the path of the condition item.
     *
     * @return the {@code SegmentItem} condition
     */
    public String getCondition() {
        return condition;
    }

    public String getCustomCondition() {
        return customCondition;
    }

    public boolean isDisabled() {
        return disabled;
    }
}
