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
package org.droolsjbpm.services.api;

import java.util.Collection;
import java.util.List;

import org.droolsjbpm.services.impl.model.NodeInstanceDesc;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
import org.droolsjbpm.services.impl.model.VariableStateDesc;

/**
 *
 * @author salaboy
 */
public interface RuntimeDataService {
    
    Collection<ProcessInstanceDesc> getProcessInstances();
    
    Collection<ProcessInstanceDesc> getProcessInstances(List<Integer> states, String initiator);
    
    Collection<ProcessInstanceDesc> getProcessInstancesByProcessId(List<Integer> states, String processId, String initiator);
    
    Collection<ProcessInstanceDesc> getProcessInstancesByProcessName(List<Integer> states, String processName, String initiator);

    Collection<ProcessInstanceDesc> getProcessInstancesByDomainId(String domainId);

    Collection<ProcessDesc> getProcessesByDomainName(String domainId);   
    
    Collection<ProcessDesc> getProcessesByFilter(String filter);

    Collection<ProcessDesc> getProcesses();
    
    ProcessInstanceDesc getProcessInstanceById(long processId);
    
    Collection<NodeInstanceDesc> getProcessInstanceHistory(String domainId, long processId);
    
    Collection<NodeInstanceDesc> getProcessInstanceHistory(String domainId, long processId, boolean completed);
    
    Collection<NodeInstanceDesc> getProcessInstanceFullHistory(String domainId, long processId);
    
    Collection<NodeInstanceDesc> getProcessInstanceActiveNodes(String domainId, long processId);
    
    Collection<NodeInstanceDesc> getProcessInstanceCompletedNodes(String domainId, long processId);
    
    Collection<VariableStateDesc> getVariablesCurrentState(long processInstanceId);
    
    Collection<VariableStateDesc> getVariableHistory(long processInstanceId, String variableId);

    Collection<ProcessInstanceDesc> getProcessInstancesByProcessDefinition(String processDefId);

    ProcessDesc getProcessById(String processId);
}
