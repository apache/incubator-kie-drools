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

import static org.kie.internal.query.QueryParameterIdentifiers.FILTER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.DeployedAsset;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.api.model.VariableDesc;
import org.jbpm.shared.services.impl.QueryManager;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.query.QueryContext;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.model.TaskEvent;



public class RuntimeDataServiceImpl implements RuntimeDataService, DeploymentEventListener {

	private static final int MAX_CACHE_ENTRIES = Integer.parseInt(System.getProperty("org.jbpm.service.cache.size", "100"));
	
    protected Set<ProcessDefinition> availableProcesses = new HashSet<ProcessDefinition>();
    protected Map<String, List<String>> deploymentsRoles = new HashMap<String, List<String>>();
    
	protected Map<String, List<String>> userDeploymentIdsCache = new LinkedHashMap<String, List<String>>() {
		private static final long serialVersionUID = -2324394641773215253L;
		
		protected boolean removeEldestEntry(Map.Entry<String, List<String>> eldest) {
			return size() > MAX_CACHE_ENTRIES;
		}
	};
    
    private TransactionalCommandService commandService;
        
    private IdentityProvider identityProvider;
    
    private TaskService taskService;
    
    
    public RuntimeDataServiceImpl() {
    	QueryManager.get().addNamedQueries("META-INF/Servicesorm.xml");
        QueryManager.get().addNamedQueries("META-INF/TaskAuditorm.xml");
        QueryManager.get().addNamedQueries("META-INF/Taskorm.xml");
    }

    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}

    
    public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
    
	/*
     * start
     * helper methods to index data upon deployment
     */
    public void onDeploy(DeploymentEvent event) {
        Collection<DeployedAsset> assets = event.getDeployedUnit().getDeployedAssets();
        List<String> roles = null;
        for( DeployedAsset asset : assets ) { 
            if( asset instanceof ProcessAssetDesc ) { 
                availableProcesses.add((ProcessAssetDesc) asset);
                if (roles == null) {
                	roles = ((ProcessAssetDesc) asset).getRoles();
                }
            }            
        }
        if (roles == null) {
        	roles = Collections.emptyList();
        }
        deploymentsRoles.put(event.getDeploymentId(), roles);
        userDeploymentIdsCache.clear();
    }
    
    public void onUnDeploy(DeploymentEvent event) {
        Collection<ProcessAssetDesc> outputCollection = new HashSet<ProcessAssetDesc>();
        CollectionUtils.select(availableProcesses, new UnsecureByDeploymentIdPredicate(event.getDeploymentId()), outputCollection);
        
        availableProcesses.removeAll(outputCollection);
        deploymentsRoles.remove(event.getDeploymentId());
        userDeploymentIdsCache.clear();
    }
    

	@Override
	public void onActivate(DeploymentEvent event) {
		Collection<ProcessAssetDesc> outputCollection = new HashSet<ProcessAssetDesc>();
        CollectionUtils.select(availableProcesses, new UnsecureByDeploymentIdPredicate(event.getDeploymentId()), outputCollection);
        
        for (ProcessAssetDesc process : outputCollection) {
        	process.setActive(true);
        }
		
	}

	@Override
	public void onDeactivate(DeploymentEvent event) {
		Collection<ProcessAssetDesc> outputCollection = new HashSet<ProcessAssetDesc>();
        CollectionUtils.select(availableProcesses, new UnsecureByDeploymentIdPredicate(event.getDeploymentId()), outputCollection);
        
        for (ProcessAssetDesc process : outputCollection) {
        	process.setActive(false);
        }
	}
    
    protected void applyQueryContext(Map<String, Object> params, QueryContext queryContext) {
    	if (queryContext != null) {
        	params.put("firstResult", queryContext.getOffset());
        	params.put("maxResults", queryContext.getCount());
        	
        	if (queryContext.getOrderBy() != null && !queryContext.getOrderBy().isEmpty()) {
        		params.put(QueryManager.ORDER_BY_KEY, queryContext.getOrderBy());
        	
	        	if (queryContext.isAscending()) {
	        		params.put(QueryManager.ASCENDING_KEY, "true");
	        	} else {
	        		params.put(QueryManager.DESCENDING_KEY, "true");        	
	        	}
        	}
        }
    }
    
    protected List<String> getDeploymentsForUser() {
    	String identityName = null;
    	List<String> roles = null;
    	try {
	    	identityName = identityProvider.getName();
	    	roles = identityProvider.getRoles();
    	} catch (Exception e) {
    		// in case there is no way to collect either name of roles of the requesting used return empty list
    		return new ArrayList<String>();
    	}
    	List<String> usersDeploymentIds = userDeploymentIdsCache.get(identityName);
    	if (usersDeploymentIds != null) {
    		return usersDeploymentIds;
    	}
    	
    	usersDeploymentIds = new ArrayList<String>();
    	userDeploymentIdsCache.put(identityName, usersDeploymentIds);
    	boolean isSecured = false;
    	for (Map.Entry<String, List<String>> entry : deploymentsRoles.entrySet()){
    		if (entry.getValue().isEmpty() || CollectionUtils.containsAny(roles, entry.getValue())) {
    			usersDeploymentIds.add(entry.getKey());
    		}
    		if (entry.getValue() != null && !entry.getValue().isEmpty()) {
    			isSecured = true;
    		}
    	}
    	
    	if (isSecured && usersDeploymentIds.isEmpty()) {
    		usersDeploymentIds.add("deployments-are-secured");
    	}
    	
    	return usersDeploymentIds;
    }
    
    protected void applyDeploymentFilter(Map<String, Object> params) {
    	List<String> deploymentIdForUser = getDeploymentsForUser();
    	
    	if (deploymentIdForUser != null && !deploymentIdForUser.isEmpty()) {
    		params.put(FILTER, " log.externalId in (:deployments) ");
    		params.put("deployments", deploymentIdForUser);
        }
    }

    protected <T> Collection<T> applyPaginition(List<T> input, QueryContext queryContext) {
        if (queryContext != null) {
        	int start = queryContext.getOffset();
        	int end = start + queryContext.getCount();
        	if (input.size() < start) {
        		// no elements in given range
        		return new ArrayList<T>();
        	} else if (input.size() >= end) {
        		return Collections.unmodifiableCollection(new ArrayList<T>(input.subList(start, end)));
        	} else if (input.size() < end) {
        		return Collections.unmodifiableCollection(new ArrayList<T>(input.subList(start, input.size())));
        	}
        	
        }
        
        return Collections.unmodifiableCollection(input);
    }
    
    protected void applySorting(List<ProcessDefinition> input, final QueryContext queryContext) {
    	if (queryContext != null && queryContext.getOrderBy() != null && !queryContext.getOrderBy().isEmpty()) {
    		Collections.sort(input, new Comparator<ProcessDefinition>() {

				@Override
				public int compare(ProcessDefinition o1, ProcessDefinition o2) {
					if ("ProcessName".equals(queryContext.getOrderBy())) {
						return o1.getName().compareTo(o2.getName());
					} else if ("ProcessVersion".equals(queryContext.getOrderBy())) {
						return o1.getVersion().compareTo(o2.getVersion());
					} else if ("Project".equals(queryContext.getOrderBy())) {
						return o1.getDeploymentId().compareTo(o2.getDeploymentId());
					}
					return 0;
				}
			});
    		
    		if (!queryContext.isAscending()) {
    			Collections.reverse(input);
    		}
    	}
    }
    /*
     * end
     * helper methods to index data upon deployment
     */
    
    /*
     * start
     * process definition methods
     */
	public Collection<ProcessDefinition> getProcessesByDeploymentId(String deploymentId, QueryContext queryContext) {
        List<ProcessDefinition> outputCollection = new ArrayList<ProcessDefinition>();
        CollectionUtils.select(availableProcesses, new ByDeploymentIdPredicate(deploymentId, identityProvider.getRoles()), outputCollection);
        
        applySorting(outputCollection, queryContext);
        return applyPaginition(outputCollection, queryContext);
    }
    
    public ProcessDefinition getProcessesByDeploymentIdProcessId(String deploymentId, String processId) {
    	List<ProcessDefinition> outputCollection = new ArrayList<ProcessDefinition>();
        CollectionUtils.select(availableProcesses, new ByDeploymentIdProcessIdPredicate(deploymentId, processId, identityProvider.getRoles(), true), outputCollection);
        
        if (!outputCollection.isEmpty()) {
            return outputCollection.iterator().next();
        }
        return null; 
    }
    
    public Collection<ProcessDefinition> getProcessesByFilter(String filter, QueryContext queryContext) {
    	List<ProcessDefinition> outputCollection = new ArrayList<ProcessDefinition>();
        CollectionUtils.select(availableProcesses, new RegExPredicate("(?i)^.*"+filter+".*$", identityProvider.getRoles()), outputCollection);
        
        applySorting(outputCollection, queryContext);
        return applyPaginition(outputCollection, queryContext);
    }

    public ProcessDefinition getProcessById(String processId){
        
        Collection<ProcessAssetDesc> outputCollection = new HashSet<ProcessAssetDesc>();
        CollectionUtils.select(availableProcesses, new ByProcessIdPredicate(processId, identityProvider.getRoles()), outputCollection);
        if (!outputCollection.isEmpty()) {
            return outputCollection.iterator().next();
        }
        return null;   
    }
    
    public Collection<ProcessDefinition> getProcesses(QueryContext queryContext) {
    	List<ProcessDefinition> outputCollection = new ArrayList<ProcessDefinition>();
    	CollectionUtils.select(availableProcesses, new SecurePredicate(identityProvider.getRoles(), false), outputCollection);
    	
    	applySorting(outputCollection, queryContext);
    	return applyPaginition(outputCollection, queryContext);
    }

    @Override
    public Collection<String> getProcessIds(String deploymentId, QueryContext queryContext) {
        List<String> processIds = new ArrayList<String>(availableProcesses.size());
        if( deploymentId == null || deploymentId.isEmpty() ) { 
            return processIds;
        }
        for( ProcessDefinition procAssetDesc : availableProcesses ) { 
            if( ((ProcessAssetDesc)procAssetDesc).getDeploymentId().equals(deploymentId) && ((ProcessAssetDesc)procAssetDesc).isActive()) {
                processIds.add(procAssetDesc.getId());
            }
        }
        return applyPaginition(processIds, queryContext);
    }
    /*
     * end
     * process definition methods
     */
    
    
    /*
     * start
     * process instances methods
     */    
    
    public Collection<ProcessInstanceDesc> getProcessInstances(QueryContext queryContext) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	applyQueryContext(params, queryContext);
    	applyDeploymentFilter(params);
        List<ProcessInstanceDesc> processInstances =  commandService.execute(
			new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstances", params));

        return Collections.unmodifiableCollection(processInstances);
    }
    
    public Collection<ProcessInstanceDesc> getProcessInstances(List<Integer> states, String initiator, QueryContext queryContext) { 
        
        List<ProcessInstanceDesc> processInstances = null; 
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("states", states);
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        if (initiator == null) {

            processInstances = commandService.execute(
    				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByStatus", params));
        } else {

            params.put("initiator", initiator);
            processInstances = commandService.execute(
    				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByStatusAndInitiator", params)); 
        }
        return Collections.unmodifiableCollection(processInstances);
    }

    public Collection<ProcessInstanceDesc> getProcessInstancesByDeploymentId(String deploymentId, List<Integer> states, QueryContext queryContext) {
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("externalId", deploymentId);
        params.put("states", states);
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        List<ProcessInstanceDesc> processInstances = commandService.execute(
				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByDeploymentId",
                params));
	    return Collections.unmodifiableCollection(processInstances);

    }


    public Collection<ProcessInstanceDesc> getProcessInstancesByProcessDefinition(String processDefId, QueryContext queryContext){
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("processDefId", processDefId);
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
    	List<ProcessInstanceDesc> processInstances = commandService.execute(
				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByProcessDefinition",
              params));

        return Collections.unmodifiableCollection(processInstances);
    }
    

	@Override
	public Collection<ProcessInstanceDesc> getProcessInstancesByProcessDefinition(String processDefId, List<Integer> states, QueryContext queryContext) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("processId", processDefId);
        params.put("states", states);
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
    	List<ProcessInstanceDesc> processInstances = commandService.execute(
				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByProcessIdAndStatus",
              params));

        return Collections.unmodifiableCollection(processInstances);
	}
    
    public ProcessInstanceDesc getProcessInstanceById(long processId) {
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("processId", processId);
        params.put("maxResults", 1);

        List<ProcessInstanceDesc> processInstances = commandService.execute(
				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstanceById", 
                params));

        if (!processInstances.isEmpty()) {
        	ProcessInstanceDesc desc = processInstances.iterator().next();
        	List<String> statuses = new ArrayList<String>();
        	statuses.add(Status.Ready.name());
        	statuses.add(Status.Reserved.name());
        	statuses.add(Status.InProgress.name());
        	
        	params = new HashMap<String, Object>();
            params.put("processInstanceId", desc.getId());
            params.put("statuses", statuses);
            List<UserTaskInstanceDesc> tasks = commandService.execute(
    				new QueryNameCommand<List<UserTaskInstanceDesc>>("getTaskInstancesByProcessInstanceId", params));
        	((org.jbpm.kie.services.impl.model.ProcessInstanceDesc)desc).setActiveTasks(tasks);
        	return desc;
        }
        return null;
   }
    
	@Override
	public ProcessInstanceDesc getProcessInstanceByCorrelationKey(CorrelationKey correlationKey) {
	   	Map<String, Object> params = new HashMap<String, Object>();
        params.put("correlationKey", correlationKey.toExternalForm());
        params.put("maxResults", 1);

        List<ProcessInstanceDesc> processInstances = commandService.execute(
				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstanceByCorrelationKey", 
                params));

        if (!processInstances.isEmpty()) {
        	ProcessInstanceDesc desc = processInstances.iterator().next();
        	List<String> statuses = new ArrayList<String>();
        	statuses.add(Status.Ready.name());
        	statuses.add(Status.Reserved.name());
        	statuses.add(Status.InProgress.name());
        	
        	params = new HashMap<String, Object>();
            params.put("processInstanceId", desc.getId());
            params.put("statuses", statuses);
            List<UserTaskInstanceDesc> tasks = commandService.execute(
    				new QueryNameCommand<List<UserTaskInstanceDesc>>("getTaskInstancesByProcessInstanceId", params));
        	((org.jbpm.kie.services.impl.model.ProcessInstanceDesc)desc).setActiveTasks(tasks);
        	return desc;
        }
        return null;
	}

    
    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesByProcessId(List<Integer> states, String processId, String initiator, QueryContext queryContext) {
        List<ProcessInstanceDesc> processInstances = null; 
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("states", states);        
        params.put("processId", processId);
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        if (initiator == null) {
  
            processInstances = commandService.execute(
    				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByProcessIdAndStatus", params));
        } else {
            params.put("initiator", initiator);
            
            processInstances = commandService.execute(
    				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByProcessIdAndStatusAndInitiator", params));
        }

        return Collections.unmodifiableCollection(processInstances);
    }

    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesByProcessName(
            List<Integer> states, String processName, String initiator, QueryContext queryContext) {
        List<ProcessInstanceDesc> processInstances = null; 
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put("states", states);        
        params.put("processName", processName);
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        if (initiator == null) {
  
            processInstances = commandService.execute(
    				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByProcessNameAndStatus", params));
        } else {
            params.put("initiator", initiator);
            
            processInstances = commandService.execute(
    				new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByProcessNameAndStatusAndInitiator", params));
        }

        return Collections.unmodifiableCollection(processInstances);
    }    
    
    /*
     * end
     * process instances methods
     */

    
    /*
     * start
     * node instances methods
     */    
    @Override
    public Collection<NodeInstanceDesc> getProcessInstanceHistoryActive(long processId, QueryContext queryContext) {
        return getProcessInstanceHistory(processId, false, queryContext);
    }
    
    @Override
    public Collection<NodeInstanceDesc> getProcessInstanceHistoryCompleted(long processId, QueryContext queryContext) {
        return getProcessInstanceHistory(processId, true, queryContext);
    }

    
    protected Collection<NodeInstanceDesc> getProcessInstanceHistory(long processId, boolean completed, QueryContext queryContext) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("processId", processId);
    	applyQueryContext(params, queryContext);
    	List<NodeInstanceDesc> nodeInstances = Collections.emptyList();
        if (completed) {
        	nodeInstances = commandService.execute(
    				new QueryNameCommand<List<NodeInstanceDesc>>("getProcessInstanceCompletedNodes", 
                    params));
        } else {
        	nodeInstances = commandService.execute(
    				new QueryNameCommand<List<NodeInstanceDesc>>("getProcessInstanceActiveNodes", 
                    params));
        }

        return nodeInstances;
    }
    
    @Override
    public Collection<NodeInstanceDesc> getProcessInstanceFullHistory(long processId, QueryContext queryContext) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("processId", processId);
    	applyQueryContext(params, queryContext);
        List<NodeInstanceDesc> nodeInstances = commandService.execute(
				new QueryNameCommand<List<NodeInstanceDesc>>("getProcessInstanceFullHistory", 
                params));

        return nodeInstances;
    }
    
    @Override
    public Collection<NodeInstanceDesc> getProcessInstanceFullHistoryByType(long processId, EntryType type, QueryContext queryContext) {    	
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("processId", processId);
    	params.put("type", type.getValue());
    	applyQueryContext(params, queryContext);
        List<NodeInstanceDesc> nodeInstances = commandService.execute(
				new QueryNameCommand<List<NodeInstanceDesc>>("getProcessInstanceFullHistoryByType", 
                params));

        return nodeInstances;
    }
    

	@Override
	public NodeInstanceDesc getNodeInstanceForWorkItem(Long workItemId) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("workItemId", workItemId);
        params.put("maxResults", 1);
        List<NodeInstanceDesc> nodeInstances = commandService.execute(
				new QueryNameCommand<List<NodeInstanceDesc>>("getNodeInstanceForWorkItem", params));

    	if (!nodeInstances.isEmpty()) {
        	return nodeInstances.iterator().next();
        }
        return null;
	}
    
    /*
     * end
     * node instances methods
     */
    
    /*
     * start 
     * variable methods
     */
    
    public Collection<VariableDesc> getVariablesCurrentState(long processInstanceId) {
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("processInstanceId", processInstanceId);
        List<VariableDesc> variablesState = commandService.execute(
				new QueryNameCommand<List<VariableDesc>>("getVariablesCurrentState", params));

        return variablesState;
    }
    
    public Collection<VariableDesc> getVariableHistory(long processInstanceId, String variableId, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processInstanceId", processInstanceId);
        params.put("variableId", variableId);
        applyQueryContext(params, queryContext);
    	List<VariableDesc> variablesState = commandService.execute(
				new QueryNameCommand<List<VariableDesc>>("getVariableHistory", 
                params));                

        return variablesState;
    }
    
    /*
     * end 
     * variable methods
     */
    
    /*
     * start
     * task methods
     */
    
	@Override
	public UserTaskInstanceDesc getTaskByWorkItemId(Long workItemId) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("workItemId", workItemId);
        params.put("maxResults", 1);
        List<UserTaskInstanceDesc> tasks = commandService.execute(
				new QueryNameCommand<List<UserTaskInstanceDesc>>("getTaskInstanceByWorkItemId", params));

    	if (!tasks.isEmpty()) {
        	return tasks.iterator().next();
        }
        return null;
	}

	@Override
	public UserTaskInstanceDesc getTaskById(Long taskId) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("taskId", taskId);
        params.put("maxResults", 1);
        List<UserTaskInstanceDesc> tasks = commandService.execute(
				new QueryNameCommand<List<UserTaskInstanceDesc>>("getTaskInstanceById", params));

    	if (!tasks.isEmpty()) {
        	return tasks.iterator().next();
        }
        return null;
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, QueryFilter filter) {
		
		List<Status> allActiveStatus = new ArrayList<Status>();
		allActiveStatus.add(Status.Created);
		allActiveStatus.add(Status.Ready);
		allActiveStatus.add(Status.Reserved);
		allActiveStatus.add(Status.InProgress);
		allActiveStatus.add(Status.Suspended);
	        
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("status", allActiveStatus);
        applyQueryContext(params, filter);
        applyQueryFilter(params, filter);
        return (List<TaskSummary>) commandService.execute(
				new QueryNameCommand<List<TaskSummary>>("TasksAssignedAsBusinessAdministratorByStatus",params));
			
	}
	
	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, QueryFilter filter) {
		return ((InternalTaskService)taskService).getTasksAssignedAsPotentialOwner(userId, null , null, filter);
	}
	
	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, QueryFilter filter) {
		return ((InternalTaskService)taskService).getTasksAssignedAsPotentialOwner(userId, groupIds , null, filter);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, List<Status> status, QueryFilter filter) {
		return ((InternalTaskService)taskService).getTasksAssignedAsPotentialOwner(userId, groupIds , status, filter);
	}
	
	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, QueryFilter filter) {
		return ((InternalTaskService)taskService).getTasksAssignedAsPotentialOwner(userId, null, status, filter);
	}
        
	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(
			String userId, List<Status> status, Date from, QueryFilter filter) {
        List<TaskSummary> taskSummaries = null;
        if (from != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("expirationDate", from);
			QueryFilter qf = new QueryFilter( "(t.taskData.expirationTime = :expirationDate or t.taskData.expirationTime is null)", 
	                            params, "order by t.id DESC", filter.getOffset(), filter.getCount());
	                
	 
			taskSummaries = ((InternalTaskService)taskService).getTasksAssignedAsPotentialOwner(userId, null, status, qf);
        } else {
            QueryFilter qf = new QueryFilter(filter.getOffset(), filter.getCount());
            taskSummaries = ((InternalTaskService)taskService).getTasksAssignedAsPotentialOwner(userId,null, status, qf);
        }
        return taskSummaries;
	}

	@Override
	public List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> strStatuses, Date from,
			QueryFilter filter) {
        List<TaskSummary> taskSummaries = null;
        if (from != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("expirationDate", from);
			QueryFilter qf = new QueryFilter( "(t.taskData.expirationTime = :expirationDate or t.taskData.expirationTime is null)", 
	                            params, "order by t.id DESC", filter.getOffset(), filter.getCount());
	                
	 
			taskSummaries = ((InternalTaskService)taskService).getTasksOwned(userId, null, qf);
        } else {
            QueryFilter qf = new QueryFilter(filter.getOffset(), filter.getCount());
            taskSummaries = ((InternalTaskService)taskService).getTasksOwned(userId,null, qf);
        }
        return taskSummaries;
	}

	@Override
	public List<TaskSummary> getTasksOwned(String userId, QueryFilter filter) {
		
        return ((InternalTaskService)taskService).getTasksOwned(userId, null, filter);        
	}

	@Override
	public List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status, QueryFilter filter) {
		return ((InternalTaskService)taskService).getTasksOwned(userId, status, filter);
	}

	@Override
	public List<Long> getTasksByProcessInstanceId(Long processInstanceId) {
		return taskService.getTasksByProcessInstanceId(processInstanceId);
	}

	@Override
	public List<TaskSummary> getTasksByStatusByProcessInstanceId(Long processInstanceId, List<Status> status, QueryFilter filter) {

		if (status == null || status.isEmpty()) {

			status = new ArrayList<Status>();
			status.add(Status.Created);
			status.add(Status.Ready);
			status.add(Status.Reserved);
			status.add(Status.InProgress);
			status.add(Status.Suspended);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("processInstanceId", processInstanceId);
		params.put("status", status);
		applyQueryContext(params, filter);
		applyQueryFilter(params, filter);
		return (List<TaskSummary>) commandService.execute(new QueryNameCommand<List<TaskSummary>>("TasksByStatusByProcessId", params));
	}
	/*
     * end
     * task methods
     */
    
   /*
    * start
    *  task audit queries   
    */     
        @Override
    public List<AuditTask> getAllAuditTask(String userId, QueryFilter filter){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("owner", userId);
        applyQueryContext(params, filter);
        applyQueryFilter(params, filter);
        List<AuditTask> auditTasks = commandService.execute(
    				new QueryNameCommand<List<AuditTask>>("getAllAuditTasksByUser", params));
        return auditTasks;
    }    
        
    public List<TaskEvent> getTaskEvents(long taskId, QueryFilter filter) {
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("taskId", taskId);
        applyQueryContext(params, filter);
        applyQueryFilter(params, filter);
        List<TaskEvent> taskEvents = commandService.execute(
    				new QueryNameCommand<List<TaskEvent>>("getAllTasksEvents", params));
        return taskEvents;
    }
    
    /*
    * end
    *  task audit queries   
    */  
        
    /*
     * start
     * predicates for collection filtering
     */

    private class RegExPredicate extends SecurePredicate {
        private String pattern;
        
        private RegExPredicate(String pattern, List<String> roles) {
        	super(roles, false);
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
        	super(roles, false);
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
        	super(roles, false);
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
            super(roles, false);
        	this.depoymentId = depoymentId;
            this.processId = processId;
        }
        
        private ByDeploymentIdProcessIdPredicate(String depoymentId, String processId, List<String> roles, boolean skipActiveCheck) {
            super(roles, skipActiveCheck);
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
    
    private class SecurePredicate extends ActiveOnlyPredicate {
    	private List<String> roles;
    	private boolean skipActivCheck;
    	
    	private SecurePredicate(List<String> roles, boolean skipActivCheck) {
    		this.roles = roles;
    		this.skipActivCheck = skipActivCheck;
    	}
    	
    	public boolean evaluate(Object object) {
    		if (!skipActivCheck) {
	    		boolean isActive = super.evaluate(object);
	    		if (!isActive) {
	    			return false;
    		}
    		}
    		ProcessAssetDesc pDesc = (ProcessAssetDesc) object;
    		if (this.roles == null || this.roles.isEmpty() || pDesc.getRoles() == null || pDesc.getRoles().isEmpty()) {
    			return true;
    		}
    		
    		
    		return CollectionUtils.containsAny(roles, pDesc.getRoles());
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
    
    private class ActiveOnlyPredicate implements Predicate {
        
        private ActiveOnlyPredicate() {
        }
        
        @Override
        public boolean evaluate(Object object) {
            if (object instanceof ProcessAssetDesc) {
                ProcessAssetDesc pDesc = (ProcessAssetDesc) object;                
                if (pDesc.isActive()) {
                    return true;
                }
            }
            return false;
        }
        
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsBusinessAdministratorByStatus(String userId,
                                                                             List<Status> statuses,
                                                                             QueryFilter filter) {
        return ((InternalTaskService)taskService).getTasksAssignedAsBusinessAdministratorByStatus(userId, filter.getLanguage(), statuses);
    }



    /*
     * end
     * predicates for collection filtering
     */
    
     protected void applyQueryFilter(Map<String, Object> params, QueryFilter queryFilter) {
    	if (queryFilter != null) {
    	    applyQueryContext(params, queryFilter);
        	if (queryFilter.getFilterParams() != null && !queryFilter.getFilterParams().isEmpty()) {
        		params.put(FILTER, queryFilter.getFilterParams());
        		for(String key : queryFilter.getParams().keySet()){
                    params.put(key, queryFilter.getParams().get(key));
                }
        	}
        }
    }

}
