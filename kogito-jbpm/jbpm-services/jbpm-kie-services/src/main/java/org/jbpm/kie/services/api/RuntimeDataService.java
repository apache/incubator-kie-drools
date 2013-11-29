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

/**
 *
 * @author salaboy
 */
public interface RuntimeDataService {
    
    Collection<ProcessInstanceDesc> getProcessInstances();
    
    Collection<ProcessInstanceDesc> getProcessInstances(List<Integer> states, String initiator);
    
    Collection<ProcessInstanceDesc> getProcessInstancesByProcessId(List<Integer> states, String processId, String initiator);
    
    Collection<ProcessInstanceDesc> getProcessInstancesByProcessName(List<Integer> states, String processName, String initiator);

    Collection<ProcessInstanceDesc> getProcessInstancesByDeploymentId(String deploymentId, List<Integer> states);

    Collection<ProcessAssetDesc> getProcessesByDeploymentId(String deploymentId);   
    
    Collection<ProcessAssetDesc> getProcessesByFilter(String filter);

    Collection<ProcessAssetDesc> getProcesses();
    
    ProcessInstanceDesc getProcessInstanceById(long processId);
    
    Collection<NodeInstanceDesc> getProcessInstanceHistory(String deploymentId, long processId);
    
    Collection<NodeInstanceDesc> getProcessInstanceHistory(String deploymentId, long processId, boolean completed);
    
    Collection<NodeInstanceDesc> getProcessInstanceFullHistory(String deploymentId, long processId);
    
    Collection<NodeInstanceDesc> getProcessInstanceActiveNodes(String deploymentId, long processId);
    
    Collection<NodeInstanceDesc> getProcessInstanceCompletedNodes(String deploymentId, long processId);
    
    Collection<VariableStateDesc> getVariablesCurrentState(long processInstanceId);
    
    Collection<VariableStateDesc> getVariableHistory(long processInstanceId, String variableId);

    Collection<ProcessInstanceDesc> getProcessInstancesByProcessDefinition(String processDefId);

    ProcessAssetDesc getProcessById(String processId);
    
    ProcessAssetDesc getProcessesByDeploymentIdProcessId(String deploymentId, String processId);
}
