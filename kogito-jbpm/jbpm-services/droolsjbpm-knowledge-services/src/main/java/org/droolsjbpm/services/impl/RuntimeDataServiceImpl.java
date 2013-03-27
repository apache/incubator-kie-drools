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
package org.droolsjbpm.services.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.droolsjbpm.services.api.RuntimeDataService;
import org.droolsjbpm.services.impl.model.NodeInstanceDesc;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
import org.droolsjbpm.services.impl.model.VariableStateDesc;
import org.jboss.seam.transaction.Transactional;

import org.jbpm.services.task.wih.RuntimeFinder;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;

import org.jbpm.process.audit.NodeInstanceLog;


/**
 *
 * @author salaboy
 */
@ApplicationScoped
@Transactional
public class RuntimeDataServiceImpl implements RuntimeDataService, RuntimeFinder {

    
    @Inject
    private JbpmServicesPersistenceManager pm;

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    public Collection<ProcessDesc> getProcessesByDomainName(String domainName) {
        List<ProcessDesc> processes = (List<ProcessDesc>) pm.queryWithParametersInTransaction("getProcessesByDomainName", 
                pm.addParametersToMap("domainName", domainName));
        return processes;
    }
    
    public Collection<ProcessDesc> getProcessesByFilter(String filter) {
        List<ProcessDesc> processes = (List<ProcessDesc>) pm.queryWithParametersInTransaction("getProcessesByFilter", 
                pm.addParametersToMap("filter", filter+"%"));
        return processes;
    }

    public ProcessDesc getProcessById(String processId){
       return ((List<ProcessDesc>) pm.queryWithParametersInTransaction("getProcessById", 
                pm.addParametersToMap("processId", processId ))).get(0);
    
    }
    
    public Collection<ProcessDesc> getProcesses() {
        List<ProcessDesc> processes = (List<ProcessDesc>) pm.queryInTransaction("getProcesses");
        return processes;
    }

    public Collection<ProcessInstanceDesc> getProcessInstances() { 
        List<ProcessInstanceDesc> processInstances = (List<ProcessInstanceDesc>) pm.queryInTransaction("getProcessInstances");

        return processInstances;
    }
    
    public Collection<ProcessInstanceDesc> getProcessInstances(List<Integer> states, String initiator) { 
        
        List<ProcessInstanceDesc> processInstances = null; 
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("states", states);
        if (initiator == null) {

            processInstances = (List<ProcessInstanceDesc>) pm.queryWithParametersInTransaction("getProcessInstancesByStatus", params);
        } else {

            params.put("initiator", initiator);
            processInstances = (List<ProcessInstanceDesc>) pm.queryWithParametersInTransaction("getProcessInstancesByStatusAndInitiator", params); 
        }
        
        return processInstances;
        
    }

    public Collection<ProcessInstanceDesc> getProcessInstancesByDomainId(String domainId) {
        List<ProcessInstanceDesc> processInstances = (List<ProcessInstanceDesc>)pm.queryStringWithParametersInTransaction("getProcessInstancesByDomainId",
                pm.addParametersToMap("domainId", domainId));

        return processInstances;
    }


    public Collection<ProcessInstanceDesc> getProcessInstancesByProcessDefinition(String processDefId){
      List<ProcessInstanceDesc> processInstances = (List<ProcessInstanceDesc>)pm.queryWithParametersInTransaction("getProcessInstancesByProcessDefinition",
              pm.addParametersToMap("processDefId", processDefId));

        return processInstances;
    }
    
