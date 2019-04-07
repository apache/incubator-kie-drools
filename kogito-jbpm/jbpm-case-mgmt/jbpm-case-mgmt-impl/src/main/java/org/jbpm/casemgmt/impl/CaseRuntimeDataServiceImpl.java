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

package org.jbpm.casemgmt.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.jbpm.kie.services.impl.CommonUtils.getAuthenticatedUserRoles;
import static org.kie.internal.query.QueryParameterIdentifiers.FILTER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jbpm.casemgmt.api.CaseNotFoundException;
import org.jbpm.casemgmt.api.CaseRuntimeDataService;
import org.jbpm.casemgmt.api.auth.AuthorizationManager;
import org.jbpm.casemgmt.api.generator.CaseIdGenerator;
import org.jbpm.casemgmt.api.model.AdHocFragment;
import org.jbpm.casemgmt.api.model.CaseDefinition;
import org.jbpm.casemgmt.api.model.CaseFileItem;
import org.jbpm.casemgmt.api.model.CaseMilestone;
import org.jbpm.casemgmt.api.model.CaseRole;
import org.jbpm.casemgmt.api.model.CaseStage;
import org.jbpm.casemgmt.api.model.CaseStatus;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.instance.CaseMilestoneInstance;
import org.jbpm.casemgmt.api.model.instance.CaseStageInstance;
import org.jbpm.casemgmt.api.model.instance.StageStatus;
import org.jbpm.casemgmt.impl.model.AdHocFragmentImpl;
import org.jbpm.casemgmt.impl.model.CaseDefinitionComparator;
import org.jbpm.casemgmt.impl.model.CaseDefinitionImpl;
import org.jbpm.casemgmt.impl.model.CaseMilestoneImpl;
import org.jbpm.casemgmt.impl.model.CaseRoleImpl;
import org.jbpm.casemgmt.impl.model.CaseStageImpl;
import org.jbpm.casemgmt.impl.model.ProcessDefinitionComparator;
import org.jbpm.casemgmt.impl.model.instance.CaseMilestoneInstanceImpl;
import org.jbpm.casemgmt.impl.model.instance.CaseStageInstanceImpl;
import org.jbpm.kie.services.impl.model.NodeInstanceDesc;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.kie.services.impl.security.DeploymentRolesManager;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.DeployedAsset;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.service.ServiceRegistry;
import org.jbpm.shared.services.impl.QueryManager;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.KieInternalServices;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;


public class CaseRuntimeDataServiceImpl implements CaseRuntimeDataService, DeploymentEventListener {

    protected Set<CaseDefinitionImpl> availableCases = new HashSet<CaseDefinitionImpl>();
    protected Set<ProcessDefinition> availableProcesses = new HashSet<ProcessDefinition>();

    private CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
    
    private CaseIdGenerator caseIdGenerator;
    
    private RuntimeDataService runtimeDataService;
    private TransactionalCommandService commandService;

    private IdentityProvider identityProvider;
    private DeploymentRolesManager deploymentRolesManager = new DeploymentRolesManager();
    
    // default statuses set to active only
    private List<CaseStatus> statuses = Arrays.asList(CaseStatus.OPEN);
    
    private static final List<Status> allActiveStatus = Arrays.asList(
        Status.Created,
        Status.Ready,
        Status.Reserved,
        Status.InProgress,
        Status.Suspended
      );
    
    public CaseRuntimeDataServiceImpl() {
        QueryManager.get().addNamedQueries("META-INF/CaseMgmtorm.xml");
        
        ServiceRegistry.get().register(CaseRuntimeDataService.class.getSimpleName(), this);
    }
   
    
    public CaseIdGenerator getCaseIdGenerator() {
        return caseIdGenerator;
    }
    
