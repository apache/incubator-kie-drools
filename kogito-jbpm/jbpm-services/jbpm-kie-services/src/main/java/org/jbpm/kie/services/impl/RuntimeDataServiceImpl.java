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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.event.Deploy;
import org.jbpm.kie.services.impl.event.DeploymentEvent;
import org.jbpm.kie.services.impl.event.Undeploy;
import org.jbpm.kie.services.impl.model.NodeInstanceDesc;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;
import org.jbpm.kie.services.impl.model.VariableStateDesc;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.kie.internal.deployment.DeployedAsset;


@ApplicationScoped
public class RuntimeDataServiceImpl implements RuntimeDataService {

    private Set<ProcessAssetDesc> availableProcesses = new HashSet<ProcessAssetDesc>();
    
    @Inject 
    private TransactionalCommandService commandService;
    
    @Inject
    private IdentityProvider identityProvider;

    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }
    
    public void indexOnDeploy(@Observes@Deploy DeploymentEvent event) {
        Collection<DeployedAsset> assets = event.getDeployedUnit().getDeployedAssets();
        for( DeployedAsset asset : assets ) { 
            if( asset instanceof ProcessAssetDesc ) { 
                availableProcesses.add((ProcessAssetDesc) asset);
            }
        }
    }
    
    public void removeOnUnDeploy(@Observes@Undeploy DeploymentEvent event) {
        Collection<ProcessAssetDesc> outputCollection = new HashSet<ProcessAssetDesc>();
        CollectionUtils.select(availableProcesses, new UnsecureByDeploymentIdPredicate(event.getDeploymentId()), outputCollection);
        
        availableProcesses.removeAll(outputCollection);
    }

    public Collection<ProcessAssetDesc> getProcessesByDeploymentId(String deploymentId) {
        Collection<ProcessAssetDesc> outputCollection = new HashSet<ProcessAssetDesc>();
        CollectionUtils.select(availableProcesses, new ByDeploymentIdPredicate(deploymentId, identityProvider.getRoles()), outputCollection);
        
        return Collections.unmodifiableCollection(outputCollection);
    }
    
    public ProcessAssetDesc getProcessesByDeploymentIdProcessId(String deploymentId, String processId) {
        Collection<ProcessAssetDesc> outputCollection = new HashSet<ProcessAssetDesc>();
        CollectionUtils.select(availableProcesses, new ByDeploymentIdProcessIdPredicate(deploymentId, processId, identityProvider.getRoles()), outputCollection);
        
        if (!outputCollection.isEmpty()) {
            return outputCollection.iterator().next();
        }
        return null; 
    }
    
    public Collection<ProcessAssetDesc> getProcessesByFilter(String filter) {
        Collection<ProcessAssetDesc> outputCollection = new HashSet<ProcessAssetDesc>();
        CollectionUtils.select(availableProcesses, new RegExPredicate("^.*"+filter+".*$", identityProvider.getRoles()), outputCollection);
        return Collections.unmodifiableCollection(outputCollection);
    }

    public ProcessAssetDesc getProcessById(String processId){
        
        Collection<ProcessAssetDesc> outputCollection = new HashSet<ProcessAssetDesc>();
        CollectionUtils.select(availableProcesses, new ByProcessIdPredicate(processId, identityProvider.getRoles()), outputCollection);
        if (!outputCollection.isEmpty()) {
            return outputCollection.iterator().next();
        }
        return null;   
    }
    
    public Collection<ProcessAssetDesc> getProcesses() {
    	Collection<ProcessAssetDesc> outputCollection = new HashSet<ProcessAssetDesc>();
    	CollectionUtils.select(availableProcesses, new SecurePredicate(identityProvider.getRoles()), outputCollection);
        return Collections.unmodifiableCollection(outputCollection);
    }

    @Override
    public Collection<String> getProcessIds(String deploymentId) {
        List<String> processIds = new ArrayList<String>(availableProcesses.size());
        if( deploymentId == null || deploymentId.isEmpty() ) { 
            return processIds;
        }
        for( ProcessAssetDesc procAssetDesc : availableProcesses ) { 
            if( procAssetDesc.getDeploymentId().equals(deploymentId) ) {
                processIds.add(procAssetDesc.getId());
            }
        }
        return processIds;
    }
    
    public Collection<ProcessInstanceDesc> getProcessInstances() {
        List<ProcessInstanceDesc> processInstances =  commandService.execute(
			new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstances"));
    	Collection<ProcessInstanceDesc> outputCollection = new HashSet<ProcessInstanceDesc>();
        CollectionUtils.select(processInstances, new SecureInstancePredicate(identityProvider.getRoles()), outputCollection);
        return Collections.unmodifiableCollection(outputCollection);
    }
    
    public Collection<ProcessInstanceDesc> getProcessInstances(List<Integer> states, String initiator) { 
        
        List<ProcessInstanceDesc> processInstances = null; 
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("states", states);
        if (initiator == null) {

            processInstances = commandService.execute(
    				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByStatus", params));
        } else {

            params.put("initiator", initiator);
            processInstances = commandService.execute(
    				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByStatusAndInitiator", params)); 
        }
        Collection<ProcessInstanceDesc> outputCollection = new HashSet<ProcessInstanceDesc>();
        CollectionUtils.select(processInstances, new SecureInstancePredicate(identityProvider.getRoles()), outputCollection);
        return Collections.unmodifiableCollection(outputCollection);
    }

    public Collection<ProcessInstanceDesc> getProcessInstancesByDeploymentId(String deploymentId, List<Integer> states) {
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("externalId", deploymentId);
        params.put("states", states);
        List<ProcessInstanceDesc> processInstances = commandService.execute(
				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByDeploymentId",
                params));
        
        try {
	        Collection<ProcessInstanceDesc> outputCollection = new HashSet<ProcessInstanceDesc>();
	        CollectionUtils.select(processInstances, new SecureInstancePredicate(identityProvider.getRoles()), outputCollection);
	        return Collections.unmodifiableCollection(outputCollection);
        } catch(ContextNotActiveException e) {
        	// in case there is no way to get roles from identity provider return complete list
        	return processInstances;
        }
    }


    public Collection<ProcessInstanceDesc> getProcessInstancesByProcessDefinition(String processDefId){
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("processDefId", processDefId);
    	List<ProcessInstanceDesc> processInstances = commandService.execute(
				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByProcessDefinition",
              params));
    	Collection<ProcessInstanceDesc> outputCollection = new HashSet<ProcessInstanceDesc>();
        CollectionUtils.select(processInstances, new SecureInstancePredicate(identityProvider.getRoles()), outputCollection);
        return Collections.unmodifiableCollection(outputCollection);
    }
    
    public ProcessInstanceDesc getProcessInstanceById(long processId) {
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("processId", processId);
        params.put("maxResults", 1);
        List<ProcessInstanceDesc> processInstances = commandService.execute(
				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstanceById", 
                params));

    	Collection<ProcessInstanceDesc> outputCollection = new HashSet<ProcessInstanceDesc>();
        CollectionUtils.select(processInstances, new SecureInstancePredicate(identityProvider.getRoles()), outputCollection);
        if (!outputCollection.isEmpty()) {
        	return outputCollection.iterator().next();
        }
        return null;
   }

    
    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesByProcessId(
            List<Integer> states, String processId, String initiator) {
        List<ProcessInstanceDesc> processInstances = null; 
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("states", states);        
        params.put("processId", processId +"%");
        if (initiator == null) {
  
            processInstances = commandService.execute(
    				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByProcessIdAndStatus", params));
        } else {
            params.put("initiator", initiator);
            
            processInstances = commandService.execute(
    				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByProcessIdAndStatusAndInitiator", params));
        }
        Collection<ProcessInstanceDesc> outputCollection = new HashSet<ProcessInstanceDesc>();
        CollectionUtils.select(processInstances, new SecureInstancePredicate(identityProvider.getRoles()), outputCollection);
        return Collections.unmodifiableCollection(outputCollection);
    }

    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesByProcessName(
            List<Integer> states, String processName, String initiator) {
        List<ProcessInstanceDesc> processInstances = null; 
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put("states", states);        
        params.put("processName", processName +"%");
        if (initiator == null) {
  
            processInstances = commandService.execute(
    				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByProcessNameAndStatus", params));
        } else {
            params.put("initiator", initiator);
            
            processInstances = commandService.execute(
    				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByProcessNameAndStatusAndInitiator", params));
        }
        Collection<ProcessInstanceDesc> outputCollection = new HashSet<ProcessInstanceDesc>();
        CollectionUtils.select(processInstances, new SecureInstancePredicate(identityProvider.getRoles()), outputCollection);
        return Collections.unmodifiableCollection(outputCollection);
    }    

    public Collection<NodeInstanceDesc> getProcessInstanceHistory(String deploymentId, long processId) {
        return getProcessInstanceHistory(deploymentId, processId, false);
    }


    public Collection<NodeInstanceDesc> getProcessInstanceHistory(String deploymentId, long processId, boolean completed) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("processId", processId);
    	params.put("externalId", deploymentId);              
        if (completed) {
            params.put("type", NodeInstanceLog.TYPE_EXIT);
        } else {
            params.put("type", NodeInstanceLog.TYPE_ENTER);
        }

        List<NodeInstanceDesc> nodeInstances = commandService.execute(
				new QueryNameCommand<List<NodeInstanceDesc>>("getProcessInstanceHistory", params));

        return nodeInstances;
    }

    public Collection<NodeInstanceDesc> getProcessInstanceFullHistory(String deploymentId, long processId) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("processId", processId);
    	params.put("externalId", deploymentId);
        List<NodeInstanceDesc> nodeInstances = commandService.execute(
				new QueryNameCommand<List<NodeInstanceDesc>>("getProcessInstanceFullHistory", 
                params));

        return nodeInstances;
    }

    public Collection<NodeInstanceDesc> getProcessInstanceActiveNodes(String deploymentId, long processId) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("processId", processId);
    	params.put("externalId", deploymentId);
        List<NodeInstanceDesc> activeNodeInstances = commandService.execute(
				new QueryNameCommand<List<NodeInstanceDesc>>("getProcessInstanceActiveNodes", 
                params));
        
        return activeNodeInstances;
    }
    

    public Collection<NodeInstanceDesc> getProcessInstanceCompletedNodes(String deploymentId, long processId) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("processId", processId);
    	params.put("externalId", deploymentId);
        List<NodeInstanceDesc> completedNodeInstances = commandService.execute(
				new QueryNameCommand<List<NodeInstanceDesc>>("getProcessInstanceCompletedNodes", 
                params));

        return completedNodeInstances;
        
    }
    
    public Collection<VariableStateDesc> getVariablesCurrentState(long processInstanceId) {
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("processInstanceId", processInstanceId);
        List<VariableStateDesc> variablesState = commandService.execute(
				new QueryNameCommand<List<VariableStateDesc>>("getVariablesCurrentState", params));

        return variablesState;
    }
    
    public Collection<VariableStateDesc> getVariableHistory(long processInstanceId, String variableId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processInstanceId", processInstanceId);
        params.put("variableId", variableId);
    	List<VariableStateDesc> variablesState = commandService.execute(
				new QueryNameCommand<List<VariableStateDesc>>("getVariableHistory", 
                params));                

        return variablesState;
    }

    private class RegExPredicate extends SecurePredicate {
        private String pattern;
        
        private RegExPredicate(String pattern, List<String> roles) {
        	super(roles);
            this.pattern = pattern;
        }
        
        @Override
        public boolean evaluate(Object object) {
            if (object instanceof ProcessAssetDesc) {
                ProcessAssetDesc pDesc = (ProcessAssetDesc) object;
                boolean hasAccess = super.evaluate(object);
                if (!hasAccess) {
                	return false;
                }
                if (pDesc.getId().matches(pattern) 
                        || pDesc.getName().matches(pattern)) {
                    return true;
                }
            }
            return false;
        }
        
    }
    
    private class ByDeploymentIdPredicate extends SecurePredicate {
        private String deploymentId;
        
        private ByDeploymentIdPredicate(String deploymentId, List<String> roles) {
        	super(roles);
            this.deploymentId = deploymentId;
        }
        
        @Override
        public boolean evaluate(Object object) {
            if (object instanceof ProcessAssetDesc) {
                ProcessAssetDesc pDesc = (ProcessAssetDesc) object;
                boolean hasAccess = super.evaluate(object);
                if (!hasAccess) {
                	return false;
                }
                if (pDesc.getDeploymentId().equals(deploymentId)) {
                    return true;
                }
            }
            return false;
        }
        
    }
    
    private class ByProcessIdPredicate extends SecurePredicate {
        private String processId;
        
        private ByProcessIdPredicate(String processId, List<String> roles) {
        	super(roles);
            this.processId = processId;
        }
        
        @Override
        public boolean evaluate(Object object) {
            if (object instanceof ProcessAssetDesc) {
                ProcessAssetDesc pDesc = (ProcessAssetDesc) object;
                boolean hasAccess = super.evaluate(object);
                if (!hasAccess) {
                	return false;
                }
                if (pDesc.getId().equals(processId)) {
                    return true;
                }
            }
            return false;
        }
        
    }
    
    private class ByDeploymentIdProcessIdPredicate extends SecurePredicate {
        private String processId;
        private String depoymentId;
        
        private ByDeploymentIdProcessIdPredicate(String depoymentId, String processId, List<String> roles) {
            super(roles);
        	this.depoymentId = depoymentId;
            this.processId = processId;
        }
        
        @Override
        public boolean evaluate(Object object) {
            if (object instanceof ProcessAssetDesc) {
                ProcessAssetDesc pDesc = (ProcessAssetDesc) object;
                boolean hasAccess = super.evaluate(object);
                if (!hasAccess) {
                	return false;
                }
                if (pDesc.getId().equals(processId) && pDesc.getDeploymentId().equals(depoymentId)) {
                    return true;
                }
            }
            return false;
        }        
    }
    
    private class SecurePredicate implements Predicate {
    	private List<String> roles;
    	
    	private SecurePredicate(List<String> roles) {
    		this.roles = roles;
    	}
    	
    	public boolean evaluate(Object object) {
    		ProcessAssetDesc pDesc = (ProcessAssetDesc) object;
    		if (this.roles == null || this.roles.isEmpty() || pDesc.getRoles() == null || pDesc.getRoles().isEmpty()) {
    			return true;
    		}
    		
    		
    		return CollectionUtils.containsAny(roles, pDesc.getRoles());
    	}
    }
    
    private class SecureInstancePredicate implements Predicate {
    	private List<String> roles;
    	
    	private SecureInstancePredicate(List<String> roles) {
    		this.roles = roles;
    	}
    	
    	public boolean evaluate(Object object) {
    		ProcessInstanceDesc pInstDesc = (ProcessInstanceDesc) object;
    		ProcessAssetDesc pDesc = getProcessesByDeploymentIdProcessId(pInstDesc.getDeploymentId(), pInstDesc.getProcessId());
    		if (this.roles == null || this.roles.isEmpty()) {
    			return true;
    		}
    		if (pDesc == null) {
    			// if you can't see the process, you shouldn't see the instances either
    			return false;
    		}
//          No need to check the list of roles, as if you can see the process, you already have the right role
//    		if (pDesc.getRoles() == null || pDesc.getRoles().isEmpty()) {
//   			return true;
//	    	}
//		    return CollectionUtils.containsAny(roles, pDesc.getRoles());
    		return true;
    	}
    }
    
    private class UnsecureByDeploymentIdPredicate implements Predicate {
        private String deploymentId;
        
        private UnsecureByDeploymentIdPredicate(String deploymentId) {
            this.deploymentId = deploymentId;
        }
        
        @Override
        public boolean evaluate(Object object) {
            if (object instanceof ProcessAssetDesc) {
                ProcessAssetDesc pDesc = (ProcessAssetDesc) object;                
                if (pDesc.getDeploymentId().equals(deploymentId)) {
                    return true;
                }
            }
            return false;
        }
        
    }
}
