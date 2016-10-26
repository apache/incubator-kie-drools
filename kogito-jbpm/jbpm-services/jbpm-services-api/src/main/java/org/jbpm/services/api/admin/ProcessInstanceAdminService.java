/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.services.api.admin;

import java.util.Collection;

import org.jbpm.services.api.NodeInstanceNotFoundException;
import org.jbpm.services.api.NodeNotFoundException;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.NodeInstanceDesc;

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
    
}
