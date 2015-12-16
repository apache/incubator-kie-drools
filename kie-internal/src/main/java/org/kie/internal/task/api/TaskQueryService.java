/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.kie.internal.task.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.query.data.QueryData;


/**
 * The Task Query Service will contain all the methods 
 *  to get information about the current Task Instances. 
 *  Most of the times these methods will be used to build
 *  User Interfaces, and we should not include any method 
 *  related with Task Statistics here. 
 */
public interface TaskQueryService {
    
    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, List<Status> status, QueryFilter filter);
    
    List<TaskSummary> getTasksOwned(String userId, List<Status> status, QueryFilter filter);
    
    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds);
    
    List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, List<String> groupIds);
    
    List<TaskSummary> getTasksAssignedAsBusinessAdministratorByStatus(String userId, List<String> groupIds, List<Status> status);

    List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId);
    
    List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<String> groupIds, List<Status> status, Date expirationDate);
    
    List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<String> groupIds, List<Status> status, Date expirationDate);
    
    List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<Status> status, Date expirationDate);
    
    List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<Status> status, Date expirationDate);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, int firstResult, int maxResults);

    List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status);
    
    List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status);
    
    List<TaskSummary> getTasksAssignedAsRecipient(String userId);

    List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId);

    List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId);

    List<TaskSummary> getTasksAssignedByGroup(String groupId);
    
    List<TaskSummary> getTasksAssignedByGroups(List<String> groupsId);
    
    List<TaskSummary> getTasksAssignedByGroupsByExpirationDate(List<String> groupIds,  Date expirationDate);
    
    List<TaskSummary> getTasksAssignedByGroupsByExpirationDateOptional(List<String> groupIds,  Date expirationDate);
    
    List<TaskSummary> getTasksOwned(String userId);

    List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status);
    
    List<TaskSummary> getTasksOwnedByExpirationDate(String userId, List<Status> status, Date expirationDate);
    
    List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> status, Date expirationDate);
    
    List<TaskSummary> getTasksOwnedByExpirationDateBeforeSpecifiedDate(String userId, List<Status> status, Date date);
    
    List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId);
    
    List<TaskSummary> getSubTasksByParent(long parentId);
    
    List<TaskSummary> getTasksByStatusByProcessInstanceId(long processInstanceId, List<Status> status);

    List<TaskSummary> getTasksByStatusByProcessInstanceIdByTaskName(long processInstanceId, List<Status> status, String taskName);

    int getPendingSubTasksByParent(long parentId);
    
    Task getTaskByWorkItemId(long workItemId);
    
    Task getTaskInstanceById(long taskId);
    
    List<Long> getTasksByProcessInstanceId(long processInstanceId);
    
    Map<Long, List<OrganizationalEntity>> getPotentialOwnersForTaskIds(List<Long> taskIds);
  
    int getCompletedTaskByUserId(String userId);
    int getPendingTaskByUserId(String userId);

    /**
     * This method will no longer be available as of jBPM 7.x
     * 
     * @param parameters A map of parameters to add to the query
     * @param union Whether the query should return a union or intersection of the criteria (parameters)
     * 
     * @see {@link #query(String, QueryData)}
     * @return
     */
    @Deprecated
    List<TaskSummary> getTasksByVariousFields( String userId, Map<String, List<?>> parameters, boolean union);
    
    
    /**
     * 
     * @param queryWhere
     * @return
     */
    List<TaskSummary> query( String userId, Object queryWhere );
}
