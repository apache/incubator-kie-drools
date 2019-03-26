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

package org.jbpm.kie.services.impl;

import static java.util.Objects.requireNonNull;
import static org.jbpm.kie.services.impl.CommonUtils.getCallbackUserRoles;
import static org.kie.internal.query.QueryParameterIdentifiers.FILTER;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jbpm.kie.services.api.DeploymentIdResolver;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.kie.services.impl.security.DeploymentRolesManager;
import org.jbpm.runtime.manager.impl.identity.UserDataServiceProvider;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.TaskNotFoundException;
import org.jbpm.services.api.model.DeployedAsset;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.api.model.VariableDesc;
import org.jbpm.services.api.service.ServiceRegistry;
import org.jbpm.services.task.audit.service.TaskAuditService;
import org.jbpm.services.task.impl.TaskSummaryQueryBuilderImpl;
import org.jbpm.shared.services.impl.QueryManager;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.internal.task.query.TaskSummaryQueryBuilder;


public class RuntimeDataServiceImpl implements RuntimeDataService, DeploymentEventListener {

    private static final String DEPLOYMENT_ID_MUST_NOT_BE_NULL = "DeploymentId must not be null";
    private static final String TASK_NOT_FOUND ="No task found with id {0}";

    protected Set<String> deploymentIds = new HashSet<String>();
    protected Set<ProcessDefinition> availableProcesses = new HashSet<ProcessDefinition>();

    private TransactionalCommandService commandService;

    private IdentityProvider identityProvider;

    protected TaskService taskService;

    protected TaskAuditService taskAuditService;

    private DeploymentRolesManager deploymentRolesManager = new DeploymentRolesManager();

    private UserGroupCallback userGroupCallback = UserDataServiceProvider.getUserGroupCallback();
    
    private static final List<Status> allActiveStatus = Arrays.asList(new Status[]{
        Status.Created,
        Status.Ready,
        Status.Reserved,
        Status.InProgress,
        Status.Suspended
      });

