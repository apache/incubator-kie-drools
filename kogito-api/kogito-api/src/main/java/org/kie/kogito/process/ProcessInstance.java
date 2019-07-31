/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.process;

import java.util.List;
import java.util.Map;

import org.kie.api.runtime.process.WorkItemNotFoundException;



public interface ProcessInstance<T> {
    
    int STATE_PENDING   = 0;
    int STATE_ACTIVE    = 1;
    int STATE_COMPLETED = 2;
    int STATE_ABORTED   = 3;
    int STATE_SUSPENDED = 4;
    int STATE_ERROR = 4;
    
    /**
     * Returns process definition associated with this process instance
     * @return process definition of this process instance
     */
    Process<T> process();
    
    /**
     * Starts process instance
     */
    void start();
    
    /**
     * Sends given signal into this process instance
     * @param signal signal to be processed
     */
    <S> void send(Signal<S> signal);
    
    /**
     * Aborts this process instance
     */
    void abort();
    
    /**
     * Returns process variables of this process instance
     * @return variables of the process instance
     */
    T variables();
    
    /**
     * Returns current status of this process instance
     * @return
     */
    int status();
    
    /**
     * Completes work item belonging to this process instance with given variables
     * @param id id of the work item to complete
     * @param variables optional variables
     * @throws WorkItemNotFoundException in case work item with given id does not exist
     */
    void completeWorkItem(String id, Map<String, Object> variables);
    
    /**
     * Aborts work item belonging to this process instance
     * @param id id of the work item to complete
     * @throws WorkItemNotFoundException in case work item with given id does not exist
     */
    void abortWorkItem(String id);
    
    /**
     * Returns work item identified by given id if found
     * @param workItemId id of the work item 
     * @return work item with its parameters if found
     * @throws WorkItemNotFoundException in case work item with given id does not exist
     */
    WorkItem workItem(String workItemId);
    
    /**
     * Returns list of currently active work items.
     * @return non empty list of identifiers of currently active tasks.
     */
    List<WorkItem> workItems();
    
    /**
     * Returns identifier of this process instance
     * @return id of the process instance
     */
    String id();

}
