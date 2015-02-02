/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.kie.services.impl.bpmn2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.services.api.model.UserTaskDefinition;

public class ProcessDescRepoHelper {

    private ProcessAssetDesc process;
    private Map<String, UserTaskDefinition> tasks = new HashMap<String, UserTaskDefinition>();
    private Map<String, Map<String, String>> taskInputMappings = new HashMap<String, Map<String, String>>();
    private Map<String, Map<String, String>> taskOutputMappings = new HashMap<String, Map<String, String>>();
    private Map<String, String> inputs = new HashMap<String, String>();
    private Map<String, Collection<String>> taskAssignments = new HashMap<String, Collection<String>>();
    private Collection<String> reusableSubProcesses = new ArrayList<String>();
    private Map<String, String> itemDefinitions = new HashMap<String, String>();
    private Map<String, String> serviceTasks = new HashMap<String, String>();
    private Map<String, String> globalItemDefinitions = new HashMap<String, String>();
    
    public ProcessDescRepoHelper() {
    }

    public void setProcess(ProcessAssetDesc process) {
        this.process = process;
    }


    public ProcessAssetDesc getProcess() {
        return process;
    }

    public Map<String, UserTaskDefinition> getTasks() {
        return tasks;
    }

    public Map<String, Map<String, String>> getTaskInputMappings() {
        return taskInputMappings;
    }
    
    public Map<String, Map<String, String>> getTaskOutputMappings() {
        return taskOutputMappings;
    }

    public Map<String, String> getInputs() {
        return inputs;
    }

    public Map<String, Collection<String>> getTaskAssignments() {
        return taskAssignments;
    }

    public Map<String, String> getItemDefinitions() {
        return itemDefinitions;
    }

    public void setItemDefinitions(Map<String, String> itemDefinitions) {
        this.itemDefinitions = itemDefinitions;
    }

    public Map<String, String> getServiceTasks() {
        return serviceTasks;
    }

    public void setServiceTasks(Map<String, String> serviceTasks) {
        this.serviceTasks = serviceTasks;
    }    

    public Map<String, String> getGlobalItemDefinitions() {
        return globalItemDefinitions;
    }

    public Collection<String> getReusableSubProcesses() {
        return reusableSubProcesses;
    }

    public void setReusableSubProcesses(Collection<String> reusableSubProcesses) {
        this.reusableSubProcesses = reusableSubProcesses;
    }
    
    public void clear(){
        process = null;
        tasks.clear();
        taskInputMappings.clear();
        taskOutputMappings.clear();
        inputs.clear();
        taskAssignments.clear();
        reusableSubProcesses.clear();
        itemDefinitions.clear();
        serviceTasks.clear();
        globalItemDefinitions.clear();
    }

}
