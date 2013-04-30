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
package org.jbpm.kie.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.event.Deploy;
import org.jbpm.kie.services.impl.event.DeploymentEvent;
import org.jbpm.kie.services.impl.event.Undeploy;
import org.jbpm.kie.services.impl.model.NodeInstanceDesc;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;
import org.jbpm.kie.services.impl.model.VariableStateDesc;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.services.task.wih.RuntimeFinder;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;


/**
 *
 * @author salaboy
 */
@ApplicationScoped
@Transactional
public class RuntimeDataServiceImpl implements RuntimeDataService, RuntimeFinder {

    @Inject
    private DeploymentService deploymentService;
    @Inject
    private JbpmServicesPersistenceManager pm;
    
    private Set<ProcessDesc> availableProcesses = new HashSet<ProcessDesc>();
    
    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }
    
    public void indexOnDeploy(@Observes@Deploy DeploymentEvent event) {
        Collection<ProcessDesc> assets = deploymentService.getDeployedUnit(event.getDeploymentId()).getDeployedAssets();
        availableProcesses.addAll(assets);
    }
    
    public void removeOnUnDeploy(@Observes@Undeploy DeploymentEvent event) {
        Collection<ProcessDesc> outputCollection = new HashSet<ProcessDesc>();
        CollectionUtils.select(availableProcesses, new ByDeploymentIdPredicate(event.getDeploymentId()), outputCollection);
        
        availableProcesses.removeAll(outputCollection);
    }

    public Collection<ProcessDesc> getProcessesByDeploymentId(String deploymentId) {
        Collection<ProcessDesc> outputCollection = new HashSet<ProcessDesc>();
        CollectionUtils.select(availableProcesses, new ByDeploymentIdPredicate(deploymentId), outputCollection);
        
        return Collections.unmodifiableCollection(outputCollection);
    }
    
    public Collection<ProcessDesc> getProcessesByFilter(String filter) {
        Collection<ProcessDesc> outputCollection = new HashSet<ProcessDesc>();
        CollectionUtils.select(availableProcesses, new RegExPredicate("^.*"+filter+".*$"), outputCollection);
        return Collections.unmodifiableCollection(outputCollection);
    }

    public ProcessDesc getProcessById(String processId){
        
        Collection<ProcessDesc> outputCollection = new HashSet<ProcessDesc>();
        CollectionUtils.select(availableProcesses, new ByProcessIdPredicate(processId), outputCollection);
        if (!outputCollection.isEmpty()) {
            return outputCollection.iterator().next();
        }
        return null;   
    }
    
    public Collection<ProcessDesc> getProcesses() {
        return Collections.unmodifiableCollection(availableProcesses);
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

    public Collection<ProcessInstanceDesc> getProcessInstancesByDeploymentId(String deploymentId) {
        List<ProcessInstanceDesc> processInstances = (List<ProcessInstanceDesc>)pm.queryStringWithParametersInTransaction("getProcessInstancesByDeploymentId",
                pm.addParametersToMap("externalId", deploymentId));

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

    public Collection<NodeInstanceDesc> getProcessInstanceHistory(String deploymentId, long processId) {
        return getProcessInstanceHistory(deploymentId, processId, false);
    }


    public Collection<NodeInstanceDesc> getProcessInstanceHistory(String deploymentId, long processId, boolean completed) {
        HashMap<String, Object> params = pm.addParametersToMap("processId", processId, "externalId", deploymentId);                
        if (completed) {
            params.put("type", NodeInstanceLog.TYPE_EXIT);
        } else {
            params.put("type", NodeInstanceLog.TYPE_ENTER);
        }

        List<NodeInstanceDesc> nodeInstances = (List<NodeInstanceDesc>) pm.queryWithParametersInTransaction("getProcessInstanceHistory", params);

        return nodeInstances;
    }

    public Collection<NodeInstanceDesc> getProcessInstanceFullHistory(String deploymentId, long processId) {
        List<NodeInstanceDesc> nodeInstances = (List<NodeInstanceDesc>)pm.queryWithParametersInTransaction("getProcessInstanceFullHistory", 
                pm.addParametersToMap("processId", processId, "externalId", deploymentId));

        return nodeInstances;
    }

    public Collection<NodeInstanceDesc> getProcessInstanceActiveNodes(String deploymentId, long processId) {
        
        List<NodeInstanceDesc> activeNodeInstances = (List<NodeInstanceDesc>) pm.queryWithParametersInTransaction("getProcessInstanceActiveNodes", 
                pm.addParametersToMap("processId", processId, "externalId", deploymentId));
        
        return activeNodeInstances;
    }
    

    public Collection<NodeInstanceDesc> getProcessInstanceCompletedNodes(String deploymentId, long processId) {
        List<NodeInstanceDesc> completedNodeInstances = (List<NodeInstanceDesc>) pm.queryWithParametersInTransaction("getProcessInstanceCompletedNodes", 
                pm.addParametersToMap("processId", processId, "externalId", deploymentId));

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
        return piDesc.getDeploymentId();
    }

    private class RegExPredicate implements Predicate {
        private String pattern;
        
        private RegExPredicate(String pattern) {
            this.pattern = pattern;
        }
        
        @Override
        public boolean evaluate(Object object) {
            if (object instanceof ProcessDesc) {
                ProcessDesc pDesc = (ProcessDesc) object;
                
                if (pDesc.getId().matches(pattern) 
                        || pDesc.getName().matches(pattern)) {
                    return true;
                }
            }
            return false;
        }
        
    }
    
    private class ByDeploymentIdPredicate implements Predicate {
        private String deploymentId;
        
        private ByDeploymentIdPredicate(String deploymentId) {
            this.deploymentId = deploymentId;
        }
        
        @Override
        public boolean evaluate(Object object) {
            if (object instanceof ProcessDesc) {
                ProcessDesc pDesc = (ProcessDesc) object;
                
                if (pDesc.getDeploymentId().equals(deploymentId)) {
                    return true;
                }
            }
            return false;
        }
        
    }
    
    private class ByProcessIdPredicate implements Predicate {
        private String processId;
        
        private ByProcessIdPredicate(String processId) {
            this.processId = processId;
        }
        
        @Override
        public boolean evaluate(Object object) {
            if (object instanceof ProcessDesc) {
                ProcessDesc pDesc = (ProcessDesc) object;
                
                if (pDesc.getId().equals(processId)) {
                    return true;
                }
            }
            return false;
        }
        
    }
}
