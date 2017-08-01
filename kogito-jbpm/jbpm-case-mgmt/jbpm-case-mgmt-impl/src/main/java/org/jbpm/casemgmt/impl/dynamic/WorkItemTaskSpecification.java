/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.impl.dynamic;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.casemgmt.api.dynamic.TaskSpecification;

/**
 * Defines work item task specification to create new "service" task in ad hoc (dynamic) process instance
 *
 */
public class WorkItemTaskSpecification implements TaskSpecification {
    
    private String nodeType;
    private String nodeName;
    private Map<String, Object> parameters;
        
    public WorkItemTaskSpecification(String nodeType, String nodeName, Map<String, Object> parameters) {
        this.nodeType = nodeType;
        this.nodeName = nodeName;
        this.parameters = parameters;
    }

    @Override
    public String getNodeType() {
        return nodeType;
    }

    @Override
    public Map<String, Object> getParameters() {
        if (nodeType == null || nodeName == null) {
            throw new IllegalArgumentException("Missing manadatory parameter - NodeType and NodeName");
        }
        Map<String, Object> workParams = new HashMap<String, Object>();
        if (parameters != null) {
            workParams.putAll(parameters);
        }
        workParams.put("TaskName", nodeName);
        return workParams;
    }

}
