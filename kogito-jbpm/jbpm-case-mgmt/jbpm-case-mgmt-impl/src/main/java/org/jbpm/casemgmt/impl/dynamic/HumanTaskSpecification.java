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
 * Defines human task specification to create new user task in ad hoc (dynamic) process instance
 * Expected parameters are:
 * <ul>
 *  <li>NodeName - is a mandatory name of the task - accept variable expressions</li>
 *  <li>TaskName - is a optional look up name of the task's form - accept variable expressions</li>
 *  <li>ActorId - is an optional list of actors to be assigned - accept variable expressions</li>
 *  <li>GroupId - is an optional list of groups to be assigned - accept variable expressions</li>
 *  <li>Comment - is an optional comment/description of the task - accept variable expressions</li>
 * </ul>
 */
public class HumanTaskSpecification implements TaskSpecification {
    
    private String taskName;
    private String actorIds; 
    private String groupIds; 
    private String description;
    private Map<String, Object> parameters;
        
    public HumanTaskSpecification(String taskName, String actorIds, String groupIds, String description, Map<String, Object> parameters) {
        this.taskName = taskName;
        this.actorIds = actorIds;
        this.groupIds = groupIds;
        this.description = description;
        this.parameters = parameters;
    }

    @Override
    public String getNodeType() {
        return "Human Task";
    }

    @Override
    public Map<String, Object> getParameters() {
        if (taskName == null) {
            throw new IllegalArgumentException("Missing manadatory parameter - taskName");
        }
        Map<String, Object> workParams = new HashMap<String, Object>();
        if (parameters != null) {
            workParams.putAll(parameters);
        }
        workParams.put("NodeName", taskName);
        workParams.put("TaskName", taskName);
        workParams.put("ActorId", actorIds);
        workParams.put("GroupId", groupIds);
        workParams.put("Comment", description);
        
        return workParams;
    }

}
