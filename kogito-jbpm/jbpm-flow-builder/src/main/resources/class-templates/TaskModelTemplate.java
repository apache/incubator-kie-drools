/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.codegen;

import org.kie.kogito.process.workitem.TaskModel;

public class $TaskModel$ implements TaskModel<$TaskInput$, $TaskOutput$>{

    private String id;
    private String name;
    private int state;
    private String phase;
    private String phaseStatus;
    private $TaskInput$ parameters;
    private $TaskOutput$ results;
    
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public int getState() {
        return state;
    }
    
    public void setState (int state) {
        this.state = state;
    }

    public String getPhase() {
        return phase;
    }
    
    public void setPhase (String phase) {
        this.phase = phase;
    }

    public String getPhaseStatus() {
        return phaseStatus;
    }
    
    public void setPhaseStatus (String phaseStatus) {
        this.phaseStatus  = phaseStatus;
    }
    
    public $TaskInput$ getParameters () {
        return parameters;
    }
    
    public void setParameters ($TaskInput$ parameters) {
        this.parameters = parameters;
    }
    
    public $TaskOutput$ getResults () {
        return results;
    }
    
    public void setParams ($TaskOutput$ results) {
        this.results = results;
    }

    public static  $TaskModel$ from(org.kie.kogito.process.WorkItem workItem) {
        $TaskModel$ taskModel = new $TaskModel$();
        taskModel.id= workItem.getId();
        taskModel.name = workItem.getName();
        taskModel.state = workItem.getState();
        taskModel.phaseStatus = workItem.getPhaseStatus();
        taskModel.phase = workItem.getPhase();
        taskModel.parameters = $TaskInput$.fromMap(workItem.getParameters());
        taskModel.results = $TaskOutput$.fromMap(workItem.getResults());
        return taskModel;
    }
}