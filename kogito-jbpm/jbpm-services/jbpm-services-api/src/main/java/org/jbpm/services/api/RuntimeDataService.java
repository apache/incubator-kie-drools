/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.services.api;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.api.model.VariableDesc;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryContext;
import org.kie.internal.query.QueryFilter;

/**
 * This service provides an interface to retrieve data about the runtime, including the following:
 * <ul>
 * 	<li>process instances</li>
 * 	<li>process definitions</li>
 * 	<li>node instance information</li>
 * 	<li>variable information</li>
 * </ul>
 */
public interface RuntimeDataService {
	/**
	 * Represents type of node instance log entries.
	 *
	 */
	enum EntryType {
		 START(0),
		 END(1);
		 
		 private int value;
		 
		 private EntryType(int value) {
			 this.value = value;
		 }

		public int getValue() {
			return value;
		}
	}
  
    // Process instance information
    
    /**
     * @return A list of {@link ProcessInstanceDesc} instances representing the available process instances.
     */
    Collection<ProcessInstanceDesc> getProcessInstances(QueryContext queryContext);
   
    /**
     * @param states A list of possible state (int) values that the {@link ProcessInstance} can have. 
     * @param initiator The initiator of the {@link ProcessInstance}.
     * @return A list of {@link ProcessInstanceDesc} instances representing the process instances that match
     *         the given criteria (states and initiator).
     */
    Collection<ProcessInstanceDesc> getProcessInstances(List<Integer> states, String initiator, QueryContext queryContext);
   
    /**
     * @param states A list of possible state (int) values that the {@link ProcessInstance} can have. 
     * @param processId The id of the {@link Process} (definition) used when starting the process instance. 
     * @param initiator The initiator of the {@link ProcessInstance}.
     * @return A list of {@link ProcessInstanceDesc} instances representing the process instances that match
     *         the given criteria (states, processId, and initiator).
     */
    Collection<ProcessInstanceDesc> getProcessInstancesByProcessId(List<Integer> states, String processId, String initiator, QueryContext queryContext);
   
    /**
     * @param states A list of possible state (int) values that the {@link ProcessInstance} can have. 
     * @param processName The name (not id!) of the {@link Process} (definition) used when starting the process instance. 
     * @param initiator The initiator of the {@link ProcessInstance}.
     * @return A list of {@link ProcessInstanceDesc} instances representing the process instances that match
     *         the given criteria (states, processName and initiator).
     */
    Collection<ProcessInstanceDesc> getProcessInstancesByProcessName(List<Integer> states, String processName, String initiator, QueryContext queryContext);
    
    /**
     * @param deploymentId The deployment id of the runtime. 
     * @param states A list of possible state (int) values that the {@link ProcessInstance} can have. 
     * @return A list of {@link ProcessInstanceDesc} instances representing the process instances that match
     *         the given criteria (deploymentId and states).
     */
    Collection<ProcessInstanceDesc> getProcessInstancesByDeploymentId(String deploymentId, List<Integer> states, QueryContext queryContext);
    
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
    Collection<ProcessInstanceDesc> getProcessInstancesByProcessDefinition(String processDefId, QueryContext queryContext);

    
    // Node and Variable instance information
   
    /**
     * Returns active node instance descriptor for given work item id, if exists.
     * @param workItemId identifier of the work item
     * @return returns node instance desc for work item if exists and is still active, otherwise null
     */
    NodeInstanceDesc getNodeInstanceForWorkItem(Long workItemId);

    /**
     * Returns trace of all active nodes for given process instance id
     * @param deploymentId unique identifier of the deployment unit
     * @param processInstanceId unique identifier of process instance
     * @return
     */
    Collection<NodeInstanceDesc> getProcessInstanceHistoryActive(long processInstanceId, QueryContext queryContext);

    /**
     * Returns trace of all executed (completed) for given process instance id
     * @param deploymentId unique identifier of the deployment unit
     * @param processInstanceId unique identifier of process instance
     * @return
     */
    Collection<NodeInstanceDesc> getProcessInstanceHistoryCompleted(long processInstanceId, QueryContext queryContext);

    /**
     * Returns complete trace of all executed (completed) and active nodes for given process instance id
     * @param deploymentId The id of the deployment (runtime).
     * @param processInstanceId The id of the process used to start the process instance.
     * @return The {@link NodeInstance} information, in the form of a list of {@link NodeInstanceDesc} instances, 
     *         that comes from a process instance that matches the given criteria (deploymentId, processId).
     */
    Collection<NodeInstanceDesc> getProcessInstanceFullHistory(long processInstanceId, QueryContext queryContext);
    
    /**
     * Returns complete trace of all events of given type (START or END) for given process instance.
     * @param deploymentId The id of the deployment (runtime).
     * @param processInstanceId The id of the process used to start the process instance.
     * @param type type of events that shall be returned (START or END) - to return both use {@link #getProcessInstanceFullHistory(String, long)}
     * @return
     */
    Collection<NodeInstanceDesc> getProcessInstanceFullHistoryByType(long processInstanceId, EntryType type, QueryContext queryContext);


    /**
     * Returns collections of all process variables current value for given process instance
     * @param processInstanceId The process instance id.
     * @return Information about variables in the specified process instance, 
     *         represented by a list of {@link VariableStateDesc} instances.
     */
    Collection<VariableDesc> getVariablesCurrentState(long processInstanceId);

