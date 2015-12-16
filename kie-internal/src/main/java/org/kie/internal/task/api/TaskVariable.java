/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.task.api;

import java.util.Date;

/**
 * Represents single Task variable entity
 *
 */
public interface TaskVariable {
    
    public enum VariableType {
        INPUT,
        OUTPUT;
    }

    /**
     * Returns task id that this variable belongs to
     * @return
     */
    Long getTaskId();
    
    /**
     * Returns process instance id that the task this variable belongs to is owned by
     * This might be null in case ad hoc tasks
     * @return
     */
    Long getProcessInstanceId();
    
    /**
     * Returns process id that the task this variable belongs to is owned by
     * This might be null in case ad hoc tasks
     * @return
     */
    String getProcessId();
    
    /**
     * Returns name of the variable
     * @return
     */
    String getName();
    
    /**
     * Returns value of this variable - its string representation that can be queried
     * @return
     */
    String getValue();
    
    /**
     * Return type of the variable - either input or output
     * @return
     */
    VariableType getType();
    
    /**
     * Returns last time this variable was modified
     * @return
     */
    Date getModificationDate();
    
    void setTaskId(Long taskId);
    
    void setProcessInstanceId(Long processInstanceId);
    
    void setProcessId(String processId);
    
    void setName(String name);
    
    void setValue(String value);
    
    void setType(VariableType type);
    
    void setModificationDate(Date modificationDate);
}
