/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.process.audit;

import java.util.List;

import org.kie.api.runtime.Environment;

/**
 * This class is essentially a very simple implementation of a service
 * that deals with {@link ProcessInstanceLog}, {@link NodeInstanceLog} 
 * and {@link VariableInstanceLog} entities. 
 * </p>
 * Please see the public methods for the interface of this service. 
 */
public interface AuditLogService {

    /**
     * Service methods
     * @return
     */

    public List<ProcessInstanceLog> findProcessInstances();

    public List<ProcessInstanceLog> findProcessInstances(String processId);

    public List<ProcessInstanceLog> findActiveProcessInstances(String processId);

    public ProcessInstanceLog findProcessInstance(long processInstanceId);

    public List<ProcessInstanceLog> findSubProcessInstances(long processInstanceId);

    public List<NodeInstanceLog> findNodeInstances(long processInstanceId);

    public List<NodeInstanceLog> findNodeInstances(long processInstanceId, String nodeId);

    public List<VariableInstanceLog> findVariableInstances(long processInstanceId);

    public List<VariableInstanceLog> findVariableInstances(long processInstanceId, String variableId);

    public List<VariableInstanceLog> findVariableInstancesByName(String variableId, boolean onlyActiveProcesses);
    
    public List<VariableInstanceLog> findVariableInstancesByNameAndValue(String variableId, String value, boolean onlyActiveProcesses);
    
    public void clear();

    public void dispose();

}