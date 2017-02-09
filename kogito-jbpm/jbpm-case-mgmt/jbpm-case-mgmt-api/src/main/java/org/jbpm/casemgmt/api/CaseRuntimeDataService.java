/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.api;

import java.util.Collection;
import java.util.List;

import org.jbpm.casemgmt.api.model.AdHocFragment;
import org.jbpm.casemgmt.api.model.CaseDefinition;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.instance.CaseMilestoneInstance;
import org.jbpm.casemgmt.api.model.instance.CaseStageInstance;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryContext;

/**
 * Provides access to case(s) and its runtime data such as:
 * <ul>
 *  <li>available cases</li>
 *  <li>available milestones</li>
 *  <li>available stages</li>
 * </ul>
 */
public interface CaseRuntimeDataService {

    /*
     * Case definitions related
     */
    /**
     * Returns case definition identified by caseDefinitionId that belongs to given deploymentId.
     * @param deploymentId deployment identifier that case definition is part of
     * @param caseDefinitionId id of the case
     *
     */
    CaseDefinition getCase(String deploymentId, String caseDefinitionId);
    
    /**
     * Returns available cases.
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<CaseDefinition> getCases(QueryContext queryContext);
    
    /**
     * Returns available cases which matching filter that applies to case name (usually represented by process id or name).
     * @param filter filter for case name to narrow down results
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<CaseDefinition> getCases(String filter, QueryContext queryContext);
    
    /**
     * Returns available cases for given deployment id
     * @param deploymentId deployment identifier that case definition is part of 
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<CaseDefinition> getCasesByDeployment(String deploymentId, QueryContext queryContext);
    
    /*
     * Process definition related
     */
    
    /**
     * Returns process definitions that are not considered as case definitions.
     * @param queryContext control parameters for the result e.g. sorting, paging 
     */
    Collection<ProcessDefinition> getProcessDefinitions(QueryContext queryContext);
    
    /**
     * Returns process definitions that are not considered as case definitions and are filtered by process id or name.
     * @param filter regex based filter for either name or id of the process
     * @param queryContext control parameters for the result e.g. sorting, paging 
     */
    Collection<ProcessDefinition> getProcessDefinitions(String filter, QueryContext queryContext);
    
    /**
     * Returns process definitions that are not considered as case definitions that belongs to given deployment.
     * @param deploymentId deployment id the processes should be found for
     * @param queryContext control parameters for the result e.g. sorting, paging 
     */
    Collection<ProcessDefinition> getProcessDefinitionsByDeployment(String deploymentId, QueryContext queryContext);
    
    /*
     * Case instance related
     */
 
    /**
     * Returns process instances found for given case id.
     * @param caseId unique id of the case
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<ProcessInstanceDesc> getProcessInstancesForCase(String caseId, QueryContext queryContext);
    
    /**
     * Returns process instances found for given case id.
     * @param caseId unique id of the case
     * @param states states representing process instance (active, completed, aborted)
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<ProcessInstanceDesc> getProcessInstancesForCase(String caseId, List<Integer> states, QueryContext queryContext);
    
    /**
     * Returns milestones for given case instance, identified by case id. 
     * @param caseId unique id of the case
     * @param achievedOnly filter option to return only these that have already been achieved
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<CaseMilestoneInstance> getCaseInstanceMilestones(String caseId, boolean achievedOnly, QueryContext queryContext);
    
    /**
     * Returns stages of given case instance, identified by case id.
     * @param caseId unique id of the case
     * @param activeOnly filter option to return only stages that are active
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<CaseStageInstance> getCaseInstanceStages(String caseId, boolean activeOnly, QueryContext queryContext);
    
    /**
     * Returns active nodes in given case regardless in what process instance they belong to.
     * @param caseId unique id of the case
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<NodeInstanceDesc> getActiveNodesForCase(String caseId, QueryContext queryContext);
    
    /**
     * Returns completed nodes in given case regardless in what process instance they belong to.
     * @param caseId unique id of the case
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<NodeInstanceDesc> getCompletedNodesForCase(String caseId, QueryContext queryContext);
    
    /**
     * Returns list of AdHocFragments available in given case. It includes all ad hoc fragments that are 
     * eligible for triggering - meaning it's container is active (case instance or stage)
     * @param caseId unique id of the case
     */
    Collection<AdHocFragment> getAdHocFragmentsForCase(String caseId);
    
