/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime.manager.audit;

import java.util.List;

/**
 * AuditService provides access to active and already completed process (and its components) data.
 * Delivers data about:
 * <ul>
 * 	<li>Process instances</li>
 * 	<li>Node instances</li>
 * 	<li>Variable instances</li>	
 * </ul>
 * there might be some limitations in various implementations thus some of the methods might throw
 * <code>UnsupportedOperationException</code>
 */
public interface AuditService {

	/**
	 * Returns all known process instances currently available to the audit service
	 * @return all process instance logs or empty list if none were found
	 */
    public List<? extends ProcessInstanceLog> findProcessInstances();

    /**
     * Returns all known process instances for given process id
     * @param processId identifier of the process definition
     * @return all process instance logs for given process id or empty list if none were found
     */
    public List<? extends ProcessInstanceLog> findProcessInstances(String processId);

    /**
     * Returns all active process instances for given process id
     * @param processId identifier of the process definition
     * @return all active process instance logs for given process id or empty list if none were found
     */
    public List<? extends ProcessInstanceLog> findActiveProcessInstances(String processId);

    /**
     * Returns process instance log for given process instance id
     * @param processInstanceId unique identifier of process instance
     * @return process instance log for given process instance id or null if not found
     */
    public ProcessInstanceLog findProcessInstance(long processInstanceId);

    /**
     * Returns all known subprocess instance logs for given process instance id - considered parent process instance id
     * @param processInstanceId identifier of the parent process instance id
     * @return all process instance logs that are subprocess to a given process instance id
     */
    public List<? extends ProcessInstanceLog> findSubProcessInstances(long processInstanceId);

    /**
     * Returns all node instances that were already triggered for given process instance id
     * @param processInstanceId unique identifier of process instance
     * @return all node instance logs for given process instance id
     */
    public List<? extends NodeInstanceLog> findNodeInstances(long processInstanceId);

    /**
     * Returns all node instances that were already triggered for given process instance id and node identifier
     * @param processInstanceId unique identifier of process instance
     * @param nodeId node identifier - by default it should be unique id (from process definition) 
     * 		  but if not available regular node id shall be used 
     * @return
     */
    public List<? extends NodeInstanceLog> findNodeInstances(long processInstanceId, String nodeId);

    /**
     * Returns all variable logs for given process instance id
     * @param processInstanceId unique identifier of process instance
     * @return all variables logs for given process instance or empty list of none were found
     */
    public List<? extends VariableInstanceLog> findVariableInstances(long processInstanceId);

    /**
     * Returns all variable logs for given process instance id and variable identifier
     * @param processInstanceId unique identifier of process instance
     * @param variableId variable name
     * @return all variable logs for given process instance and variable identifier or empty list if none were found
     */
    public List<? extends VariableInstanceLog> findVariableInstances(long processInstanceId, String variableId);

    /**
     * Returns all variable logs that are identified by variable id regardless of what process instance they belong to
     * @param variableId variable name
     * @param onlyActiveProcesses indicates if only active process instances should be considered or both active and completed
     * @return all variable logs for given variable id or empty list if none were found
     */
    public List<? extends VariableInstanceLog> findVariableInstancesByName(String variableId, boolean onlyActiveProcesses);
    
    /**
     * Returns all variable logs that are identified by variable id and has given value
     * regardless of what process instance they belong to
     * @param variableId variable name
     * @param value value of the variable
     * @param onlyActiveProcesses indicates if only active process instances should be considered or both active and completed
     * @return all variable logs for given variable id and its value matches given value or empty list if none were found
     */
    public List<? extends VariableInstanceLog> findVariableInstancesByNameAndValue(String variableId, String value, boolean onlyActiveProcesses);
    
    /**
     * Removes all entries from audit data store
     */
    public void clear();
    
    /**
     * Indicates that work with this instance of <code>AuditService</code> is completed and can be disposed (release resources)
     */
    public void dispose();
}
