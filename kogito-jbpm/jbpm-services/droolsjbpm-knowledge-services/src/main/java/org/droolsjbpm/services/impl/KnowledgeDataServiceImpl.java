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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.Query;

import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.api.SessionLocator;
import org.droolsjbpm.services.impl.model.NodeInstanceDesc;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
import org.droolsjbpm.services.impl.model.VariableStateDesc;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;

/**
 *
 * @author salaboy
 */
@ApplicationScoped
@Transactional
public class KnowledgeDataServiceImpl implements KnowledgeDataService {

    Map<String, SessionLocator> ksessionLocators = new HashMap<String, SessionLocator>();
    @Inject
    private JbpmServicesPersistenceManager pm;

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    public Collection<ProcessInstanceDesc> getProcessInstances() { 
        List<ProcessInstanceDesc> processInstances = (List<ProcessInstanceDesc>) pm.queryStringInTransaction("select pi FROM ProcessInstanceDesc pi where pi.pk = (select max(pid.pk) FROM ProcessInstanceDesc pid WHERE pid.id = pi.id ) ");

        return processInstances;
    }
    
    public Collection<ProcessInstanceDesc> getProcessInstances(List<Integer> states, String initiator) { 
        String query = "";
        Map<String, Object> params = new HashMap<String, Object>();
        if (initiator == null) {
            query = "select pi FROM ProcessInstanceDesc pi where pi.pk = (select max(pid.pk) FROM ProcessInstanceDesc pid WHERE pid.id = pi.id ) and pi.state in (:states)";
            params.put("states", states);
        } else {
            query = "select pi FROM ProcessInstanceDesc pi where pi.pk = (select max(pid.pk) FROM ProcessInstanceDesc pid WHERE pid.id = pi.id and pi.initiator =:initiator) and pi.state in (:states)";
            params.put("states", states);
            params.put("initiator", initiator);
        }
        
        return (List<ProcessInstanceDesc>) pm.queryStringWithParametersInTransaction(query, params); 
        
    }

    public Collection<ProcessInstanceDesc> getProcessInstancesBySessionId(String sessionId) {
        List<ProcessInstanceDesc> processInstances = (List<ProcessInstanceDesc>)pm.queryStringWithParametersInTransaction("select pi FROM ProcessInstanceDesc pi where pi.sessionId=:sessionId, pi.pk = (select max(pid.pk) FROM ProcessInstanceDesc pid WHERE pid.id = pi.id )", 
                pm.addParametersToMap("sessionId", sessionId));
        return processInstances;
    }

    public Collection<ProcessDesc> getProcessesByDomainName(String domainName) {
        List<ProcessDesc> processes = (List<ProcessDesc>) pm.queryStringWithParametersInTransaction("select pd from ProcessDesc pd where pd.domainName=:domainName GROUP BY pd.id ORDER BY pd.dataTimeStamp DESC", 
                pm.addParametersToMap("domainName", domainName));
        return processes;
    }
    
    public Collection<ProcessDesc> getProcessesByFilter(String filter) {
        List<ProcessDesc> processes = (List<ProcessDesc>)pm.queryStringWithParametersInTransaction("select pd from ProcessDesc pd where pd.id like :filter or pd.name like :filter ORDER BY pd.dataTimeStamp DESC", 
                pm.addParametersToMap("filter", filter+"%"));
        return processes;
    }

    public Collection<ProcessDesc> getProcesses() {
        List<ProcessDesc> processes = (List<ProcessDesc>) pm.queryStringInTransaction("select pd from ProcessDesc pd ORDER BY pd.pki DESC, pd.dataTimeStamp DESC");
        return processes;
    }

    public Collection<ProcessInstanceDesc> getProcessInstancesByProcessDefinition(String processDefId){
      List<ProcessInstanceDesc> processInstances = (List<ProcessInstanceDesc>)pm.queryStringWithParametersInTransaction("select pi FROM ProcessInstanceDesc pi where pi.processDefId =:processDefId and pi.pk = (select max(pid.pk) FROM ProcessInstanceDesc pid WHERE pid.id = pi.id and pid.processDefId =:processDefId )",
              pm.addParametersToMap("processDefId", processDefId));

        return processInstances;
    }
    
    public ProcessInstanceDesc getProcessInstanceById(long processId) {
        List<ProcessInstanceDesc> processInstances = (List<ProcessInstanceDesc>)pm.queryStringWithParametersInTransaction("select pid from ProcessInstanceDesc pid where pid.id=:processId ORDER BY pid.pk DESC", 
                pm.addParametersToMap("processId", processId, "maxResults", 1));

       return processInstances.get(0);
   }

    public Collection<NodeInstanceDesc> getProcessInstanceHistory(int sessionId, long processId) {
        return getProcessInstanceHistory(sessionId, processId, false);
    }

    public Collection<VariableStateDesc> getVariablesCurrentState(long processInstanceId) {
        List<VariableStateDesc> variablesState = (List<VariableStateDesc>) pm.queryStringWithParametersInTransaction("select vs FROM VariableStateDesc vs where vs.processInstanceId =:processInstanceId AND vs.pk in (select max(vss.pk) FROM VariableStateDesc vss WHERE vss.processInstanceId =:processInstanceId group by vss.variableId ) order by vs.pk", 
                pm.addParametersToMap("processInstanceId", processInstanceId));

        return variablesState;
    }

