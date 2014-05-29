/**
 * Copyright 2013 JBoss Inc
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

package org.jbpm.services.task.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jbpm.services.task.query.QueryFilterImpl;

import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.task.api.QueryFilter;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.model.InternalTaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TaskQueryServiceImpl implements TaskQueryService {

    private static final Logger logger = LoggerFactory.getLogger(TaskQueryServiceImpl.class);
    
    private TaskPersistenceContext persistenceContext;

    public TaskQueryServiceImpl() {
    }
    
    public TaskQueryServiceImpl(TaskPersistenceContext persistenceContext) {
    	this.persistenceContext = persistenceContext;
    }

    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsBusinessAdministrator",
        		persistenceContext.addParametersToMap("userId", userId),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsExcludedOwner", 
        		persistenceContext.addParametersToMap("userId", userId),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("groupIds", groupIds);
       
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerWithGroups", 
                params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedByGroup(String groupId) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroup", 
                persistenceContext.addParametersToMap("groupId", groupId ),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedByGroupsByExpirationDateOptional(List<String> groupIds, Date expirationDate) {
        List<Object[]> tasksByGroups = (List<Object[]>)persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroupsByExpirationDateOptional", 
                persistenceContext.addParametersToMap("groupIds", groupIds, "expirationDate", expirationDate),
                ClassUtil.<List<Object[]>>castClass(List.class));
                
        return collectTasksByPotentialOwners(tasksByGroups);
    }  
    
    protected List<TaskSummary> collectTasksByPotentialOwners(List<Object[]> tasksByGroups) {
        Set<Long> tasksIds = Collections.synchronizedSet(new HashSet<Long>());
        Map<Long, List<String>> potentialOwners = Collections.synchronizedMap(new HashMap<Long, List<String>>());
        for (Object o : tasksByGroups) {
            Object[] get = (Object[]) o;
            tasksIds.add((Long) get[0]);
            if (potentialOwners.get((Long) get[0]) == null) {
                potentialOwners.put((Long) get[0], new ArrayList<String>());
            }
            potentialOwners.get((Long) get[0]).add((String) get[1]);
        }
        if (!tasksIds.isEmpty()) {
            List<TaskSummary> tasks = (List<TaskSummary>)persistenceContext.queryWithParametersInTransaction("TaskSummariesByIds", 
                    persistenceContext.addParametersToMap("taskIds", tasksIds),
                    ClassUtil.<List<TaskSummary>>castClass(List.class));
                    

            for (TaskSummary ts : tasks) {
                ((InternalTaskSummary) ts).setPotentialOwners(potentialOwners.get(ts.getId()));
            }
            return tasks;
        }
        return new ArrayList<TaskSummary>();
    }
    
    public List<TaskSummary> getTasksAssignedByGroupsByExpirationDate(List<String> groupIds, Date expirationDate) {

        List<Object[]> tasksByGroups = (List<Object[]>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroupsByExpirationDate", 
                persistenceContext.addParametersToMap("groupIds", groupIds, "expirationDate", expirationDate),
                ClassUtil.<List<Object[]>>castClass(List.class));
        return collectTasksByPotentialOwners(tasksByGroups);
    }        
            
    public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds) {

        List<Object[]> tasksByGroups = (List<Object[]>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroups", 
                persistenceContext.addParametersToMap("groupIds", groupIds),
                ClassUtil.<List<Object[]>>castClass(List.class));
                
        Set<Long> tasksIds = Collections.synchronizedSet(new HashSet<Long>());
        Map<Long, List<String>> potentialOwners = Collections.synchronizedMap(new HashMap<Long, List<String>>());
        for (Object o : tasksByGroups) {
            Object[] get = (Object[]) o;
            tasksIds.add((Long) get[0]);
            if (potentialOwners.get((Long) get[0]) == null) {
                potentialOwners.put((Long) get[0], new ArrayList<String>());
            }
            potentialOwners.get((Long) get[0]).add((String) get[1]);
        }
        if (!tasksIds.isEmpty()) {
            List<TaskSummary> tasks = (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TaskSummariesByIds", 
                        persistenceContext.addParametersToMap("taskIds", tasksIds),
                        ClassUtil.<List<TaskSummary>>castClass(List.class));

            for (TaskSummary ts : tasks) {
                ((InternalTaskSummary) ts).setPotentialOwners(potentialOwners.get(ts.getId()));
            }
            return tasks;
        }
        return new ArrayList<TaskSummary>();
    }

    public Map<Long, List<OrganizationalEntity>> getPotentialOwnersForTaskIds(List<Long> taskIds){
        List<Object[]> potentialOwners = persistenceContext.queryWithParametersInTransaction("GetPotentialOwnersForTaskIds", 
                persistenceContext.addParametersToMap("taskIds", taskIds),
                ClassUtil.<List<Object[]>>castClass(List.class));
        
        Map<Long, List<OrganizationalEntity>> potentialOwnersMap = new HashMap<Long, List<OrganizationalEntity>>();
        Long currentTaskId = 0L;
        for(Object[] item : potentialOwners){
            Long taskId = (Long) item[0];
            OrganizationalEntity potentialOwner = (OrganizationalEntity)item[1];
            if(currentTaskId != taskId){
                currentTaskId = taskId;
            }
            
            if(potentialOwnersMap.get(currentTaskId) == null){
                potentialOwnersMap.put(currentTaskId, new ArrayList<OrganizationalEntity>());
            }
            potentialOwnersMap.get(currentTaskId).add(potentialOwner);
        }
        
        return potentialOwnersMap;
    
    }
    
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, int firstResult, int maxResults) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerWithGroups", 
                                    persistenceContext.addParametersToMap("userId", userId, "groupIds", groupIds, 
                                                    "firstResult", firstResult, "maxResults", maxResults),
                                                    ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(String userId) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsRecipient", 
                persistenceContext.addParametersToMap("userId", userId),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId) {
        return (List<TaskSummary>)  persistenceContext.queryWithParametersInTransaction("TasksAssignedAsTaskInitiator", 
                persistenceContext.addParametersToMap("userId", userId),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsTaskStakeholder", 
                persistenceContext.addParametersToMap("userId", userId),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksOwned(String userId) {
        return getTasksOwned(userId, null, null);

    }
    
   
    

    public List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status) {

        List<TaskSummary> taskOwned =  getTasksOwned(userId, null, null);

        if (!taskOwned.isEmpty()) {
            Set<Long> tasksIds = new HashSet<Long>();
            for (TaskSummary ts : taskOwned) {
                tasksIds.add(ts.getId());
            }

            List<Object[]> tasksPotentialOwners = (List<Object[]>) persistenceContext.queryWithParametersInTransaction("TasksOwnedPotentialOwnersByTaskIds",
                        persistenceContext.addParametersToMap("taskIds", tasksIds),
                        ClassUtil.<List<Object[]>>castClass(List.class));

            Map<Long, List<String>> potentialOwners = new HashMap<Long, List<String>>();
            for (Object o : tasksPotentialOwners) {
                Object[] get = (Object[]) o;
                tasksIds.add((Long) get[0]);
                if (potentialOwners.get((Long) get[0]) == null) {
                    potentialOwners.put((Long) get[0], new ArrayList<String>());
                }
                potentialOwners.get((Long) get[0]).add((String) get[1]);
            }
            for (TaskSummary ts : taskOwned) {
                ((InternalTaskSummary) ts).setPotentialOwners(potentialOwners.get(ts.getId()));
            }
        } else {
            return new ArrayList<TaskSummary>(0);
        }

        return taskOwned;
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status) {
        return getTasksAssignedAsPotentialOwner(userId, null, status, null);
                
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, List<Status> status, QueryFilter filter) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("status", status);
        params.put("groupIds", groupIds);
        if(filter != null){
           params.put("firstResult",filter.getOffset());
           params.put("maxResults", filter.getCount());
           if(!"".equals(filter.getFilterParams())){
               for(String key : filter.getParams().keySet()){
                   params.put(key, filter.getParams().get(key));
               }
               return (List<TaskSummary>) persistenceContext
                       .queryStringWithParametersInTransaction(TASKS_ASSIGNED_AS_POTENTIALOWNER_TEMPLATE + filter.getFilterParams() + " " +filter.getOrderBy(), 
                                        params,
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
           
           }
        }
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("NewTasksAssignedAsPotentialOwner", 
                                        params,
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
                
    }

    public List<TaskSummary> getTasksOwned(String userId, List<Status> status, QueryFilter filter) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        if(status == null){
            status = new ArrayList<Status>();
            status.add(Status.Reserved);
            status.add(Status.InProgress);
        }
        params.put("status", status);
        if(filter != null){
           params.put("firstResult",filter.getOffset());
           params.put("maxResults", filter.getCount());
           if(!"".equals(filter.getFilterParams())){
               for(String key : filter.getParams().keySet()){
                   params.put(key, filter.getParams().get(key));
               }
               return (List<TaskSummary>) persistenceContext
                       .queryStringWithParametersInTransaction(TASKS_OWNED_TEMPLATE + filter.getFilterParams() + " " +filter.getOrderBy(), 
                                        params,
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
           
           } 
        }
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("NewTasksOwned", 
                                        params,
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
    }
    
    
    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("SubTasksAssignedAsPotentialOwner",
                                        persistenceContext.addParametersToMap("parentId", parentId, "userId", userId),
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
                
    }

    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("GetSubTasksByParentTaskId", 
                persistenceContext.addParametersToMap("parentId", parentId),
                ClassUtil.<List<TaskSummary>>castClass(List.class)); 
                
    }

    public int getPendingSubTasksByParent(long parentId) {
        return  persistenceContext.queryWithParametersInTransaction("GetSubTasksByParentTaskId", 
                                persistenceContext.addParametersToMap("parentId", parentId),
                                ClassUtil.<List<TaskSummary>>castClass(List.class)).size();
    }

    public Task getTaskInstanceById(long taskId) {
        Task taskInstance = persistenceContext.findTask(taskId);
        return taskInstance;

    }

    public Task getTaskByWorkItemId(long workItemId) {
        List<Task> tasks = (List<Task>)persistenceContext.queryWithParametersInTransaction("TaskByWorkItemId", 
                                persistenceContext.addParametersToMap("workItemId", workItemId,"maxResults", 1),
                                ClassUtil.<List<Task>>castClass(List.class));
        if (tasks.isEmpty())
            return null;
        else 
            return (Task) (tasks.get(0));
    }
    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<String> groupsIds,
                                            List<Status> status, Date expirationDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("expirationDate", expirationDate);
        return (List<TaskSummary>) getTasksAssignedAsPotentialOwner(userId, groupsIds, status,
                new QueryFilterImpl("t.taskData.expirationTime = :expirationDate", params, "order by t.id DESC"));
        
        

    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<String> groupsIds,
                        List<Status> status, Date expirationDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("expirationDate", expirationDate);
        return (List<TaskSummary>) getTasksAssignedAsPotentialOwner(userId, groupsIds, status,
                new QueryFilterImpl("(t.taskData.expirationTime = :expirationDate or t.taskData.expirationTime is null)", params, "order by t.id DESC"));
        
    }
    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDate(String userId,  List<Status> status, Date expirationDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("expirationDate", expirationDate);
        return (List<TaskSummary>) getTasksOwned(userId, status,
                new QueryFilterImpl( "t.taskData.expirationTime = :expirationDate", params, "order by t.id DESC"));
        
        

    }
   

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> status, Date expirationDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("expirationDate", expirationDate);
        return (List<TaskSummary>) getTasksOwned(userId, status,
                new QueryFilterImpl( "(t.taskData.expirationTime = :expirationDate or t.taskData.expirationTime is null)"
                        , params, "order by t.id DESC"));
        
    }
    
    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateBeforeSpecifiedDate(String userId, List<Status> status, Date date) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksOwnedWithParticularStatusByExpirationDateBeforeSpecifiedDate",
                persistenceContext.addParametersToMap("userId", userId, "status", status, "date", date),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceId(long processInstanceId, List<Status> status) {
        List<TaskSummary> tasks = (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksByStatusByProcessId",
                persistenceContext.addParametersToMap("processInstanceId", processInstanceId, 
                                        "status", status),
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
    
        return tasks;
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceIdByTaskName(long processInstanceId, List<Status> status, String taskName) {
        List<TaskSummary> tasks = (List<TaskSummary>)persistenceContext.queryWithParametersInTransaction("TasksByStatusByProcessIdByTaskName", 
                persistenceContext.addParametersToMap("processInstanceId", processInstanceId,
                                        "status", status, 
                                        "taskName", taskName),
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
    
        return tasks;
    }

    @Override
    public List<Long> getTasksByProcessInstanceId(long processInstanceId) {
        List<Long> tasks = (List<Long>)persistenceContext.queryWithParametersInTransaction("TasksByProcessInstanceId",
                persistenceContext.addParametersToMap("processInstanceId", processInstanceId),
                ClassUtil.<List<Long>>castClass(List.class));
        return tasks;
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<Status> status, Date expirationDate) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerStatusByExpirationDate",
                          persistenceContext.addParametersToMap("userId", userId, "groupIds", "", "status", status, "expirationDate", expirationDate),
                          ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<Status> status, Date expirationDate) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerStatusByExpirationDateOptional",
                    persistenceContext.addParametersToMap("userId", userId, "groupIds", "", "status", status, "expirationDate", expirationDate),
                    ClassUtil.<List<TaskSummary>>castClass(List.class)); 
    }
   
    
    @Override
    public List<TaskSummary> getTasksByVariousFields(List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds,
            List<String> busAdmins, List<String> potOwners, List<String> taskOwners, 
            List<Status> status,  boolean union) {
        Map<String, List<?>> params = new HashMap<String, List<?>>();
        params.put(WORK_ITEM_ID_LIST, workItemIds);
        params.put(TASK_ID_LIST, taskIds);
        params.put(PROCESS_INST_ID_LIST, procInstIds);
        params.put(BUSINESS_ADMIN_ID_LIST, busAdmins);
        params.put(POTENTIAL_OWNER_ID_LIST, potOwners);
        params.put(ACTUAL_OWNER_ID_LIST, taskOwners);
        params.put(STATUS_LIST, status);
        
        return getTasksByVariousFields(params, union);
    }
   
    public static final String MAX_RESULTS = "maxResults";
    
    public List<TaskSummary> getTasksByVariousFields(List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds,
            List<String> busAdmins, List<String> potOwners, List<String> taskOwners, 
            List<Status> status,  boolean union, Integer maxResults) {
        Map<String, List<?>> params = new HashMap<String, List<?>>();
        params.put(WORK_ITEM_ID_LIST, workItemIds);
        params.put(TASK_ID_LIST, taskIds);
        params.put(PROCESS_INST_ID_LIST, procInstIds);
        params.put(BUSINESS_ADMIN_ID_LIST, busAdmins);
        params.put(POTENTIAL_OWNER_ID_LIST, potOwners);
        params.put(ACTUAL_OWNER_ID_LIST, taskOwners);
        params.put(STATUS_LIST, status);
        
        if( maxResults != null ) {
            if( maxResults <= 0 ) { 
                return new ArrayList<TaskSummary>();
            }
            Integer [] maxResultsArr = { maxResults };
            params.put(MAX_RESULTS, Arrays.asList(maxResultsArr));
        }
        
        return getTasksByVariousFields(params, union);
    }
    
    public List<TaskSummary> getTasksByVariousFields( Map<String, List<?>> parameters, boolean union ) { 
        StringBuilder queryBuilder = new StringBuilder(VARIOUS_FIELDS_TASKSUM_QUERY);
        Map<String, Object> params = new HashMap<String, Object>();
        WhereClauseWithListParamAppender<Long> longQueryAdder = new WhereClauseWithListParamAppender<Long>(Long.class, queryBuilder, params, union);
        WhereClauseWithListParamAppender<String> stringQueryAdder = new WhereClauseWithListParamAppender<String>(String.class, queryBuilder, params, union);
        WhereClauseWithListParamAppender<Status> statusQueryAdder = new WhereClauseWithListParamAppender<Status>(Status.class, queryBuilder, params, union);
        
        List<Long> workItemIds = longQueryAdder.checkNullAndInstanceOf(parameters, WORK_ITEM_ID_LIST);
        List<Long> taskIds = longQueryAdder.checkNullAndInstanceOf(parameters, TASK_ID_LIST);
        List<Long> procInstIds = longQueryAdder.checkNullAndInstanceOf(parameters, PROCESS_INST_ID_LIST);
        List<String> busAdmins = stringQueryAdder.checkNullAndInstanceOf(parameters, BUSINESS_ADMIN_ID_LIST);
        List<String> potOwners = stringQueryAdder.checkNullAndInstanceOf(parameters, POTENTIAL_OWNER_ID_LIST);
        List<String> taskOwners = stringQueryAdder.checkNullAndInstanceOf(parameters, ACTUAL_OWNER_ID_LIST);
        List<Status> status = statusQueryAdder.checkNullAndInstanceOf(parameters, STATUS_LIST);
        
        List<?> maxResultsList = parameters.get(MAX_RESULTS);
        if( maxResultsList != null && ! maxResultsList.isEmpty() ) { 
            Object maxResults = maxResultsList.get(0);
            if( maxResults instanceof Integer ) {
                params.put(MAX_RESULTS, maxResults);
            }
        }
        
        if( workItemIds != null && workItemIds.size() > 0 ) { 
            String paramName = "workItemIds";
            longQueryAdder.addToQueryBuilder(
                    "( t.taskData.workItemId in ( :" + paramName + " ) ) ",
                    paramName, 
                    workItemIds);
        }
        if( taskIds != null && taskIds.size() > 0 ) { 
            String paramName = "taskIds";
            longQueryAdder.addToQueryBuilder(
                    "( t.id in ( :" + paramName + " ) ) ",
                    paramName, 
                    taskIds);
        }
        if( procInstIds != null && procInstIds.size() > 0 ) { 
            String paramName = "procInstIds";
            longQueryAdder.addToQueryBuilder(
                    "( t.taskData.processInstanceId in ( :" + paramName + " ) ) ",
                    paramName, 
                    procInstIds);
        }
        
        stringQueryAdder.getQueryState(longQueryAdder);
        if( busAdmins != null && busAdmins.size() > 0 ) { 
            String paramName = "busAdminIds";
            String query = "( businessAdministrator.id in ( :" + paramName + " ) and "
                    + "businessAdministrator in elements ( t.peopleAssignments.businessAdministrators ) ) ";
            stringQueryAdder.addToQueryBuilder(query, paramName, busAdmins);
        }
        if( potOwners != null && potOwners.size() > 0 ) { 
            String paramName = "potOwnerIds";
            String query =  "( potentialOwners.id in ( :" + paramName + " ) and "
                    + "potentialOwners in elements ( t.peopleAssignments.potentialOwners ) ) ";
            stringQueryAdder.addToQueryBuilder(query, paramName, potOwners);
        }
        if( taskOwners != null && taskOwners.size() > 0 ) { 
            String paramName = "taskOwnerIds";
            String query =  "( t.taskData.actualOwner.id in ( :" + paramName + " ) ) ";
            stringQueryAdder.addToQueryBuilder(query, paramName, taskOwners);
        }
        
        
        statusQueryAdder.getQueryState(stringQueryAdder);
        if( status != null && status.size() > 0 ) { 
            String paramName = "statuses";
            String query = "( t.taskData.status in (:"+ paramName + ") ) ";
            statusQueryAdder.addToQueryBuilder(query, paramName, status);
        }
       
        if( ! statusQueryAdder.firstUse ) { 
           queryBuilder.append(")"); 
        }
        
        // order by task id
        queryBuilder.append(" ORDER BY t.id" );
        
        String query = queryBuilder.toString();
        logger.debug("QUERY: {}", query);
        return persistenceContext.queryStringWithParametersInTransaction(query, params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }
    
    public int getCompletedTaskByUserId(String userId) {
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Completed);
        List<TaskSummary> tasksCompleted = getTasksAssignedAsPotentialOwnerByStatus(userId, statuses);
        return tasksCompleted.size();
    }

    public int getPendingTaskByUserId(String userId) {
        List<TaskSummary> tasksAssigned = getTasksAssignedAsPotentialOwner(userId, null, null, null);
        return tasksAssigned.size();
    }
    
    private static String TASKS_ASSIGNED_AS_POTENTIALOWNER_TEMPLATE = "select distinct \n" +
"                new org.jbpm.services.task.query.TaskSummaryImpl(\n" +
"                    t.id,\n" +
"                    t.name,\n" +
"                    t.description,\n" +
"                    t.taskData.status,\n" +
"                    t.priority,\n" +
"                    t.taskData.actualOwner.id,\n" +
"                    t.taskData.createdBy.id,\n" +
"                    t.taskData.createdOn,\n" +
"                    t.taskData.activationTime,\n" +
"                    t.taskData.expirationTime,\n" +
"                    t.taskData.processId,\n" +
"                    t.taskData.processInstanceId,\n" +
"                    t.taskData.parentId,\n" +
"                    t.taskData.deploymentId              )\n" +
"            from\n" +
"                TaskImpl t,\n" +
"                OrganizationalEntityImpl potentialOwners\n" +
"            where\n" +
"                t.archived = 0 and\n" +
"                ( potentialOwners.id = :userId or potentialOwners.id in (:groupIds) ) and\n" +
"                potentialOwners in elements ( t.peopleAssignments.potentialOwners  )  and\n" +
"                t.taskData.status in (:status) and \n";

    
    private static String TASKS_OWNED_TEMPLATE = "select distinct \n" +
"                new org.jbpm.services.task.query.TaskSummaryImpl(\n" +
"                    t.id,\n" +
"                    t.name,\n" +
"                    t.description,\n" +
"                    t.taskData.status,\n" +
"                    t.priority,\n" +
"                    t.taskData.actualOwner.id,\n" +
"                    t.taskData.createdBy.id,\n" +
"                    t.taskData.createdOn,\n" +
"                    t.taskData.activationTime,\n" +
"                    t.taskData.expirationTime,\n" +
"                    t.taskData.processId,\n" +
"                    t.taskData.processInstanceId,\n" +
"                    t.taskData.parentId,\n" +
"                    t.taskData.deploymentId              )\n" +
"            from\n" +
"                TaskImpl t\n" +
"            where\n" +
"                t.archived = 0 and\n" +
"                t.taskData.actualOwner.id = :userId and\n" +
"                t.taskData.status in (:status) and \n";
    
    private static String VARIOUS_FIELDS_TASKSUM_QUERY = 
            "select distinct"
            + "  new org.jbpm.services.task.query.TaskSummaryImpl("
            + "t.id,\n" +
"                t.name,\n" +
"                t.description,\n" +
"                t.taskData.status,\n" +
"                t.priority,\n" +
"                t.taskData.actualOwner.id,\n" +
"                t.taskData.createdBy.id,\n" +
"                t.taskData.createdOn,\n" +
"                t.taskData.activationTime,\n" +
"                t.taskData.expirationTime,\n" +
"                t.taskData.processId,\n" +
"                t.taskData.processInstanceId,\n" +
"                t.taskData.parentId,\n" +
"                t.taskData.deploymentId              )"
            + "from"
            + "  TaskImpl t, "
            + "  OrganizationalEntityImpl businessAdministrator, "
            + "  OrganizationalEntityImpl potentialOwners "
            + "where "
            + "t.archived = 0";

    
    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, 
                                                                        List<Status> status) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("groupIds", groupIds);
        params.put("status", status);
        
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("QuickTasksAssignedAsPotentialOwnerWithGroupsByStatus", 
        		params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    
    private class WhereClauseWithListParamAppender<T> { 

        private final String andOr;
        private boolean firstUse = true;
        private boolean alreadyUsed = false;
        private final StringBuilder queryBuilder;
        private final Map<String, Object> queryParams;
        private final Class<?> clazz;
        
        public WhereClauseWithListParamAppender(Class<?> clazz, StringBuilder queryBuilder, Map<String, Object> params, boolean union) { 
            this.andOr = union ? " OR " : " AND ";
            this.queryBuilder = queryBuilder;
            this.queryParams = params;
            this.clazz = clazz;
        }
        
        public void addToQueryBuilder(String query, String paramName, List<T> paramValList) { 
            if( this.firstUse ) { 
                queryBuilder.append( " AND (");
                this.firstUse = false;
            } else if( this.alreadyUsed ) { 
                queryBuilder.append( andOr );
            } 
            queryBuilder.append( query );
            Set<T> paramVals = new HashSet<T>();
            for( T val : paramValList ) { 
                if( val != null ) { 
                    paramVals.add(val);
                }
            }
            queryParams.put(paramName, paramVals);
            this.alreadyUsed = true;
        }

        public void getQueryState(WhereClauseWithListParamAppender<?> paramAppender) { 
           this.alreadyUsed = paramAppender.alreadyUsed;
           this.firstUse = paramAppender.firstUse;
        }
        
        @SuppressWarnings("unchecked")
        public List<T> checkNullAndInstanceOf(Map<String, List<?>> params, String field) { 
            List<T> result = null;
            List<?> inputList = params.get(field);
            if( inputList != null ) { 
                if( inputList.size() > 0 ) { 
                    Object inputObject = inputList.get(0);
                    if( this.clazz.equals(inputObject.getClass()) ) { 
                        return (List<T>) inputList; 
                    } else { 
                        throw new IllegalArgumentException( field + " parameter is an instance of "
                                + "List<" + inputObject.getClass().getSimpleName() + "> instead of "
                                + "List<"+ this.clazz.getSimpleName() + ">");
                    }
                } 
            }
            return result;
        }
    }
    
}
