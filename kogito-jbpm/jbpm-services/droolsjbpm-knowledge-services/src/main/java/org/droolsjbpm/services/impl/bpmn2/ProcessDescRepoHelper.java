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
package org.droolsjbpm.services.impl.bpmn2;

import java.util.HashMap;
import java.util.Map;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jbpm.task.TaskDef;

/**
 *
 * @author salaboy
 */
public class ProcessDescRepoHelper {

    private ProcessDesc process;
    private Map<String, TaskDef> tasks = new HashMap<String, TaskDef>();
    private Map<String, Map<String, String>> taskInputMappings = new HashMap<String, Map<String, String>>();
    private Map<String, Map<String, String>> taskOutputMappings = new HashMap<String, Map<String, String>>();
    private Map<String, String> inputs = new HashMap<String, String>();
    private Map<String, String> taskAssignments = new HashMap<String, String>();

    public ProcessDescRepoHelper() {
    }

    public void setProcess(ProcessDesc process) {
        this.process = process;
    }


    public ProcessDesc getProcess() {
        return process;
    }

    public Map<String, TaskDef> getTasks() {
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

    public Map<String, String> getTaskAssignments() {
        return taskAssignments;
    }
}
