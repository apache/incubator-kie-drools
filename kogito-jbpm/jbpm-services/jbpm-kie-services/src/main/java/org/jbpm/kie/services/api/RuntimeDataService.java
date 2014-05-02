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
package org.jbpm.kie.services.api;

import java.util.Collection;
import java.util.List;

import org.jbpm.kie.services.impl.model.NodeInstanceDesc;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;
import org.jbpm.kie.services.impl.model.VariableStateDesc;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * This service provides an interface to retrieve data about the runtime, including the following:<ul>
 * <li>process instances</li>
 * <li>process definitions</li>
 * <li>node instance information</li>
 * <li>variable information</li>
 * </ul>
 */
public interface RuntimeDataService {
  
    // Process instance information
    
    /**
     * @return A list of {@link ProcessInstanceDesc} instances representing the available process instances.
     */
    Collection<ProcessInstanceDesc> getProcessInstances();
   
    /**
     * @param states A list of possible state (int) values that the {@link ProcessInstance} can have. 
     * @param initiator The initiator of the {@link ProcessInstance}.
     * @return A list of {@link ProcessInstanceDesc} instances representing the process instances that match
     *         the given criteria (states and inititator).
     */
    Collection<ProcessInstanceDesc> getProcessInstances(List<Integer> states, String initiator);
   
    /**
     * @param states A list of possible state (int) values that the {@link ProcessInstance} can have. 
     * @param processId The id of the {@link Process} (definition) used when starting the process instance. 
     * @param initiator The initiator of the {@link ProcessInstance}.
     * @return A list of {@link ProcessInstanceDesc} instances representing the process instances that match
     *         the given criteria (states, processId, and inititator).
     */
    Collection<ProcessInstanceDesc> getProcessInstancesByProcessId(List<Integer> states, String processId, String initiator);
   
    /**
     * @param states A list of possible state (int) values that the {@link ProcessInstance} can have. 
     * @param processName The name (not id!) of the {@link Process} (definition) used when starting the process instance. 
     * @param initiator The initiator of the {@link ProcessInstance}.
     * @return A list of {@link ProcessInstanceDesc} instances representing the process instances that match
     *         the given criteria (states, processName and inititator).
     */
    Collection<ProcessInstanceDesc> getProcessInstancesByProcessName(List<Integer> states, String processName, String initiator);
    
    /**
     * @param deploymentId The deployment id of the runtime. 
     * @param states A list of possible state (int) values that the {@link ProcessInstance} can have. 
     * @return A list of {@link ProcessInstanceDesc} instances representing the process instances that match
     *         the given criteria (deploymentId and states).
     */
    Collection<ProcessInstanceDesc> getProcessInstancesByDeploymentId(String deploymentId, List<Integer> states);
    
    /**
     * @param processId The id of the process (definition) used to start the {@link ProcessInstance}.
     * @return Process instance information, in the form of a {@link ProcessInstanceDesc} instance.
     */
    ProcessInstanceDesc getProcessInstanceById(long processId);
    
    /**
     * @param processDefId The id of the process (definition) 
     * @return A list of {@link ProcessInstanceDesc} instances representing the process instances that match
     *         the given criteria (deploymentId and states).
     */
    Collection<ProcessInstanceDesc> getProcessInstancesByProcessDefinition(String processDefId);

    
    // Node and Variable instance information
   

    /**
     *  (difference between this and getProcessInstanceActiveNodes(..)?) 
     * @param deploymentId
     * @param processId
     * @return
     */
    Collection<NodeInstanceDesc> getProcessInstanceHistory(String deploymentId, long processId);

    /**
     *  (difference between this and getProcessInstanceActiveNodes(..)/getProcessInstanceCompletedNodes(..) ?) 
     * @param deploymentId
     * @param processId
     * @param completed
     * @return
     */
    Collection<NodeInstanceDesc> getProcessInstanceHistory(String deploymentId, long processId, boolean completed);

    /**
     * @param deploymentId The id of the deployment (runtime).
     * @param processId The id of the process used to start the process instance.
     * @return The {@link NodeInstance} information, in the form of a list of {@link NodeInstanceDesc} instances, 
     *         that comes from a process instance that matches the given criteria (deploymentId, processId).
     */
    Collection<NodeInstanceDesc> getProcessInstanceFullHistory(String deploymentId, long processId);

    /**
     *  (difference between this and getProcessInstanceHistory(..) methods?)
     * @param deploymentId
     * @param processId
     * @return
     */
    Collection<NodeInstanceDesc> getProcessInstanceActiveNodes(String deploymentId, long processId);

    /**
     *  (difference between this and getProcessInstanceHistory(..) methods?)
     * @param deploymentId
     * @param processId
     * @return
     */
    Collection<NodeInstanceDesc> getProcessInstanceCompletedNodes(String deploymentId, long processId);

    /**
     * 
     * @param processInstanceId The process instance id.
     * @return Information about variables in the specified process instance, 
     *         represented by a list of {@link VariableStateDesc} instances.
     */
    Collection<VariableStateDesc> getVariablesCurrentState(long processInstanceId);

    /**
     * 
     * @param processInstanceId The process instance id.
     * @param variableId The id of the variable
     * @return Information about the variable with the given id in the specified process instance, 
     *         represented by a list of {@link VariableStateDesc} instances.
     */
    Collection<VariableStateDesc> getVariableHistory(long processInstanceId, String variableId);

    
    // Process information
  
    
    /**
     * @param deploymentId The deployment id of the runtime. 
     * @return A list of {@link ProcessAssetDesc} instances representing processes that match
     *         the given criteria (deploymentId)
     */
    Collection<ProcessAssetDesc> getProcessesByDeploymentId(String deploymentId);   
    
    /**
     * @param filter A regular expression.
     * @return A list of {@link ProcessAssetDesc} instances whose name or id matches the given regular expression.
     */
    Collection<ProcessAssetDesc> getProcessesByFilter(String filter);

    /**
     * @return A list of all available processes, in the form a of a list of {@link ProcessAssetDesc} instances.
     */
    Collection<ProcessAssetDesc> getProcesses();
   
    /**
     * @param deploymentId The deployment id of the runtime.
     * @return A list of all available process id's for a particular deployment/runtime.
     */
    Collection<String> getProcessIds(String deploymentId);
   
    /**
    * @param processId The id of the process
    * @return A {@link ProcessAssetDesc} instance, representing the {@link Process} 
    *         with the specified (process) id. 
    */
    ProcessAssetDesc getProcessById(String processId);
  
    /**
    * @param deploymentId The id of the deployment (runtime)
    * @param processId The id of the process
    * @return A {@link ProcessAssetDesc} instance, representing the {@link Process} 
    *         that is present in the specified deployment with the specified (process) id. 
    */
    ProcessAssetDesc getProcessesByDeploymentIdProcessId(String deploymentId, String processId);
}
