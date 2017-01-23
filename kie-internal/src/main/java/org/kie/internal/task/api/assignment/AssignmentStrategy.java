/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.internal.task.api.assignment;

import org.kie.api.task.TaskContext;
import org.kie.api.task.model.Task;

/**
 * Task assignment strategy that defines how to select actual owner based on task properties
 * such as potential owners, task data etc. Depending on the strategy implementation
 * additional source of information might be needed e.g. people availability etc.
 * 
 * Each strategy must uniquely identify itself as it's placed in the registry for further use.
 */
public interface AssignmentStrategy {
    
    /**
     * Returns unique identifier of the strategy
     * @return identifier to be used for further look ups
     */
    String getIdentifier();

    /**
     * Applies the strategy on given task based on concrete implementation details.
     * @param task task that should be assigned
     * @param context task context to be able to use various services including PersistenceContext
     * @param excludedUser user that should be excluded from the assignment as it's the one who performed operation
     * e.g. use who has been assigned to the task and released should not be reassign to it again
     * @return returns single Assignment selected for this task or null if none was found 
     */
    Assignment apply(Task task, TaskContext context, String excludedUser);       
    
    /**
     * Optional method that notifies the strategy about task being done (completed, exited, etc). 
     * Mainly for strategies that do maintain state so tasks that are done are removed from 
     * assignees' queues
     * @param task task that has been done
     * @param context task context to be able to use various services including PersistenceContext
     */
    default void taskDone(Task task, TaskContext context) {
        
    };
}
