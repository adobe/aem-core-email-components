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
package com.adobe.cq.email.core.components.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.sun.org.apache.xerces.internal.impl.dv.xs.QNameDV;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = Resource.class)
public class SegmentationItemModel {

    @Self
    Resource resource;

    private String openingACCMarkup;
    private String closingACCMarkup;

    @Inject
    @Optional
    private String condition;

    @Inject @Named("default")
    @Optional
    private boolean defaultBranch = false;


    @PostConstruct
    private void initModel() {
        Resource segmentationComponent = resource.getParent();
        Resource nextResource = null;

        int currentIndex = 0;
        int total = 0;
        for(Resource child : segmentationComponent.getChildren()) {
            if(StringUtils.equals(child.getPath(), resource.getPath())) {
                currentIndex = total;
            }
            if(currentIndex+1 == total) {
                nextResource = child;
            }
            total++;
        }

        if(StringUtils.isNotBlank(condition) || defaultBranch) {
            if (currentIndex == 0) {
                openingACCMarkup = "<% if (" + condition + ") { %>";
            } else {
                openingACCMarkup = "";
            }

            if (currentIndex+1 == total) {
                closingACCMarkup = "<% } %>";
            } else if (nextResource != null) {
                ValueMap nextVm = nextResource.getValueMap();
                boolean nextDefault = nextVm.get("default", false);
                String nextCondition = nextVm.get("condition", "");

                if (nextDefault) {
                    closingACCMarkup = "<% } else { %>";
                } else if (StringUtils.isNotBlank(nextCondition)) {
                    closingACCMarkup = "<% } else if (" + nextCondition + ") { %>";
                } else {
                    closingACCMarkup = "<% } %>";
                }
            } else {
                closingACCMarkup = "<% } %>";
            }
        }
    }

    public String getOpeningACCMarkup() {
        return openingACCMarkup;
    }
    public String getClosingACCMarkup() {
        return closingACCMarkup;
    }
}