    public RuntimeDataServiceImpl() {
    	QueryManager.get().addNamedQueries("META-INF/Servicesorm.xml");
        QueryManager.get().addNamedQueries("META-INF/TaskAuditorm.xml");
        QueryManager.get().addNamedQueries("META-INF/Taskorm.xml");
        
        ServiceRegistry.get().register(RuntimeDataService.class.getSimpleName(), this);
    }

    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}

	public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
        this.userGroupCallback = userGroupCallback;
    }

    public UserGroupCallback getUserGroupCallback() {
        return userGroupCallback;
    }

    public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

    public void setTaskAuditService(TaskAuditService taskAuditService) {
        this.taskAuditService = taskAuditService;
    }

    public void setDeploymentRolesManager(DeploymentRolesManager deploymentRolesManager) {
        this.deploymentRolesManager = deploymentRolesManager;
    }

    private void addProcessDefinition( ProcessAssetDesc asset) {
       availableProcesses.add(asset);
       deploymentIds.add(asset.getDeploymentId());
    }

    private void removeAllProcessDefinitions( Collection<ProcessAssetDesc> assets) {
        Iterator<ProcessAssetDesc> iter = assets.iterator();
        while( iter.hasNext() ) {
            ProcessAssetDesc asset = iter.next();
            availableProcesses.remove(asset);
            deploymentIds.remove(asset.getDeploymentId());
        }
    }

    private String getLatestDeploymentId(String deploymentId) {
        String matched = deploymentId;
        if (deploymentId != null && deploymentId.toLowerCase().endsWith("latest")) {
            matched = DeploymentIdResolver.matchAndReturnLatest(deploymentId, deploymentIds);
        }
        return matched;
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
                addProcessDefinition((ProcessAssetDesc) asset);
                if (roles == null) {
                	roles = ((ProcessAssetDesc) asset).getRoles();
                }
            }
        }
        if (roles == null) {
        	roles = Collections.emptyList();
        }
        deploymentRolesManager.addRolesForDeployment(event.getDeploymentId(), roles);
    }

    public void onUnDeploy(DeploymentEvent event) {
        Collection<ProcessAssetDesc> outputCollection = new HashSet<ProcessAssetDesc>();
        CollectionUtils.select(availableProcesses, new UnsecureByDeploymentIdPredicate(event.getDeploymentId()), outputCollection);

        removeAllProcessDefinitions(outputCollection);
        deploymentRolesManager.removeRolesForDeployment(event.getDeploymentId());
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



    protected void applyDeploymentFilter(Map<String, Object> params) {
    	List<String> deploymentIdForUser = deploymentRolesManager.getDeploymentsForUser(identityProvider);

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
						return o1.getName().compareToIgnoreCase(o2.getName());
					} else if ("ProcessVersion".equals(queryContext.getOrderBy())) {
						return o1.getVersion().compareTo(o2.getVersion());
					} else if ("Project".equals(queryContext.getOrderBy())) {
						return o1.getDeploymentId().compareToIgnoreCase(o2.getDeploymentId());
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
	    deploymentId = getLatestDeploymentId(requireNonNull(deploymentId, DEPLOYMENT_ID_MUST_NOT_BE_NULL));

        List<ProcessDefinition> outputCollection = new ArrayList<ProcessDefinition>();
        CollectionUtils.select(availableProcesses, new ByDeploymentIdPredicate(deploymentId, identityProvider.getRoles()), outputCollection);

        applySorting(outputCollection, queryContext);
        return applyPaginition(outputCollection, queryContext);
    }

    public ProcessDefinition getProcessesByDeploymentIdProcessId(String deploymentId, String processId) {
	    deploymentId = getLatestDeploymentId(requireNonNull(deploymentId, DEPLOYMENT_ID_MUST_NOT_BE_NULL));

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

    @Deprecated
    public ProcessDefinition getProcessById(String processId){

        Collection<ProcessDefinition> definitions = getProcessesById(processId);
        if (!definitions.isEmpty()) {
            return definitions.iterator().next();
        }

        return null;
    }


    public Collection<ProcessDefinition> getProcessesById(String processId){

        Collection<ProcessDefinition> outputCollection = new HashSet<ProcessDefinition>();
        CollectionUtils.select(availableProcesses, new ByProcessIdPredicate(processId, identityProvider.getRoles()), outputCollection);

        return outputCollection;
    }

    public Collection<ProcessDefinition> getProcesses(QueryContext queryContext) {
    	List<ProcessDefinition> outputCollection = new ArrayList<ProcessDefinition>();
    	CollectionUtils.select(availableProcesses, new SecurePredicate(identityProvider.getRoles(), false), outputCollection);

    	applySorting(outputCollection, queryContext);
    	return applyPaginition(outputCollection, queryContext);
    }

    @Override
    public Collection<String> getProcessIds(String deploymentId, QueryContext queryContext) {
	    deploymentId = getLatestDeploymentId(requireNonNull(deploymentId, DEPLOYMENT_ID_MUST_NOT_BE_NULL));

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
	    deploymentId = getLatestDeploymentId(requireNonNull(deploymentId, DEPLOYMENT_ID_MUST_NOT_BE_NULL));

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
    public Collection<ProcessInstanceDesc> getProcessInstancesByCorrelationKey(CorrelationKey correlationKey, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("correlationKey", correlationKey.toExternalForm() + "%");
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);

        List<ProcessInstanceDesc> processInstances = commandService.execute(
                new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByCorrelationKey",
                params));

        return processInstances;
    }
	
    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesByCorrelationKeyAndStatus(CorrelationKey correlationKey, List<Integer> states, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("correlationKey", correlationKey.toExternalForm() + "%");
        params.put("states", states);
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);

        List<ProcessInstanceDesc> processInstances = commandService.execute(
                new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByCorrelationKeyAndStatus",
                params));

        return processInstances;
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


    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesByVariable(String variableName, List<Integer> states, QueryContext queryContext) {

        Map<String, Object> params = new HashMap<String, Object>();
        if (states == null || states.isEmpty()) {
            states = new ArrayList<Integer>();
            states.add(ProcessInstance.STATE_ACTIVE);
        }
        params.put("states", states);
        params.put("variable", variableName);
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);

        List<ProcessInstanceDesc> processInstances = commandService.execute(
                    new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByVariableName", params));


        return Collections.unmodifiableCollection(processInstances);
    }

    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesByVariableAndValue(String variableName, String variableValue, List<Integer> states, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();

        if (states == null || states.isEmpty()) {
            states = new ArrayList<Integer>();
            states.add(ProcessInstance.STATE_ACTIVE);
        }
        params.put("states", states);
        params.put("variable", variableName);
        params.put("variableValue", variableValue);
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);

        List<ProcessInstanceDesc> processInstances = commandService.execute(
                    new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByVariableNameAndValue", params));


        return Collections.unmodifiableCollection(processInstances);
    }
    

    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesByParent(Long parentProcessInstanceId, List<Integer> states, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();

        if (states == null || states.isEmpty()) {
            states = new ArrayList<Integer>();
            states.add(ProcessInstance.STATE_ACTIVE);
        }
        params.put("states", states);
        params.put("parentId", parentProcessInstanceId);
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);

        List<ProcessInstanceDesc> processInstances = commandService.execute(new QueryNameCommand<List<ProcessInstanceDesc>>("getProcessInstancesByParent", params));
        return processInstances;
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
	

    @Override
    public Collection<NodeInstanceDesc> getNodeInstancesByNodeType(long processInstanceId, List<String> nodeTypes, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processInstanceId", processInstanceId);
        params.put("nodeTypes", nodeTypes);
        applyQueryContext(params, queryContext);
        List<NodeInstanceDesc> nodeInstances = commandService.execute(
                new QueryNameCommand<List<NodeInstanceDesc>>("getNodeInstancesByNodeType",
                params));

        return nodeInstances;
    }
    
    @Override
    public Collection<NodeInstanceDesc> getNodeInstancesByCorrelationKeyNodeType(CorrelationKey correlationKey,  List<Integer> states, List<String> nodeTypes, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("correlationKey", correlationKey.toExternalForm() + "%");
        params.put("states", states);
        params.put("nodeTypes", nodeTypes);
        List<NodeInstanceDesc> nodeInstances = commandService.execute(
                new QueryNameCommand<List<NodeInstanceDesc>>("getNodeInstancesByCorrelationKeyAndNodeType",
                params));

        return nodeInstances;
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
	
		return getTasksAssignedAsBusinessAdministratorByStatus(userId, null, filter);

	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, QueryFilter filter) {
	    
	    Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("groupIds", getCallbackUserRoles(userGroupCallback, userId));
        params.put("status", allActiveStatus); 
        
        applyQueryContext(params, filter);
        applyQueryFilter(params, filter);
        return (List<TaskSummary>) commandService.execute(new QueryNameCommand<List<TaskSummary>>("NewTasksAssignedAsPotentialOwner",params));
        
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, QueryFilter filter) {
	    Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);        
        params.put("groupIds", mergeLists(groupIds, getCallbackUserRoles(userGroupCallback, userId)));
        
        applyQueryContext(params, filter);
        applyQueryFilter(params, filter);
        return (List<TaskSummary>) commandService.execute(new QueryNameCommand<List<TaskSummary>>("TasksAssignedAsPotentialOwnerWithGroups",params));
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, List<Status> status, QueryFilter filter) {
	    Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);        
        params.put("groupIds", mergeLists(groupIds, getCallbackUserRoles(userGroupCallback, userId)));
        params.put("status", adoptList(status, allActiveStatus));  
        
        applyQueryContext(params, filter);
        applyQueryFilter(params, filter);
        return (List<TaskSummary>) commandService.execute(new QueryNameCommand<List<TaskSummary>>("NewTasksAssignedAsPotentialOwner",params));
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, QueryFilter filter) {
	    Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);        
        params.put("groupIds", getCallbackUserRoles(userGroupCallback, userId));
        params.put("status", adoptList(status, allActiveStatus));  
        
        applyQueryContext(params, filter);
        applyQueryFilter(params, filter);
        return (List<TaskSummary>) commandService.execute(new QueryNameCommand<List<TaskSummary>>("NewTasksAssignedAsPotentialOwner",params));
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


			taskSummaries = getTasksAssignedAsPotentialOwnerByStatus(userId, status, qf);
        } else {
            QueryFilter qf = new QueryFilter(filter.getOffset(), filter.getCount());
            taskSummaries = getTasksAssignedAsPotentialOwnerByStatus(userId, status, qf);
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


			taskSummaries = getTasksOwnedByStatus(userId, strStatuses, qf);
        } else {
            QueryFilter qf = new QueryFilter(filter.getOffset(), filter.getCount());
            taskSummaries = getTasksOwnedByStatus(userId, strStatuses, qf);
        }
        return taskSummaries;
	}

	@Override
	public List<TaskSummary> getTasksOwned(String userId, QueryFilter filter) {

        return getTasksOwnedByStatus(userId, null, filter);
	}

	@Override
	public List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status, QueryFilter filter) {
	    Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);        
        params.put("status", adoptList(status, allActiveStatus));  
        
        applyQueryContext(params, filter);
        applyQueryFilter(params, filter);
        return (List<TaskSummary>) commandService.execute(new QueryNameCommand<List<TaskSummary>>("NewTasksOwned",params));
	}

	@Override
	public List<Long> getTasksByProcessInstanceId(Long processInstanceId) {
	    Map<String, Object> params = new HashMap<String, Object>();
        params.put("processInstanceId", processInstanceId);                  
        
        return (List<Long>) commandService.execute(new QueryNameCommand<List<Long>>("TasksByProcessInstanceId",params));
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


    @Override
    public List<TaskSummary> getTasksAssignedAsBusinessAdministratorByStatus(String userId,
                                                                             List<Status> statuses,
                                                                             QueryFilter filter) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("status", adoptList(statuses, allActiveStatus));
        params.put("groupIds", getCallbackUserRoles(userGroupCallback, userId));
        applyQueryContext(params, filter);
        applyQueryFilter(params, filter);
        return (List<TaskSummary>) commandService.execute(
                new QueryNameCommand<List<TaskSummary>>("TasksAssignedAsBusinessAdministratorByStatus",params));
        
    }

    @Override
    public TaskSummaryQueryBuilder taskSummaryQuery(String userId) {
        return new TaskSummaryQueryBuilderImpl(userId, taskService);
    }

    @Override
    public List<TaskSummary> getTasksByVariable( String userId, String variableName, List<Status> statuses,
            QueryContext queryContext ) {
        TaskSummaryQueryBuilder queryBuilder = new TaskSummaryQueryBuilderImpl(userId, taskService).intersect();
        addVariableNameAndVariableValueParameters(queryBuilder, variableName);
        if( statuses != null && ! statuses.isEmpty() ) {
            queryBuilder.status(statuses.toArray(new Status[statuses.size()]));
        }
        setQueryContextOptions(queryContext, queryBuilder);
        return queryBuilder.build().getResultList();
    }


    @Override
    public List<TaskSummary> getTasksByVariableAndValue( String userId, String variableName, String variableValue,
            List<Status> statuses, QueryContext queryContext ) {
        TaskSummaryQueryBuilder queryBuilder = new TaskSummaryQueryBuilderImpl(userId, taskService).intersect();
        addVariableNameAndVariableValueParameters(queryBuilder, variableName, variableValue);
        if( statuses != null && ! statuses.isEmpty() ) {
            queryBuilder.status(statuses.toArray(new Status[statuses.size()]));
        }
        setQueryContextOptions(queryContext, queryBuilder);
        return queryBuilder.build().getResultList();
    }

    private void setQueryContextOptions(QueryContext queryContext, TaskSummaryQueryBuilder queryBuilder) {
        if( queryContext != null ) {
            Integer param = queryContext.getOffset();
            if( param != null ) {
                queryBuilder.offset(param);
            }
            param = queryContext.getCount();
            if( param != null ) {
                queryBuilder.maxResults(param);
            }
            String orderBy = queryContext.getOrderBy();
            if( orderBy != null ) {
                orderBy = orderBy.toLowerCase();
                boolean orderBySet = false;
                for( TaskSummaryQueryBuilder.OrderBy orderByEnum: TaskSummaryQueryBuilder.OrderBy.values() ) {
                    if( orderBy.equals(orderByEnum.toString().toLowerCase()) ) {
                        if (queryContext.isAscending()) {
                            queryBuilder.ascending(orderByEnum);
                        } else {
                            queryBuilder.descending(orderByEnum);
                        }
                        orderBySet = true;
                        break;
                    }
                }
                if( ! orderBySet ) {
                    throw new IllegalArgumentException("Unsupported QueryContext.orderBy value: \"" + orderBy + "\"");
                }
            }
        }
    }

    private void addVariableNameAndVariableValueParameters(TaskSummaryQueryBuilder queryBuilder, String... variableNameOrValue) {
        if( variableNameOrValue.length > 0 ) {
            if( variableNameOrValue[0].contains("*") || variableNameOrValue[0].contains("?") ) {
                queryBuilder.regex();
            }
            queryBuilder.variableName(variableNameOrValue[0]).equals();
        }
        if( variableNameOrValue.length > 1 ) {
            if( variableNameOrValue[1].contains("*") || variableNameOrValue[1].contains("?") ) {
                queryBuilder.regex();
            }
            queryBuilder.variableValue(variableNameOrValue[1]).equals();
        }
        if( variableNameOrValue.length > 2 ) {
            throw new IllegalStateException("Only String arguments expected for the " + Thread.currentThread().getStackTrace()[0].getMethodName() + " method!");
        }
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

       @Override
    public List<AuditTask> getAllAuditTaskByStatus(String userId, QueryFilter filter){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("owner", userId);
        params.put("statuses", filter.getParams().get("statuses"));
        applyQueryContext(params, filter);
        applyQueryFilter(params, filter);
        List<AuditTask> auditTasks = commandService.execute(
    				new QueryNameCommand<List<AuditTask>>("getAllAuditTasksByStatus", params));
        return auditTasks;
    }

    @Override
    public List<AuditTask> getAllGroupAuditTask(String userId, QueryFilter filter){

        List<String> owners = new ArrayList<String>();
        owners.add(userId);
        owners.addAll(getCallbackUserRoles(userGroupCallback, userId));
    
        Map<String, Object> params = new HashMap<String, Object>();           
        params.put("potentialOwners", owners);
        
        applyQueryContext(params, filter);
        applyQueryFilter(params, filter);
        List<AuditTask> auditTasks = commandService.execute(new QueryNameCommand<List<AuditTask>>("getAllGroupAuditTasksByUser", params));
        return auditTasks;            

    }

     @Override
    public List<AuditTask> getAllAdminAuditTask(String userId, QueryFilter filter){
         List<String> businessAdmins = new ArrayList<String>();
         businessAdmins.add(userId);
         businessAdmins.addAll(getCallbackUserRoles(userGroupCallback, userId));
     
         Map<String, Object> params = new HashMap<String, Object>();           
         params.put("businessAdmins", businessAdmins);
         
         applyQueryContext(params, filter);
         applyQueryFilter(params, filter);
         List<AuditTask> auditTasks = commandService.execute(new QueryNameCommand<List<AuditTask>>("getAllAdminAuditTasksByUser", params));
         return auditTasks;  
    }

    public List<TaskEvent> getTaskEvents(long taskId, QueryFilter filter) {
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("taskId", taskId);
        applyQueryContext(params, filter);
        applyQueryFilter(params, filter);
        List<TaskEvent> taskEvents = commandService.execute(
    				new QueryNameCommand<List<TaskEvent>>("getAllTasksEvents", params));

        if(taskEvents == null || taskEvents.isEmpty()) {
            
            UserTaskInstanceDesc task = getTaskById(taskId);
            if (task == null) {
                throw new TaskNotFoundException( MessageFormat.format(TASK_NOT_FOUND, taskId) );
            }
        }
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

     protected List<?> adoptList(List<?> source, List<?> values) {
         
         if (source == null || source.isEmpty()) {
             List<Object> data = new ArrayList<Object>();            
             for (Object value : values) {
                 data.add(value);
             }
             return data;
         }
         
         return source;
     }
     
     protected List<?> mergeLists(List<?> source, List<?> values) {
         List<Object> data = new ArrayList<Object>();  
         data.addAll(values);
        
         if (source != null) {
             data.addAll(source);
         }
         
         return data;
     }
     

}
