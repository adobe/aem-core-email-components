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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.TemplatedResource;
import com.day.cq.wcm.foundation.model.responsivegrid.ResponsiveColumn;

/**
 * Container ob
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class ContainerModel {

    @Inject
    @Via("resource")
    @Optional
    @Default(values = "6")
    private String layout = "6";

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    private List<ContainerColumn> resources = new ArrayList<>();

    /**
     * Getter for columns
     *
     * @return the list column resources
     */
    public List<? extends ContainerColumn> getColumns() {
        return resources;
    }

    /**
     * Getter for layout
     *
     * @return the layout
     */
    public String getLayout() {
        return layout;
    }

    private String[] colClasses = new String[3];

    @PostConstruct
    private void initModel() {
        buildClass();
        initializeGrid();
    }

    private void initializeGrid() {
        switch (layout) {
            case "3-3":
            case "2-4":
            case "4-2":
                initColumns(2);
                break;
            case "2-2-2":
                initColumns(3);
                break;
            case "6":
            default:
                initColumns(1);;
        }
    }

    private void initColumns(int i) {
        Resource effectiveResource = getEffectiveResource();
        for (int j = 0; j < i; j++) {
            Resource child = effectiveResource.getChild("col-" + j);
            if (child != null) {
                resources.add(new ContainerColumn(colClasses[j], child));
            }
        }
    }

    public <T extends Resource> T getEffectiveResource() {
        if (resource instanceof TemplatedResource) {
            return (T) resource;
        }
        if (resource instanceof ResourceWrapper) {
            Resource wrappedResource = ((ResourceWrapper) resource).getResource();
            if (wrappedResource instanceof TemplatedResource) {
                return (T) resource;
            }
        }
        Resource templatedResource = request.adaptTo(TemplatedResource.class);

        if (templatedResource == null) {
            return (T) resource;
        } else {
            return (T) templatedResource;
        }
    }

    private void buildClass() {
        String[] splitString = layout.split("-");
        if (splitString.length > 0) {
            colClasses[0] = "grid-" + splitString[0];
        }
        if (splitString.length > 1) {
            colClasses[1] = "grid-" + splitString[1];
        }
        if (splitString.length > 2) {
            colClasses[2] = "grid-" + splitString[2];
        }

    }

}
