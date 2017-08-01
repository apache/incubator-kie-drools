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

package org.jbpm.services.task.audit.variable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;

import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskVariable;
import org.kie.internal.task.api.TaskVariableIndexer;

/**
 * Represents logic behind mechanism to index task variables.
 * Supports custom indexers to be loaded dynamically via JDK ServiceLoader
 * 
 * Adds default indexer (org.jbpm.services.task.audit.variable.StringTaskVariableIndexer) as the last indexer
 * as it accepts all types
 *
 */
public class TaskIndexerManager {
    
    private static ServiceLoader<TaskVariableIndexer> taskVariableIndexers = ServiceLoader.load(TaskVariableIndexer.class);
    
    private static TaskIndexerManager INSTANCE = new TaskIndexerManager();
    
    private List<TaskVariableIndexer> indexers = new ArrayList<TaskVariableIndexer>();
    
    private TaskIndexerManager() {
        for (TaskVariableIndexer indexer : taskVariableIndexers) {
            indexers.add(indexer);
        }
        
        // always add at the end the default one
        indexers.add(new StringTaskVariableIndexer());
    }
    
    public List<TaskVariable> index(Task task, String variableName, Object variable) {
        for (TaskVariableIndexer indexer : indexers) {
            if (indexer.accept(variable)) {
                List<TaskVariable> indexed = indexer.index(variableName, variable);
                
                if (indexed != null) {
                                   
                    // populate all indexed variables with task information
                    for (TaskVariable taskVariable : indexed) {
                        taskVariable.setTaskId(task.getId());
                        taskVariable.setTaskId(task.getId());
                        taskVariable.setProcessInstanceId(task.getTaskData().getProcessInstanceId());
                        taskVariable.setProcessId(task.getTaskData().getProcessId());
                        taskVariable.setModificationDate(new Date());
                    }
                    
                    return indexed;

                }
            }
        }
        
        return null;
    }
    
    public static TaskIndexerManager get() {
        return INSTANCE;
    }
}
