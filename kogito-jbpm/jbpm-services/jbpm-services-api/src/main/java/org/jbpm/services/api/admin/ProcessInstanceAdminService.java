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

package org.jbpm.services.api.admin;

import java.util.Collection;
import java.util.List;

import org.jbpm.services.api.NodeInstanceNotFoundException;
import org.jbpm.services.api.NodeNotFoundException;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.runtime.error.ExecutionError;

/**
 * Administrative operations for process instances to allow runtime modifications
 * to active process instances.
 *
 */
public interface ProcessInstanceAdminService {
    
    /**
     * Returns all process nodes found in given process instance.
     * @param processInstanceId unique id of process instance
     * @return list of process nodes in process instance
     * @throws ProcessInstanceNotFoundException in case process instance id with given id was not found
     */
    Collection<ProcessNode> getProcessNodes(long processInstanceId) throws ProcessInstanceNotFoundException;

    /**
     * Cancels node instance with given id within given process instance.
     * @param processInstanceId unique id of process instance
     * @param nodeInstanceId node instance id within given process instance to be canceled
     * @throws NodeInstanceNotFoundException in case node instance with given id is not active
     * @throws ProcessInstanceNotFoundException in case process instance id with given id was not found
     */
    void cancelNodeInstance(long processInstanceId, long nodeInstanceId) throws NodeInstanceNotFoundException, ProcessInstanceNotFoundException;
    
    /**
     * Retriggers (which includes cancel) node instance given with node instance id within process instance.
     * @param processInstanceId unique id of process instance
     * @param nodeInstanceId node instance id within given process instance to be retriggered
     * @throws NodeInstanceNotFoundException in case node instance with given id is not active
     * @throws ProcessInstanceNotFoundException in case process instance id with given id was not found
     */
    void retriggerNodeInstance(long processInstanceId, long nodeInstanceId) throws NodeInstanceNotFoundException, ProcessInstanceNotFoundException;

    /**
     * Returns all active node instances in given process instance;
     * @param processInstanceId unique id of process instance
     * @return found active node instances
     * @throws ProcessInstanceNotFoundException in case process instance id with given id was not found
     */
    Collection<NodeInstanceDesc> getActiveNodeInstances(long processInstanceId) throws ProcessInstanceNotFoundException;
    
    /**
     * Updates active timer identified by timer id with delay that is given in seconds. Delay is absolute meaning it is as it would be set when timer was created. <br/>
     * Example:<br/>
     * In case timer was initially created with delay of 1 hour and after 30 min we decide to update it to 2 hours it will then expire in 1,5 hour from the time it was updated. 
     * @param processInstanceId unique id of process instance
     * @param timerId timer id that should be updated
     * @param delay absolute delay in seconds 
     * @param period in case of repeatable timer how often it should repeat in milliseconds - if not applicable should be set to -1
     * @param repeatLimit in case of repeatable timer how many times it should trigger - if not applicable should be set to -1  
     * @throws NodeInstanceNotFoundException in case node instance with given id is not active
     * @throws ProcessInstanceNotFoundException in case process instance id with given id was not found
     */
    void updateTimer(long processInstanceId, long timerId, long delay, long period, int repeatLimit) throws NodeInstanceNotFoundException, ProcessInstanceNotFoundException;

    /**
     * Updates active timer identified by timer id with delay that is given in seconds and is relative to current time. <br/>
     * Example:<br/>
     * In case timer was initially created with delay of 1 hour and after 30 min we decide to update it to 2 hours it will then expire in 2 hours from the time it was updated. 
     * @param processInstanceId unique id of process instance
     * @param timerId timer id that should be updated
     * @param delay absolute delay in seconds 
     * @param period in case of repeatable timer how often it should repeat in milliseconds - if not applicable should be set to -1
     * @param repeatLimit in case of repeatable timer how many times it should trigger - if not applicable should be set to -1  
     * @throws NodeInstanceNotFoundException in case node instance with given id is not active
     * @throws ProcessInstanceNotFoundException in case process instance id with given id was not found
     */
    void updateTimerRelative(long processInstanceId, long timerId, long delay, long period, int repeatLimit) throws NodeInstanceNotFoundException, ProcessInstanceNotFoundException;
    
    /**
     * Returns active timers for given process instance.
     * @param processInstanceId unique id of process instance
     * @return list of timer found for given process instance
     * @throws ProcessInstanceNotFoundException in case process instance id with given id was not found
     */
    Collection<TimerInstance> getTimerInstances(long processInstanceId) throws ProcessInstanceNotFoundException;
    
    /**
     * Triggers node to create new node instance with node id within process instance. 
     * This results in new instance of a task within process instance.
     * @param processInstanceId unique id of process instance
     * @param nodeId node id to be triggered (new instance of that node to be created)
     * @throws NodeNotFoundException in case node with given id does not exist in process instance
     * @throws ProcessInstanceNotFoundException in case process instance id with given id was not found
     */
    void triggerNode(long processInstanceId, long nodeId) throws NodeNotFoundException, ProcessInstanceNotFoundException;
    
    /**
     * Acknowledge given error that it was reviewed and understood
     * @param errorId unique id of the error
     * @throws ExecutionErrorNotFoundException thrown when there is no unacknowledged error with that id
     */
    void acknowledgeError(String... errorId) throws ExecutionErrorNotFoundException;
    
    /**
     * Returns execution error identified by given error id
     * @param errorId unique id of the error
     * @return returns execution error instance
     * @throws ExecutionErrorNotFoundException is thrown in case no error was found for given error id
     */
    ExecutionError getError(String errorId) throws ExecutionErrorNotFoundException;
    
    /**
     * Returns execution errors for given process id and deployment id
     * @param deploymentId deployment id that contains given process
     * @param processId process id of the process
     * @param includeAcknowledged indicates whether to include acknowledged errors or not
     * @param queryContext control parameters for pagination 
     * @return list of found errors
     */
    List<ExecutionError> getErrorsByProcessId(String deploymentId, String processId, boolean includeAcknowledged, QueryContext queryContext);
    
    /**
     * Returns execution errors for given process instance id
     * @param processInstanceId process instance id of the process
     * @param includeAcknowledged indicates whether to include acknowledged errors or not
     * @param queryContext control parameters for pagination 
     * @return list of found errors
     */
    List<ExecutionError> getErrorsByProcessInstanceId(long processInstanceId, boolean includeAcknowledged, QueryContext queryContext);
    
    /**
     * Returns execution errors for given process instance id and node
     * @param processInstanceId process instance id of the process
     * @param nodeName name of the node that error should be found for
     * @param includeAcknowledged indicates whether to include acknowledged errors or not
     * @param queryContext control parameters for pagination 
     * @return list of found errors
     */
    List<ExecutionError> getErrorsByProcessInstanceId(long processInstanceId, String nodeName, boolean includeAcknowledged, QueryContext queryContext);
    
    /**
     * Returns all execution errors regardless of their type
     * @param includeAcknowledged indicates whether to include acknowledged errors or not
     * @param queryContext control parameters for pagination
     * @return list of found errors
     */
    List<ExecutionError> getErrors(boolean includeAcknowledged, QueryContext queryContext);
    
}
