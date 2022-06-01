/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.email.core.components.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.resourceTypes="+ DefineConditionsDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class DefineConditionsDataSourceServlet extends SlingSafeMethodsServlet {

    protected static final String RESOURCE_TYPE = "core/email/components/commons/datasources/definecondition/v1";
    protected static final String PN_ALLOWED_CONDITIONS = "definedCondition";
    protected static final String PN_DEFINED_CONDITION = "value";

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        SimpleDataSource allowedConditions = new SimpleDataSource(getDefinedConditions(request).iterator());
        request.setAttribute(DataSource.class.getName(), allowedConditions);
    }

    protected List<Resource> getDefinedConditions(@NotNull SlingHttpServletRequest request) {
        List<Resource> conditions = new ArrayList<>();
        ResourceResolver resolver = request.getResourceResolver();
        Resource contentResource = resolver.getResource((String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE));
        ContentPolicyManager policyMgr = resolver.adaptTo(ContentPolicyManager.class);
        if (policyMgr != null) {
            ContentPolicy policy = policyMgr.getPolicy(contentResource);
            if (policy != null) {
                ValueMap condition = null;
                ValueMap properties = policy.getProperties();
                if (properties != null) {
                    String[] allowedConditions = properties.get(PN_ALLOWED_CONDITIONS, String[].class);
                    if (allowedConditions != null && allowedConditions.length > 0) {
                        for (String allowedCondition : allowedConditions) {
                            condition = new ValueMapDecorator(new HashMap<String, Object>());
                            condition.put(PN_DEFINED_CONDITION, allowedCondition);
                            conditions.add(new ConditionElementResource(allowedCondition, resolver));
                        }
                    }
                }
            }
        }
        return conditions;
    }
    private static class ConditionElementResource extends SyntheticResource {

        private final String elementName;
        private ValueMap valueMap;

        ConditionElementResource(String headingElement, ResourceResolver resourceResolver) {
            super(resourceResolver, StringUtils.EMPTY, RESOURCE_TYPE_NON_EXISTING);
            this.elementName = headingElement;
        }
        public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
            if (type == ValueMap.class) {
                if (this.valueMap == null) {
                    this.initValueMap();
                }

                return (AdapterType) this.valueMap;
            } else {
                return super.adaptTo(type);
            }
        }

        private void initValueMap() {
            this.valueMap = new ValueMapDecorator(new HashMap());
            this.valueMap.put("value", this.getValue());
            this.valueMap.put("text", this.getText());
            this.valueMap.put("selected", this.getSelected());
        }


        public String getText() {
            return elementName;
        }

        public String getValue() {
            return elementName;
        }

        public boolean getSelected() {
            return false;
        }
    }
}
