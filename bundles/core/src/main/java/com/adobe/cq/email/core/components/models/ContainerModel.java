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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class ContainerModel {

    @Inject
    @Optional
    @Default(values = "6")
    private String layout;

    public String getColClass1() {
        return colClass1;
    }

    public String getColClass2() {
        return colClass2;
    }

    public String getColClass3() {
        return colClass3;
    }

    public int getColumns() {
        return columns;
    }

    public String getLayout() {
        return layout;
    }

    private String colClass1;
    private String colClass2;
    private String colClass3;
    private int columns;


    @PostConstruct
    private void initModel() {
        columns = 1;
        initializeGrid();
        buildClass();
    }

    private void initializeGrid() {
        switch (layout) {
            case "3-3":
            case "2-4":
            case "4-2":
                columns = 2;
                break;
            case "2-2-2":
                columns = 3;
                break;
            case "6":
            default:
                columns = 1;
        }
    }

    private void buildClass() {
        String[] splitString = layout.split("-");
        if (splitString.length > 0) {
            colClass1 = "grid-" + splitString[0];
        }
        if (splitString.length > 1) {
            colClass2 = "grid-" + splitString[1];
        }
        if (splitString.length > 2) {
            colClass3 = "grid-" + splitString[2];
        }

    }

}