    /**
     * Returns case instance for given case id if the caller of this class is authorized to see the case instance
     * @param caseId unique case id of the case 
     * @return case instance with basic details or null in case it was not found or access was not granted
     */
    CaseInstance getCaseInstanceById(String caseId);
    
    /*
     * Case instance query related
     */
    
    /**
     * Returns all available active case instances
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<CaseInstance> getCaseInstances(QueryContext queryContext);
   
    /**
     * Returns all available active case instances that match given statuses
     * @param statuses list of statuses that case should be in to match
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<CaseInstance> getCaseInstances(List<Integer> statuses, QueryContext queryContext);
    
    /**
     * Returns all available case instances;
     * @param deploymentId deployment identifier that case instance is part of
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<CaseInstance> getCaseInstancesByDeployment(String deploymentId, List<Integer> statuses, QueryContext queryContext);
    
    /**
     * Returns all available case instances;
     * @param caseDefinitionId case definition id
     * @param queryContext control parameters for the result e.g. sorting, paging 
     *
     */
    Collection<CaseInstance> getCaseInstancesByDefinition(String caseDefinitionId, List<Integer> statuses, QueryContext queryContext);
    
    /**
     * Returns all case instances owned by given user
     * @param queryContext control parameters for the result e.g. sorting, paging
     *
     */
    Collection<CaseInstance> getCaseInstancesOwnedBy(String owner, List<Integer> statuses, QueryContext queryContext);
    
    /**
     * Returns cases instances that given user (via identity provider) has access to with given role.
     * @param roleName name of the role that user should be
     * @param statuses statuses of the case instances
     * @param queryContext control parameters for the result e.g. sorting, paging
     */
    Collection<CaseInstance> getCaseInstancesByRole(String roleName, List<Integer> statuses, QueryContext queryContext);
    
    /**
     * Returns case instances that given user (via identity provider) is involved in in any role.
     * @param statuses statuses of the case instances
     * @param queryContext control parameters for the result e.g. sorting, paging
     */
    Collection<CaseInstance> getCaseInstancesAnyRole(List<Integer> statuses, QueryContext queryContext);
    
    /**
     * Returns all tasks associated with given case id that are eligible for user to see.
     * @param caseId unique id of the case 
     * @param userId user id that the tasks should be available for
     * @param status status of the task
     * @param queryContext control parameters for the result e.g. sorting, paging 
     * @return list of tasks found for given case id and user
     */
    List<TaskSummary> getCaseTasksAssignedAsPotentialOwner(String caseId, String userId, List<Status> status, QueryContext queryContext);
    
    /**
     * Returns all tasks associated with given case id that are eligible for user to see as business admin role.
     * @param caseId unique id of the case 
     * @param userId user id that the tasks should be available for
     * @param status status of the task
     * @param queryContext control parameters for the result e.g. sorting, paging 
     * @return list of tasks found for given case id and user
     */
    List<TaskSummary> getCaseTasksAssignedAsBusinessAdmin(String caseId, String userId, List<Status> status, QueryContext queryContext);
    
    /**
     * Returns all tasks associated with given case id that are eligible for user to see in task stakeholder role.
     * @param caseId unique id of the case 
     * @param userId user id that the tasks should be available for
     * @param status status of the task
     * @param queryContext control parameters for the result e.g. sorting, paging 
     * @return list of tasks found for given case id and user
     */
    List<TaskSummary> getCaseTasksAssignedAsStakeholder(String caseId, String userId, List<Status> status, QueryContext queryContext);
    
}