    public void setCaseIdGenerator(CaseIdGenerator caseIdGenerator) {
        this.caseIdGenerator = caseIdGenerator;
    }
    
    
    public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
        this.runtimeDataService = runtimeDataService;
    }
    
    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }
    
    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }
    
    public void setDeploymentRolesManager(DeploymentRolesManager deploymentRolesManager) {
        this.deploymentRolesManager = deploymentRolesManager;
    }

    /*
     * Deploy and undeploy handling
     */    
    @Override
    public void onDeploy(DeploymentEvent event) {
        AbstractRuntimeManager runtimeManager = (AbstractRuntimeManager) event.getDeployedUnit().getRuntimeManager();        
        KieBase kieBase = runtimeManager.getEnvironment().getKieBase();
        Collection<Process> processes = kieBase.getProcesses(); 
        
        Map<String, DeployedAsset> mapProcessById = event.getDeployedUnit().getDeployedAssets()
                                                                            .stream()
                                                                            .collect(toMap(DeployedAsset::getId, asset -> asset));        
        for( Process process : processes ) {
            if( ((WorkflowProcess)process).isDynamic()) {
                String caseIdPrefix = collectCaseIdPrefix(process);
                Collection<CaseMilestone> caseMilestones = collectMilestoness(process);
                Collection<CaseStage> caseStages = collectCaseStages(event.getDeploymentId(), process.getId(), ((WorkflowProcess)process));                
                Collection<CaseRole> caseRoles = collectCaseRoles(process);
                Collection<AdHocFragment> adHocFragments = collectAdHocFragments((WorkflowProcess)process);
                Map<String, List<String>> dataAccessRestrictions = collectDataAccessRestrictions(process);
                
                CaseDefinitionImpl caseDef = new CaseDefinitionImpl((ProcessAssetDesc) mapProcessById.get(process.getId()), caseIdPrefix, caseStages, caseMilestones, caseRoles, adHocFragments, dataAccessRestrictions);
                
                availableCases.add(caseDef);
                caseIdGenerator.register(caseIdPrefix);
            }
        }
        
        // collect role information
        Collection<DeployedAsset> assets = event.getDeployedUnit().getDeployedAssets();
        List<String> roles = null;
        for( DeployedAsset asset : assets ) {
            if( asset instanceof ProcessAssetDesc ) {  
                // if it's not dynamic it's considered as not case definition
                if (!((ProcessAssetDesc) asset).isDynamic()) {
                    availableProcesses.add((ProcessAssetDesc) asset);
                }
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

    @Override
    public void onUnDeploy(DeploymentEvent event) {
        
        Collection<CaseDefinitionImpl> undeployed = availableCases.stream()
                    .filter(caseDef -> caseDef.getDeploymentId().equals(event.getDeploymentId()))
                    .collect(toList());
        
        availableCases.removeAll(undeployed);
        
        Collection<ProcessDefinition> undeployedProcesses = availableProcesses.stream()
                .filter(process -> process.getDeploymentId().equals(event.getDeploymentId()))
                .collect(toList());
        
        availableProcesses.removeAll(undeployedProcesses);
        
        undeployed.forEach(caseDef -> caseIdGenerator.unregister(caseDef.getIdentifierPrefix()));
        deploymentRolesManager.removeRolesForDeployment(event.getDeploymentId());

    }

    @Override
    public void onActivate(DeploymentEvent event) {
        // no op - all is done on RuntimeDataService level as CaseDefinition depends on ProcessDefinition
    }

    @Override
    public void onDeactivate(DeploymentEvent event) {
        // no op - all is done on RuntimeDataService level as CaseDefinition depends on ProcessDefinition
    }

    /*
     * CaseDefinition operations
     */
    

    @Override
    public CaseDefinition getCase(String deploymentId, String caseDefinitionId) {
        return availableCases.stream()
                            .filter(caseDef -> caseDef.getDeploymentId().equals(deploymentId) 
                                    && caseDef.getId().equals(caseDefinitionId))
                            .findFirst()
                            .orElse(null);        
    }
    
    @Override
    public Collection<CaseDefinition> getCases(QueryContext queryContext) {
        Collection<CaseDefinition> cases = availableCases.stream()
                .filter(caseDef -> caseDef.isActive())
                .sorted(new CaseDefinitionComparator(queryContext.getOrderBy(), queryContext.isAscending()))
                .skip(queryContext.getOffset())
                .limit(queryContext.getCount())
                .collect(toList());
        return cases;
    }

    @Override
    public Collection<CaseDefinition> getCases(String filter, QueryContext queryContext) {
        String pattern = "(?i)^.*"+filter+".*$";
        
        Collection<CaseDefinition> cases = availableCases.stream()
                .filter(caseDef -> caseDef.isActive() 
                        && (caseDef.getId().matches(pattern) || caseDef.getName().matches(pattern)))
                .sorted(new CaseDefinitionComparator(queryContext.getOrderBy(), queryContext.isAscending()))
                .skip(queryContext.getOffset())
                .limit(queryContext.getCount())
                .collect(toList());
        return cases;
    }

    @Override
    public Collection<CaseDefinition> getCasesByDeployment(String deploymentId, QueryContext queryContext) {
        Collection<CaseDefinition> cases = availableCases.stream()
                .filter(caseDef -> caseDef.isActive() && caseDef.getDeploymentId().equals(deploymentId))
                .sorted(new CaseDefinitionComparator(queryContext.getOrderBy(), queryContext.isAscending()))
                .skip(queryContext.getOffset())
                .limit(queryContext.getCount())
                .collect(toList());
        return cases;
    }
    
    /*
     * Process definitions related
     */
    @Override
    public Collection<ProcessDefinition> getProcessDefinitions(QueryContext queryContext) {
        Collection<ProcessDefinition> cases = availableProcesses.stream()
                .filter(caseDef -> caseDef.isActive())
                .sorted(new ProcessDefinitionComparator(queryContext.getOrderBy(), queryContext.isAscending()))
                .skip(queryContext.getOffset())
                .limit(queryContext.getCount())
                .collect(toList());
        return cases;
    }

    @Override
    public Collection<ProcessDefinition> getProcessDefinitions(String filter, QueryContext queryContext) {
        String pattern = "(?i)^.*"+filter+".*$";
        
        Collection<ProcessDefinition> cases = availableProcesses.stream()
                .filter(caseDef -> caseDef.isActive() 
                        && (caseDef.getId().matches(pattern) || caseDef.getName().matches(pattern)))
                .sorted(new ProcessDefinitionComparator(queryContext.getOrderBy(), queryContext.isAscending()))
                .skip(queryContext.getOffset())
                .limit(queryContext.getCount())
                .collect(toList());
        return cases;
    }

    @Override
    public Collection<ProcessDefinition> getProcessDefinitionsByDeployment(String deploymentId, QueryContext queryContext) {
        Collection<ProcessDefinition> cases = availableProcesses.stream()
                .filter(caseDef -> caseDef.isActive() && caseDef.getDeploymentId().equals(deploymentId))
                .sorted(new ProcessDefinitionComparator(queryContext.getOrderBy(), queryContext.isAscending()))
                .skip(queryContext.getOffset())
                .limit(queryContext.getCount())
                .collect(toList());
        return cases;
    }
    
    
    /*
     * Case instance and its process instances operations
     */
    

    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesForCase(String caseId, QueryContext queryContext) {
        CorrelationKey correlationKey = correlationKeyFactory.newCorrelationKey(caseId);
        
        return runtimeDataService.getProcessInstancesByCorrelationKey(correlationKey, queryContext);        
    }

    
    @Override
    public Collection<ProcessInstanceDesc> getProcessInstancesForCase(String caseId, List<Integer> processStates, QueryContext queryContext) {
        CorrelationKey correlationKey = correlationKeyFactory.newCorrelationKey(caseId);
        
        return runtimeDataService.getProcessInstancesByCorrelationKeyAndStatus(correlationKey, processStates, queryContext);
    } 
    

    @Override
    public Collection<CaseMilestoneInstance> getCaseInstanceMilestones(String caseId, boolean achievedOnly, QueryContext queryContext) {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceByCorrelationKey(correlationKeyFactory.newCorrelationKey(caseId));        
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new CaseNotFoundException("No case instance found with id " + caseId + " or it's not active anymore");
        }
        CorrelationKey correlationKey = correlationKeyFactory.newCorrelationKey(caseId);
        
        Collection<org.jbpm.services.api.model.NodeInstanceDesc> nodes = runtimeDataService.getNodeInstancesByCorrelationKeyNodeType(correlationKey, 
                                                                                                Arrays.asList(ProcessInstance.STATE_ACTIVE), 
                                                                                                Arrays.asList("MilestoneNode"), 
                                                                                                queryContext);
        
        Collection<Long> completedNodes = nodes.stream().filter(n -> ((NodeInstanceDesc)n).getType() == 1).map(n -> n.getId()).collect(toList());
        Predicate<org.jbpm.services.api.model.NodeInstanceDesc> filterNodes = null;
        if (achievedOnly) {            
            filterNodes = n -> ((NodeInstanceDesc)n).getType() == 1;             
        } else {
            filterNodes = n -> ((NodeInstanceDesc)n).getType() == 0;
        }
        List<String> foundMilestones = new ArrayList<>();
        
        List<CaseMilestoneInstance> milestones = nodes.stream()
        .filter(filterNodes)
        .map(n -> {
            foundMilestones.add(n.getName());
            return new CaseMilestoneInstanceImpl(String.valueOf(n.getId()), n.getName(), completedNodes.contains(n.getId()), n.getDataTimeStamp());        
        })
        .collect(toList());
        
        if (!achievedOnly) {
            // add other milestones that are present in the definition
            CaseDefinition caseDef = getCase(pi.getDeploymentId(), pi.getProcessId());
            caseDef.getCaseMilestones().stream()
            .filter(cm -> !foundMilestones.contains(cm.getName()))
            .map(cm -> new CaseMilestoneInstanceImpl(cm.getId(), cm.getName(), false, null))
            .forEach(cmi -> milestones.add(cmi));
        }
        
        return applyPagination(milestones, queryContext);
    }

    @Override
    public Collection<CaseStageInstance> getCaseInstanceStages(String caseId, boolean activeOnly, QueryContext queryContext) {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceByCorrelationKey(correlationKeyFactory.newCorrelationKey(caseId));        
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new CaseNotFoundException("No case instance found with id " + caseId + " or it's not active anymore");
        }
        
        CaseDefinition caseDef = getCase(pi.getDeploymentId(), pi.getProcessId());
        List<CaseStageInstance> stages = internalGetCaseStages(caseDef, caseId, activeOnly, queryContext);
        
        return applyPagination(stages, queryContext);
    }
    
    /*
     * Case instance queries
     */
    

    @Override
    public Collection<org.jbpm.services.api.model.NodeInstanceDesc> getActiveNodesForCase(String caseId, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseId + "%");
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        List<org.jbpm.services.api.model.NodeInstanceDesc> nodeInstances =  commandService.execute(new QueryNameCommand<List<org.jbpm.services.api.model.NodeInstanceDesc>>("getActiveNodesForCase", params));
        return nodeInstances;
    }
    
    @Override
    public Collection<org.jbpm.services.api.model.NodeInstanceDesc> getCompletedNodesForCase(String caseId, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseId + "%");
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        List<org.jbpm.services.api.model.NodeInstanceDesc> nodeInstances =  commandService.execute(new QueryNameCommand<List<org.jbpm.services.api.model.NodeInstanceDesc>>("getCompletedNodesForCase", params));
        return nodeInstances;
    }


    @Override
    public Collection<CaseInstance> getCaseInstances(QueryContext queryContext) {

        return getCaseInstances(statuses, queryContext);
    }
    
    @Override
    public Collection<CaseInstance> getCaseInstances(List<CaseStatus> statuses, QueryContext queryContext) {
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("statuses", resolveCaseStatuses(statuses));
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        List<CaseInstance> processInstances =  commandService.execute(new QueryNameCommand<List<CaseInstance>>("getCaseInstances", params));

        return processInstances;
    }


    @Override
    public Collection<CaseInstance> getCaseInstancesByDeployment(String deploymentId, List<CaseStatus> statuses, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("deploymentId", deploymentId);
        params.put("statuses", resolveCaseStatuses(statuses));
        params.put("entities", collectUserAuthInfo());
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        List<CaseInstance> processInstances =  commandService.execute(new QueryNameCommand<List<CaseInstance>>("getCaseInstancesByDeployment", params));

        return processInstances;
    }


    @Override
    public Collection<CaseInstance> getCaseInstancesByDefinition(String definitionId, List<CaseStatus> statuses, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("definitionId", definitionId);
        params.put("statuses", resolveCaseStatuses(statuses));
        params.put("entities", collectUserAuthInfo());
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        List<CaseInstance> processInstances =  commandService.execute(new QueryNameCommand<List<CaseInstance>>("getCaseInstancesByDefinition", params));

        return processInstances;
    }


    @Override
    public Collection<CaseInstance> getCaseInstancesOwnedBy(String owner, List<CaseStatus> statuses, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("owner", owner);
        params.put("statuses", resolveCaseStatuses(statuses));
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        List<CaseInstance> processInstances =  commandService.execute(new QueryNameCommand<List<CaseInstance>>("getCaseInstancesOwnedBy", params));

        return processInstances;
    }
    
    @Override
    public Collection<CaseInstance> getCaseInstancesByRole(String roleName, List<CaseStatus> statuses, QueryContext queryContext) {
   
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("roleName", roleName);
        params.put("entities", collectUserAuthInfo());
        params.put("statuses", resolveCaseStatuses(statuses));
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        List<CaseInstance> processInstances =  commandService.execute(new QueryNameCommand<List<CaseInstance>>("getCaseInstancesByRole", params));

        return processInstances;
    }
    
    @Override
    public Collection<CaseInstance> getCaseInstancesAnyRole(List<CaseStatus> statuses, QueryContext queryContext) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("entities", collectUserAuthInfo());
        params.put("statuses", resolveCaseStatuses(statuses));
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        List<CaseInstance> processInstances =  commandService.execute(new QueryNameCommand<List<CaseInstance>>("getCaseInstancesAnyRole", params));

        return processInstances;
    }
    
    @Override
    public Collection<CaseInstance> getCaseInstancesByDataItem(String dataItemName, List<CaseStatus> statuses, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("itemName", dataItemName);
        params.put("entities", collectUserAuthInfo());
        params.put("statuses", resolveCaseStatuses(statuses));
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        List<CaseInstance> processInstances =  commandService.execute(new QueryNameCommand<List<CaseInstance>>("getCaseInstancesByVariableName", params));

        return processInstances;
    }


    @Override
    public Collection<CaseInstance> getCaseInstancesByDataItemAndValue(String dataItemName, String dataItemValue, List<CaseStatus> statuses, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("itemName", dataItemName);
        params.put("itemValue", dataItemValue);
        params.put("entities", collectUserAuthInfo());
        params.put("statuses", resolveCaseStatuses(statuses));
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        List<CaseInstance> processInstances =  commandService.execute(new QueryNameCommand<List<CaseInstance>>("getCaseInstancesByVariableNameAndValue", params));

        return processInstances;
    }
    
    @Override
    public CaseInstance getCaseInstanceById(String caseId) {
                
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("correlationKey", caseId);
        params.put("maxResults", 1);
        
        List<CaseInstance> processInstances =  commandService.execute(new QueryNameCommand<List<CaseInstance>>("getCaseInstanceById", params));
        if (!processInstances.isEmpty()) {
            return processInstances.get(0);
        }        
        
        throw new CaseNotFoundException("Case " + caseId + " was not found");
    }
    
    @Override
    public Collection<AdHocFragment> getAdHocFragmentsForCase(String caseId) {
        ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceByCorrelationKey(correlationKeyFactory.newCorrelationKey(caseId));        
        if (pi == null || !pi.getState().equals(ProcessInstance.STATE_ACTIVE)) {
            throw new CaseNotFoundException("No case instance found with id " + caseId + " or it's not active anymore");
        }
        
        CaseDefinition caseDef = getCase(pi.getDeploymentId(), pi.getProcessId());
        List<AdHocFragment> adHocFragments = new ArrayList<>();
        adHocFragments.addAll(caseDef.getAdHocFragments());
        
        Collection<CaseStageInstance> activeStages = internalGetCaseStages(caseDef, caseId, true, new QueryContext(0, 100));
        activeStages.forEach(stage -> adHocFragments.addAll(stage.getAdHocFragments()));
        
        return adHocFragments;
    }
    
    /*
     * Task related queries
     */   

    @Override
    public List<TaskSummary> getCaseTasksAssignedAsPotentialOwner(String caseId, String userId, List<Status> status, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseId + "%");
        params.put("userId", userId);
        params.put("status", adoptList(status, allActiveStatus));
        params.put("groupIds", getAuthenticatedUserRoles(identityProvider));
        applyQueryContext(params, queryContext);
        List<TaskSummary> tasks =  commandService.execute(new QueryNameCommand<List<TaskSummary>>("getCaseTasksAsPotentialOwner", params));
        return tasks;
    }


    @Override
    public List<TaskSummary> getCaseTasksAssignedAsBusinessAdmin(String caseId, String userId, List<Status> status, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseId + "%");
        params.put("userId", userId);
        params.put("status", adoptList(status, allActiveStatus));
        params.put("groupIds", getAuthenticatedUserRoles(identityProvider));
        applyQueryContext(params, queryContext);
        List<TaskSummary> tasks =  commandService.execute(new QueryNameCommand<List<TaskSummary>>("getCaseTasksAsBusinessAdmin", params));
        return tasks;
    }
    
    @Override
    public List<TaskSummary> getCaseTasksAssignedAsStakeholder(String caseId, String userId, List<Status> status, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseId + "%");
        params.put("userId", userId);
        params.put("status", adoptList(status, allActiveStatus));
        params.put("groupIds", getAuthenticatedUserRoles(identityProvider));
        applyQueryContext(params, queryContext);
        List<TaskSummary> tasks =  commandService.execute(new QueryNameCommand<List<TaskSummary>>("getCaseTasksAsStakeholder", params));
        return tasks;
    }
    
    /*
     * Helper methods to parse process and extract case related information
     */
    
    public List<CaseStageInstance> internalGetCaseStages(CaseDefinition caseDef, String caseId, boolean activeOnly, QueryContext queryContext) {
        
        CorrelationKey correlationKey = correlationKeyFactory.newCorrelationKey(caseId);
        Collection<org.jbpm.services.api.model.NodeInstanceDesc> nodes = runtimeDataService.getNodeInstancesByCorrelationKeyNodeType(correlationKey, 
                                                                                            Arrays.asList(ProcessInstance.STATE_ACTIVE), 
                                                                                            Arrays.asList("DynamicNode"), 
                                                                                            queryContext);
        Collection<Long> completedNodes = nodes.stream().filter(n -> ((NodeInstanceDesc)n).getType() == 1).map(n -> n.getId()).collect(toList());
        
        Map<String, CaseStage> stagesByName = caseDef.getCaseStages().stream()
        .collect(toMap(CaseStage::getId, c -> c)); 
        Predicate<org.jbpm.services.api.model.NodeInstanceDesc> filterNodes = null;
        if (activeOnly) {
            
            filterNodes = n -> ((NodeInstanceDesc)n).getType() == 0 && !completedNodes.contains(((NodeInstanceDesc)n).getId());             
        } else {
            filterNodes = n -> ((NodeInstanceDesc)n).getType() == 0;
        }
        
        List<String> triggeredStages = new ArrayList<>();
        List<CaseStageInstance> stages = new ArrayList<>();
        nodes.stream()
        .filter(filterNodes)
        .map(n -> {
            StageStatus status = StageStatus.Active;
            if (completedNodes.contains(((NodeInstanceDesc)n).getId())) {
                status = StageStatus.Completed;
            }
            Collection<org.jbpm.services.api.model.NodeInstanceDesc> activeNodes = getActiveNodesForCaseAndStage(caseId, n.getNodeId(), new QueryContext(0, 100));
            return new CaseStageInstanceImpl(n.getNodeId(), n.getName(), stagesByName.get(n.getNodeId()).getAdHocFragments(), activeNodes, status);
            })
        .forEach(csi -> {
            stages.add(csi);
            triggeredStages.add(csi.getName());                        
        });
        
        
        if (!activeOnly) {
            // add other stages that are present in the definition            
            caseDef.getCaseStages().stream()
            .filter(cs -> !triggeredStages.contains(cs.getName()))
            .map(cs -> new CaseStageInstanceImpl(cs.getId(), cs.getName(), cs.getAdHocFragments(), Collections.emptyList(), StageStatus.Available))
            .forEach(csi -> stages.add(csi));
        }
        
        return stages;
    }
    
    
    protected Collection<org.jbpm.services.api.model.NodeInstanceDesc> getActiveNodesForCaseAndStage(String caseId, String stageId, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseId + "%");
        params.put("nodeContainerId", stageId);
        applyQueryContext(params, queryContext);
        applyDeploymentFilter(params);
        List<org.jbpm.services.api.model.NodeInstanceDesc> nodeInstances =  commandService.execute(new QueryNameCommand<List<org.jbpm.services.api.model.NodeInstanceDesc>>("getActiveNodesForCaseAndStage", params));
        return nodeInstances;
    }
    
    private Collection<CaseRole> collectCaseRoles(Process process) {
        
        String roles = (String) process.getMetaData().get("customCaseRoles");
        if (roles == null) {
            return Collections.emptyList();
        }
        List<CaseRole> result = new ArrayList<CaseRole>();
        String[] roleStrings = roles.split(",");
        for (String roleString: roleStrings) {
            String[] elements = roleString.split(":");
            CaseRoleImpl role = new CaseRoleImpl(elements[0]);
            result.add(role);
            if (elements.length > 1) {
                role.setCardinality(Integer.parseInt(elements[1]));
            }
        }
        return result;
    }
    

    private String collectCaseIdPrefix(Process process) {
        String caseIdPrefix = (String) process.getMetaData().get("customCaseIdPrefix");
        if (caseIdPrefix == null) {
            return CaseDefinition.DEFAULT_PREFIX;
        }
        
        return caseIdPrefix;
    }

    private Collection<CaseMilestone> collectMilestoness(Process process) {
        Collection<CaseMilestone> result = new ArrayList<CaseMilestone>();
        getMilestones((WorkflowProcess) process, result);
        
        return result;
    }
    
    private void getMilestones(NodeContainer container, Collection<CaseMilestone> result) {
        for (Node node: container.getNodes()) {
            if (node instanceof MilestoneNode) {                
                result.add(new CaseMilestoneImpl((String) node.getMetaData().get("UniqueId"), node.getName(), ((MilestoneNode) node).getConstraint(), false));               
            }
            if (node instanceof NodeContainer) {
                getMilestones((NodeContainer) node, result);
            }
        }
    }
    
    private Collection<CaseStage> collectCaseStages(String deploymentId, String processId, NodeContainer process) {
        Collection<CaseStage> result = new ArrayList<CaseStage>();
        
        for (Node node : process.getNodes()) {
            if (node instanceof DynamicNode) {
                DynamicNode dynamicNode = (DynamicNode) node;
                Collection<AdHocFragment> adHocFragments = collectAdHocFragments(dynamicNode);
                
                result.add(new CaseStageImpl((String) ((DynamicNode) node).getMetaData("UniqueId"),
                                             node.getName(),
                                             adHocFragments));
            }
        }
        return result;
    }


    private Collection<AdHocFragment> collectAdHocFragments(NodeContainer process) {
        List<AdHocFragment> result = new ArrayList<AdHocFragment>();
        
        checkAdHoc(process, result);
        
        return result;
    }
    
    private Map<String, List<String>> collectDataAccessRestrictions(Process process) {
        // expected format: item:role1,role2;item2:role2,role3
        // where item and item2 are the case file data item names and role1, role2, role3 are case roles
        String dataAccess = (String) process.getMetaData().get("customCaseDataAccess");
        if (dataAccess == null) {
            return new HashMap<>();
        }
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        String[] accessStrings = dataAccess.split(";");
        for (String accessString: accessStrings) {
            String[] elements = accessString.split(":");
            
            String dataItem = elements[0];
            List<String> roles = Arrays.asList(elements[1].split(","));
            
            result.put(dataItem, roles);
            
        }
        return result;
    }
    
    private void checkAdHoc(NodeContainer nodeContainer, List<AdHocFragment> result) {
        for (Node node : nodeContainer.getNodes()) {
            if (node instanceof StartNode || node instanceof BoundaryEventNode) {
                continue;
            }
            if (node.getIncomingConnections().isEmpty()) {
                result.add(new AdHocFragmentImpl(node.getName(), node.getClass().getSimpleName()));
            }
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
        if (deploymentRolesManager != null) {
            List<String> deploymentIdForUser = deploymentRolesManager.getDeploymentsForUser(identityProvider);
    
            if (deploymentIdForUser != null && !deploymentIdForUser.isEmpty()) {
                params.put(FILTER, " log.externalId in (:deployments) ");
                params.put("deployments", deploymentIdForUser);
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


    protected List<String> collectUserAuthInfo() {
        List<String> entities = new ArrayList<>();
        entities.add(identityProvider.getName());
        entities.addAll(getAuthenticatedUserRoles(identityProvider));
        
        // add special public role to allow to find cases that do not use case roles
        entities.add(AuthorizationManager.PUBLIC_GROUP);
        
        return entities;
    }
    
    protected <T> Collection<T> applyPagination(List<T> input, QueryContext queryContext) {
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

    protected List<Integer> resolveCaseStatuses(List<CaseStatus> caseStatusesList) {
        return caseStatusesList != null ? caseStatusesList.stream().map(event -> event.getId()).collect(Collectors.toList()) : null;
    }


    @Override
    public Collection<CaseFileItem> getCaseInstanceDataItems(String caseId, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseId);        
        params.put("entities", collectUserAuthInfo());
        applyQueryContext(params, queryContext);
        List<CaseFileItem> caseFileItems = commandService.execute(new QueryNameCommand<List<CaseFileItem>>("getCaseInstanceDataItems", params));

        return caseFileItems;
    }


    @Override
    public Collection<CaseFileItem> getCaseInstanceDataItemsByName(String caseId, List<String> names, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseId);
        params.put("itemNames", names); 
        params.put("entities", collectUserAuthInfo());
        applyQueryContext(params, queryContext);
        List<CaseFileItem> caseFileItems = commandService.execute(new QueryNameCommand<List<CaseFileItem>>("getCaseInstanceDataItemsByName", params));

        return caseFileItems;
    }


    @Override
    public Collection<CaseFileItem> getCaseInstanceDataItemsByType(String caseId, List<String> types, QueryContext queryContext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseId); 
        params.put("itemTypes", types); 
        params.put("entities", collectUserAuthInfo());
        applyQueryContext(params, queryContext);
        List<CaseFileItem> caseFileItems = commandService.execute(new QueryNameCommand<List<CaseFileItem>>("getCaseInstanceDataItemsByType", params));

        return caseFileItems;
    }

}