    public Collection<NodeInstanceDesc> getProcessInstanceHistory(int sessionId, long processId, boolean completed) {
        List<NodeInstanceDesc> nodeInstances = (List<NodeInstanceDesc>)pm.queryStringWithParametersInTransaction("select nid from NodeInstanceDesc nid where nid.processInstanceId=:processId AND nid.sessionId=:sessionId AND nid.completed =:completed ORDER BY nid.dataTimeStamp DESC",
                pm.addParametersToMap("processId", processId, "sessionId", sessionId, "completed", completed));
        return nodeInstances;
    }

    public Collection<NodeInstanceDesc> getProcessInstanceFullHistory(int sessionId, long processId) {
        List<NodeInstanceDesc> nodeInstances = (List<NodeInstanceDesc>)pm.queryStringWithParametersInTransaction("select nid from NodeInstanceDesc nid where nid.processInstanceId=:processId AND nid.sessionId=:sessionId ORDER BY nid.dataTimeStamp DESC",
                pm.addParametersToMap("processId", processId, "sessionId", sessionId));

        return nodeInstances;
    }

    public Collection<NodeInstanceDesc> getProcessInstanceActiveNodes(int sessionId, long processId) {
        List<NodeInstanceDesc> completedNodeInstances = (List<NodeInstanceDesc>)pm.queryStringWithParametersInTransaction("select nid from NodeInstanceDesc nid where nid.processInstanceId=:processId AND nid.sessionId=:sessionId AND nid.completed =:completed ORDER BY nid.dataTimeStamp DESC",
                pm.addParametersToMap("processId", processId, "sessionId", sessionId, "completed", true));
        
        List<NodeInstanceDesc> activeNodeInstances = (List<NodeInstanceDesc>)pm.queryStringWithParametersInTransaction("select nid from NodeInstanceDesc nid where nid.processInstanceId=:processId AND nid.sessionId=:sessionId AND nid.completed =:completed ORDER BY nid.dataTimeStamp DESC",
                pm.addParametersToMap("processId", processId, "sessionId", sessionId, "completed", false));
        
        List<NodeInstanceDesc> uncompletedNodeInstances = new ArrayList<NodeInstanceDesc>(activeNodeInstances.size() - completedNodeInstances.size());
        for(NodeInstanceDesc nid : activeNodeInstances){
            boolean completed = false;
            for(NodeInstanceDesc cnid : completedNodeInstances){
                
                if(nid.getNodeId() == cnid.getNodeId()){
                    completed = true;
                }
            }
            if(!completed){
                uncompletedNodeInstances.add(nid);
            } 
        }
        

        return uncompletedNodeInstances;
    }
    
    public Collection<VariableStateDesc> getVariableHistory(long processInstanceId, String variableId) {
        List<VariableStateDesc> variablesState = (List<VariableStateDesc>) pm.queryStringWithParametersInTransaction("select vs FROM VariableStateDesc vs where vs.processInstanceId =:processInstanceId AND vs.variableId =:variableId order by vs.pk DESC",
                pm.addParametersToMap("processInstanceId", processInstanceId, "variableId", variableId));

        return variablesState;
    }

    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesByProcessId(
            List<Integer> states, String processId, String initiator) {
        
        String query = "";
        Map<String, Object> params = new HashMap<String, Object>();
        if (initiator == null) {
            query = "select pi FROM ProcessInstanceDesc pi where pi.pk = (select max(pid.pk) FROM ProcessInstanceDesc pid WHERE pid.id = pi.id ) " +
            		"and pi.state in (:states) and pi.processId like :processId";
            params.put("states", states);
            params.put("processId", processId +"%");
        } else {
            query = "select pi FROM ProcessInstanceDesc pi where pi.pk = (select max(pid.pk) FROM ProcessInstanceDesc pid WHERE pid.id = pi.id  and pi.initiator =:initiator) " +
            		"and pi.state in (:states) and pi.processId like :processId";
            params.put("states", states);
            params.put("initiator", initiator);
            params.put("processId", processId +"%");
        }
                
  
        return (Collection<ProcessInstanceDesc>)pm.queryStringWithParametersInTransaction(query, params);

    }

    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesByProcessName(
            List<Integer> states, String processName, String initiator) {
        List<ProcessInstanceDesc> processInstances = null; 
        String query = "";
        Map<String, Object> params = new HashMap<String, Object>();
        if (initiator == null) {
            query = "select pi FROM ProcessInstanceDesc pi where pi.pk = (select max(pid.pk) FROM ProcessInstanceDesc pid WHERE pid.id = pi.id ) " +
                    "and pi.state in (:states) and pi.processName like :processName";
            params.put("states", states);
            params.put("processName", processName +"%");
        } else {
            query = "select pi FROM ProcessInstanceDesc pi where pi.pk = (select max(pid.pk) FROM ProcessInstanceDesc pid WHERE pid.id = pi.id  and pi.initiator =:initiator) " +
                    "and pi.state in (:states) and pi.processName like :processName";
            params.put("states", states);
            params.put("initiator", initiator);
            params.put("processName", processName +"%");
        }
        return (List<ProcessInstanceDesc>) pm.queryStringWithParametersInTransaction(query, params);
    }

    @Override
    public Collection<NodeInstanceDesc> getProcessInstanceCompletedNodes(int sessionId, long processId) {
        List<NodeInstanceDesc> completedNodeInstances = (List<NodeInstanceDesc>) pm.queryStringWithParametersInTransaction("select n from NodeInstanceDesc n where n.nodeId in " +
        		"(select nodeId from NodeInstanceDesc nid where nid.processInstanceId=:processId AND nid.sessionId=:sessionId AND nid.completed =:completed) ORDER BY n.nodeId, n.dataTimeStamp DESC", 
                pm.addParametersToMap("processId", processId, "sessionId", sessionId, "completed", true));
        return completedNodeInstances;
        
    }
}
