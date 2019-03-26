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

package org.jbpm.services.api;

import java.util.Collection;
import java.util.Map;

import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.UserTaskDefinition;
import org.kie.api.runtime.KieContainer;

/**
 * Provides details from definition point of view which is extracted from BPMN2 definitions.
 * Delivers information about:
 * <ul>
 * 	<li>process such as id, name, version</li>
 * 	<li>process variables (name and type)</li>
 * 	<li>defined reusable subprocesses</li>
 * 	<li>defined service tasks (domain specific services)</li>
 * 	<li>defined user tasks</li>
 *	<li>user task input and outputs</li> 	
 * </ul>
 */
public interface DefinitionService {
	
	/**
	 * Performs build operation for given bpmn2content to produce fully populated <code>ProcessDefinition</code>
	 * @param deploymentId identifier of deployment this process belongs to, 
	 * 			might be null if built definition does not need to be stored
	 * @param bpmn2Content actual BPMN xml content as string to be parsed and processed
	 * @param kieContainer the {@link KieContainer} instance that contains the deployment project: this should be used when 
	 *          parsing the BPMN2 in case custom classes or other project resources (processes, rules) are referenced
	 * @param cache indicates if the definition service should cache this <code>ProcessDefinition</code>
	 * @return fully populated <code>ProcessDefinition</code>
	 * @throws IllegalArgumentException in case build operation cannot be completed
	 */
	ProcessDefinition buildProcessDefinition(String deploymentId, String bpmn2Content,
			KieContainer kieContainer, boolean cache) throws IllegalArgumentException;
	
	void addProcessDefinition(String deploymentId, String processId, Object processDescriptor, KieContainer kieContainer);

	/**
	 * Returns previously built <code>ProcessDefinition</code>. 
	 * <br/>
	 * NOTE: This method assumes process has already been built by invoking <code>buildProcessDefinition</code> method
	 * @param deploymentId identifier of deployment that process belongs to
	 * @param processId identifier of the process
	 * @return returns complete <code>ProcessDefinition</code>
	 * @throws DeploymentNotFoundException in case deployment with given deploymentId cannot be found
	 * @throws ProcessDefinitionNotFoundException in case process definition with given processId cannot be found
	 */
	ProcessDefinition getProcessDefinition(String deploymentId, String processId);
    
	/**
	 * Returns collection of process identifiers of reusable processes used by given process.
	 * <br/>
	 * NOTE: This method assumes process has already been built by invoking <code>buildProcessDefinition</code> method
	 * @param deploymentId identifier of deployment that process belongs to
	 * @param processId identifier of the process
	 * @return returns collection of found reusable subprocess identifiers, maybe an empty list if none were found
	 */
    Collection<String> getReusableSubProcesses(String deploymentId, String processId);
    
    /**
     * Returns all process variables defined in the given process where:
     * <ul>
     * 	<li>key in the map is name of the process variable</li>
     * 	<li>value in the map is type of the process variable</li>
     * </ul>
     * <br/>
	 * NOTE: This method assumes process has already been built by invoking <code>buildProcessDefinition</code> method
     * @param deploymentId identifier of deployment that process belongs to
	 * @param processId identifier of the process
     * @return map of all process variables defined or empty map if none are found
     */
    Map<String, String> getProcessVariables(String deploymentId, String processId);
   
    /**
     * Returns a list of all referenced java classes defined in the given process.
     * <br/>
     * NOTE: This method assumes process has already been built by invoking <code>buildProcessDefinition</code> method
     * @param deploymentId identifier of deployment that process belongs to
     * @param processId identifier of the process
     * @return a list of all referenced classes defined or an empty list if none are found
     */
    Collection<String> getJavaClasses(String deploymentId, String processId);
   
    /**
     * Returns a list of all referenced rules used in the given process.
     * <br/>
     * NOTE: This method assumes process has already been built by invoking <code>buildProcessDefinition</code> method
     * @param deploymentId identifier of deployment that process belongs to
     * @param processId identifier of the process
     * @return a list of all referenced rules or an empty list if none are found
     */
    Collection<String> getRuleSets(String deploymentId, String processId);
    
    /**
     * Returns service (domain specific) tasks defined in the process where:
     * <ul>
     * 	<li>key in the map is name of the task node</li>
     * 	<li>value in the map is name of the domain specific service (name that handler should be registered with)</li>
     * </ul>
     * <br/>
	 * NOTE: This method assumes process has already been built by invoking <code>buildProcessDefinition</code> method
     * @param deploymentId identifier of deployment that process belongs to
	 * @param processId identifier of the process
     * @return returns map of all found service tasks or empty map if none are found
     */
    Map<String, String> getServiceTasks(String deploymentId, String processId);
    
    /**
     * Returns all organizational entities identifiers involved in the process - like users and groups.
     * Since this is based on definition and not runtime it can return references to process variables
     * <br/>
	 * NOTE: This method assumes process has already been built by invoking <code>buildProcessDefinition</code> method
     * @param deploymentId identifier of deployment that process belongs to
	 * @param processId identifier of the process
     * @return returns map of all found organizational entities grouped by task they are assigned to
     */
    Map<String, Collection<String>> getAssociatedEntities(String deploymentId, String processId);
    
    /**
     * Returns all user task definitions defined in given process
     * <br/>
	 * NOTE: This method assumes process has already been built by invoking <code>buildProcessDefinition</code> method
     * @param deploymentId identifier of deployment that process belongs to
	 * @param processId identifier of the process
     * @return returns collection of <code>UserTaskDefinition</code>s or empty collection if none were found
     */
    Collection<UserTaskDefinition> getTasksDefinitions(String deploymentId, String processId);
    
    /**
     * Returns map of data input defined for given user task
     * <ul>
     * 	<li>key in the map is identifier of dataInput</li>
     * 	<li>value in the map is name dataInput</li>
     * </ul>
     * <br/>
	 * NOTE: This method assumes process has already been built by invoking <code>buildProcessDefinition</code> method
     * @param deploymentId identifier of deployment that process belongs to
	 * @param processId identifier of the process
     * @param taskName name of a task the data input should be collected for
     * @return returns map of found data inputs or empty map if none were found
     */
    Map<String, String> getTaskInputMappings(String deploymentId, String processId, String taskName);
    
    /**
     * Returns map of data output defined for given user task
     * <ul>
     * 	<li>key in the map is identifier of dataOutput</li>
     * 	<li>value in the map is name dataOutput</li>
     * </ul>
     * <br/>
	 * NOTE: This method assumes process has already been built by invoking <code>buildProcessDefinition</code> method
     * @param deploymentId identifier of deployment that process belongs to
	 * @param processId identifier of the process
     * @param taskName name of a task the data output should be collected for
     * @return returns map of found data outputs or empty map if none were found
     */
    Map<String, String> getTaskOutputMappings(String deploymentId, String processId, String taskName);
	
}