    public ProcessInstanceDesc getProcessInstanceById(long processId) {
        List<ProcessInstanceDesc> processInstances = (List<ProcessInstanceDesc>)pm.queryWithParametersInTransaction("getProcessInstanceById", 
                pm.addParametersToMap("processId", processId, "maxResults", 1));

        return processInstances.get(0);
   }

    
    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesByProcessId(
            List<Integer> states, String processId, String initiator) {
        List<ProcessInstanceDesc> processInstances = null; 
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("states", states);        
        params.put("processId", processId +"%");
        if (initiator == null) {
  
            processInstances = (List<ProcessInstanceDesc>) pm.queryWithParametersInTransaction("getProcessInstancesByProcessIdAndStatus", params);
        } else {
            params.put("initiator", initiator);
            
            processInstances = (List<ProcessInstanceDesc>) pm.queryWithParametersInTransaction("getProcessInstancesByProcessIdAndStatusAndInitiator", params);
        }
        return processInstances;

    }

    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesByProcessName(
            List<Integer> states, String processName, String initiator) {
        List<ProcessInstanceDesc> processInstances = null; 
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put("states", states);        
        params.put("processName", processName +"%");
        if (initiator == null) {
  
            processInstances = (List<ProcessInstanceDesc>) pm.queryWithParametersInTransaction("getProcessInstancesByProcessNameAndStatus", params);
        } else {
            params.put("initiator", initiator);
            
            processInstances = (List<ProcessInstanceDesc>) pm.queryWithParametersInTransaction("getProcessInstancesByProcessNameAndStatusAndInitiator", params);
        }
        return processInstances;
    }    

    public Collection<NodeInstanceDesc> getProcessInstanceHistory(String domainId, long processId) {
        return getProcessInstanceHistory(domainId, processId, false);
    }


    public Collection<NodeInstanceDesc> getProcessInstanceHistory(String domainId, long processId, boolean completed) {
        HashMap<String, Object> params = pm.addParametersToMap("processId", processId, "domainId", domainId);                
        if (completed) {
            params.put("type", NodeInstanceLog.TYPE_EXIT);
        } else {
            params.put("type", NodeInstanceLog.TYPE_ENTER);
        }

        List<NodeInstanceDesc> nodeInstances = (List<NodeInstanceDesc>) pm.queryWithParametersInTransaction("getProcessInstanceHistory", params);

        return nodeInstances;
    }

    public Collection<NodeInstanceDesc> getProcessInstanceFullHistory(String domainId, long processId) {
        List<NodeInstanceDesc> nodeInstances = (List<NodeInstanceDesc>)pm.queryWithParametersInTransaction("getProcessInstanceFullHistory", 
                pm.addParametersToMap("processId", processId, "domainId", domainId));

        return nodeInstances;
    }

    public Collection<NodeInstanceDesc> getProcessInstanceActiveNodes(String domainId, long processId) {
        
        List<NodeInstanceDesc> activeNodeInstances = (List<NodeInstanceDesc>) pm.queryWithParametersInTransaction("getProcessInstanceActiveNodes", 
                pm.addParametersToMap("processId", processId, "domainId", domainId));
        
        return activeNodeInstances;
    }
    

    public Collection<NodeInstanceDesc> getProcessInstanceCompletedNodes(String domainId, long processId) {
        List<NodeInstanceDesc> completedNodeInstances = (List<NodeInstanceDesc>) pm.queryWithParametersInTransaction("getProcessInstanceCompletedNodes", 
                pm.addParametersToMap("processId", processId, "domainId", domainId));

        return completedNodeInstances;
        
    }
    
    public Collection<VariableStateDesc> getVariablesCurrentState(long processInstanceId) {
        List<VariableStateDesc> variablesState = (List<VariableStateDesc>) pm.queryWithParametersInTransaction("getVariablesCurrentState", pm.addParametersToMap("processInstanceId", processInstanceId));

        return variablesState;
    }
    
    public Collection<VariableStateDesc> getVariableHistory(long processInstanceId, String variableId) {
        List<VariableStateDesc> variablesState = (List<VariableStateDesc>) pm.queryWithParametersInTransaction("getVariableHistory", 
                pm.addParametersToMap("processInstanceId", processInstanceId,"variableId", variableId));                

        return variablesState;
    }

    @Override
    public String findName(long id) {
        ProcessInstanceDesc piDesc = getProcessInstanceById(id);
        return piDesc.getDomainId();
    }



}