    /**
     * Returns collection of changes to given variable within scope of process instance
     * @param processInstanceId The process instance id.
     * @param variableId The id of the variable
     * @return Information about the variable with the given id in the specified process instance, 
     *         represented by a list of {@link VariableStateDesc} instances.
     */
    Collection<VariableDesc> getVariableHistory(long processInstanceId, String variableId, QueryContext queryContext);

    
    // Process information
  
    
    /**
     * @param deploymentId The deployment id of the runtime. 
     * @return A list of {@link ProcessAssetDesc} instances representing processes that match
     *         the given criteria (deploymentId)
     */
    Collection<ProcessDefinition> getProcessesByDeploymentId(String deploymentId, QueryContext queryContext);   
    
    /**
     * @param filter A regular expression.
     * @return A list of {@link ProcessAssetDesc} instances whose name or id matches the given regular expression.
     */
    Collection<ProcessDefinition> getProcessesByFilter(String filter, QueryContext queryContext);

    /**
     * @return A list of all available processes, in the form a of a list of {@link ProcessAssetDesc} instances.
     */
    Collection<ProcessDefinition> getProcesses(QueryContext queryContext);
   
    /**
     * @param deploymentId The deployment id of the runtime.
     * @return A list of all available process id's for a particular deployment/runtime.
     */
    Collection<String> getProcessIds(String deploymentId, QueryContext queryContext);
   
    /**
    * @param processId The id of the process
    * @return A {@link ProcessAssetDesc} instance, representing the {@link Process} 
    *         with the specified (process) id. 
    */
    ProcessDefinition getProcessById(String processId);
  
    /**
    * @param deploymentId The id of the deployment (runtime)
    * @param processId The id of the process
    * @return A {@link ProcessAssetDesc} instance, representing the {@link Process} 
    *         that is present in the specified deployment with the specified (process) id. 
    */
    ProcessDefinition getProcessesByDeploymentIdProcessId(String deploymentId, String processId);
    
	// user task query operations

	/**
	 * Return a task by its workItemId.
	 * 
	 * @param workItemId
	 * @return
	 */
    UserTaskInstanceDesc getTaskByWorkItemId(Long workItemId);

	/**
	 * Return a task by its taskId.
	 * 
	 * @param taskId
	 * @return
	 */
	UserTaskInstanceDesc getTaskById(Long taskId);

	/**
	 * Return a list of assigned tasks as a Business Administrator. Business
	 * administrators play the same role as task stakeholders but at task type
	 * level. Therefore, business administrators can perform the exact same
	 * operations as task stakeholders. Business administrators may also observe
	 * the progress of notifications.
	 * 
	 * @param userId
	 * @param filter
	 * @return
	 */
	List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, QueryFilter filter);

	/**
	 * Return a list of tasks the user is eligible for.
	 * 
	 * @param userId
	 * @param filter
	 * @return
	 */
	List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, QueryFilter filter);
	
	/**
	 * Return a list of tasks the user or groups are eligible for.
	 * 
	 * @param userId
	 * @param groupIds
	 * @param filter
	 * @return
	 */
	List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, QueryFilter filter);

	/**
	 * Return a list of tasks the user is eligible for with one of the listed
	 * statuses.
	 * 
	 * @param userId
	 * @param status
	 * @param filter
	 * @return
	 */
	List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, QueryFilter filter);
	
	/**
	 * Return a list of tasks the user or groups are eligible for with one of the listed
	 * statuses.
	 * @param userId
	 * @param groupIds
	 * @param status
	 * @param filter
	 * @return
	 */
	List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, List<Status> status, QueryFilter filter);
	
	/**
	 * Return a list of tasks the user is eligible for with one of the listed
	 * statuses and expiration date starting at <code>from</code>. Tasks that do not have expiration date set
	 * will also be included in the result set.
	 * 
	 * @param userId
	 * @param status
	 * @param from
	 * @param filter
	 * @return
	 */
	List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<Status> status, Date from, QueryFilter filter);
	
	/**
	 * Return a list of tasks the user has claimed with one of the listed
	 * statuses and expiration date starting at <code>from</code>. Tasks that do not have expiration date set
	 * will also be included in the result set.
	 * 
	 * @param userId
	 * @param status
	 * @param from
	 * @param filter
	 * @return
	 */
	List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> strStatuses, Date from, QueryFilter filter);

	/**
	 * Return a list of tasks the user has claimed.
	 * 
	 * @param userId
	 * @param filter
	 * @return
	 */
	List<TaskSummary> getTasksOwned(String userId, QueryFilter filter);

	/**
	 * Return a list of tasks the user has claimed with one of the listed
	 * statuses.
	 * 
	 * @param userId
	 * @param status
	 * @param filter
	 * @return
	 */
	List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status, QueryFilter filter);

	/**
	 * Get a list of tasks the Process Instance is waiting on.
	 * 
	 * @param processInstanceId
	 * @return
	 */
	List<Long> getTasksByProcessInstanceId(Long processInstanceId);

	/**
	 * Get a list of tasks the Process Instance is waiting on with one of the
	 * listed statuses.
	 * 
	 * @param processInstanceId
	 * @param status
	 * @param filter
	 * @return
	 */
	List<TaskSummary> getTasksByStatusByProcessInstanceId(Long processInstanceId, List<Status> status, QueryFilter filter);
	    
}
