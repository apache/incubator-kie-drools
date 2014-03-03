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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.services.task.utils.ClassUtil;
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

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsBusinessAdministrator",
        		persistenceContext.addParametersToMap("userId", userId, "language", language),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsExcludedOwner", 
        		persistenceContext.addParametersToMap("userId", userId, "language", language),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwner", 
        		persistenceContext.addParametersToMap("userId", userId, "language", language),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
                
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerWithGroups", 
                persistenceContext.addParametersToMap("userId", userId, "groupIds", groupIds, "language", language),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedByGroup(String groupId, String language) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroup", 
                persistenceContext.addParametersToMap("groupId", groupId, "language", language),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedByGroupsByExpirationDateOptional(List<String> groupIds, String language, Date expirationDate) {
        List<Object[]> tasksByGroups = (List<Object[]>)persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroupsByExpirationDateOptional", 
                persistenceContext.addParametersToMap("groupIds", groupIds, "expirationDate", expirationDate),
                ClassUtil.<List<Object[]>>castClass(List.class));
                
        return collectTasksByPotentialOwners(tasksByGroups, language);
    }  
    
    protected List<TaskSummary> collectTasksByPotentialOwners(List<Object[]> tasksByGroups, String language) {
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
                    persistenceContext.addParametersToMap("taskIds", tasksIds, "language", language),
                    ClassUtil.<List<TaskSummary>>castClass(List.class));
                    

            for (TaskSummary ts : tasks) {
                ((InternalTaskSummary) ts).setPotentialOwners(potentialOwners.get(ts.getId()));
            }
            return tasks;
        }
        return new ArrayList<TaskSummary>();
    }
    
    public List<TaskSummary> getTasksAssignedByGroupsByExpirationDate(List<String> groupIds, String language, Date expirationDate) {

        List<Object[]> tasksByGroups = (List<Object[]>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroupsByExpirationDate", 
                persistenceContext.addParametersToMap("groupIds", groupIds, "expirationDate", expirationDate),
                ClassUtil.<List<Object[]>>castClass(List.class));
        return collectTasksByPotentialOwners(tasksByGroups, language);
    }        
            
    public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds, String language) {

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
                        persistenceContext.addParametersToMap("taskIds", tasksIds, "language", language),
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
    
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResults) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerWithGroups", 
                                    persistenceContext.addParametersToMap("userId", userId, "groupIds", groupIds, "language", language, 
                                                    "firstResult", firstResult, "maxResults", maxResults),
                                                    ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsRecipient", 
                persistenceContext.addParametersToMap("userId", userId, "language", language),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        return (List<TaskSummary>)  persistenceContext.queryWithParametersInTransaction("TasksAssignedAsTaskInitiator", 
                persistenceContext.addParametersToMap("userId", userId, "language", language),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsTaskStakeholder", 
                persistenceContext.addParametersToMap("userId", userId,"language", language),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getTasksOwned(String userId, String language) {
        return (List<TaskSummary>)persistenceContext.queryWithParametersInTransaction("TasksOwned", 
                persistenceContext.addParametersToMap("userId", userId, "language", language),
                ClassUtil.<List<TaskSummary>>castClass(List.class));

    }
    
   
    

    public List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status, String language) {

        List<TaskSummary> taskOwned = (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksOwnedWithParticularStatus", 
                persistenceContext.addParametersToMap("userId", userId, "status", status, "language", language),
                ClassUtil.<List<TaskSummary>>castClass(List.class));

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

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByStatus", 
                                        persistenceContext.addParametersToMap("userId", userId ,"language", language,"status", status),
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
                
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByStatusByGroup", 
                                        persistenceContext.addParametersToMap("userId", userId, "groupIds", groupIds, "status", status, "language", language),
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
                
    }

    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("SubTasksAssignedAsPotentialOwner",
                                        persistenceContext.addParametersToMap("parentId", parentId, "userId", userId, "language", language),
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
                
    }

    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("GetSubTasksByParentTaskId", 
                persistenceContext.addParametersToMap("parentId", parentId, "language", "en-UK"),
                ClassUtil.<List<TaskSummary>>castClass(List.class)); //@TODO: FIX THIS!
                
    }

    public int getPendingSubTasksByParent(long parentId) {
        return  persistenceContext.queryWithParametersInTransaction("GetSubTasksByParentTaskId", 
                                persistenceContext.addParametersToMap("parentId", parentId, "language", "en-UK"),
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
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerStatusByExpirationDate",
                          persistenceContext.addParametersToMap("userId", userId, "groupIds", groupsIds, "status", status, "expirationDate", expirationDate, "language", "en-UK"),
                          ClassUtil.<List<TaskSummary>>castClass(List.class));

    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<String> groupsIds,
                        List<Status> status, Date expirationDate) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerStatusByExpirationDateOptional",
                    persistenceContext.addParametersToMap("userId", userId, "groupIds", groupsIds, "status", status, "expirationDate", expirationDate, "language", "en-UK"),
                    ClassUtil.<List<TaskSummary>>castClass(List.class)); //@TODO: FIX LANGUANGE
        
    }
    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDate(String userId,  List<Status> status, Date expirationDate) {
        return persistenceContext.queryWithParametersInTransaction("TasksOwnedWithParticularStatusByExpirationDate",
                          persistenceContext.addParametersToMap("userId", userId, "status", status, "expirationDate", expirationDate, "language", "en-UK"),
                          ClassUtil.<List<TaskSummary>>castClass(List.class));

    }
   

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> status, Date expirationDate) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksOwnedWithParticularStatusByExpirationDateOptional",
                    persistenceContext.addParametersToMap("userId", userId, "status", status, "expirationDate", expirationDate, "language", "en-UK"),
                    ClassUtil.<List<TaskSummary>>castClass(List.class)); //@TODO: FIX LANGUANGE
        
    }
    
    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateBeforeSpecifiedDate(String userId, List<Status> status, Date date) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksOwnedWithParticularStatusByExpirationDateBeforeSpecifiedDate",
                persistenceContext.addParametersToMap("userId", userId, "status", status, "date", date, "language", "en-UK"),
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceId(long processInstanceId, List<Status> status, String language) {
        List<TaskSummary> tasks = (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksByStatusByProcessId",
                persistenceContext.addParametersToMap("processInstanceId", processInstanceId, 
                                        "status", status,
                                        "language", language),
                                        ClassUtil.<List<TaskSummary>>castClass(List.class));
    
        return tasks;
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceIdByTaskName(long processInstanceId, List<Status> status, String taskName,
            String language) {
        List<TaskSummary> tasks = (List<TaskSummary>)persistenceContext.queryWithParametersInTransaction("TasksByStatusByProcessIdByTaskName", 
                persistenceContext.addParametersToMap("processInstanceId", processInstanceId,
                                        "status", status, 
                                        "taskName", taskName,
                                        "language", language),
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
                          persistenceContext.addParametersToMap("userId", userId, "groupIds", "", "status", status, "expirationDate", expirationDate, "language", "en-UK"),
                          ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<Status> status, Date expirationDate) {
        return (List<TaskSummary>) persistenceContext.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerStatusByExpirationDateOptional",
                    persistenceContext.addParametersToMap("userId", userId, "groupIds", "", "status", status, "expirationDate", expirationDate, "language", "en-UK"),
                    ClassUtil.<List<TaskSummary>>castClass(List.class)); //@TODO: FIX LANGUANGE
    }
    
    @Override
    public List<TaskSummary> getTasksByVariousFields(List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds,
            List<String> busAdmins, List<String> potOwners, List<String> taskOwners, 
            List<Status> status, List<String> language, boolean union) {
        Map<String, List<?>> params = new HashMap<String, List<?>>();
        params.put(WORK_ITEM_ID_LIST, workItemIds);
        params.put(TASK_ID_LIST, taskIds);
        params.put(PROCESS_INST_ID_LIST, procInstIds);
        params.put(BUSINESS_ADMIN_ID_LIST, busAdmins);
        params.put(POTENTIAL_OWNER_ID_LIST, potOwners);
        params.put(ACTUAL_OWNER_ID_LIST, taskOwners);
        params.put(STATUS_LIST, status);
        params.put(LANGUAGE, language);
        
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
        List<String> language = stringQueryAdder.checkNullAndInstanceOf(parameters, LANGUAGE);
        List<Status> status = statusQueryAdder.checkNullAndInstanceOf(parameters, STATUS_LIST);
        
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
        if( language != null && language.size() > 0 ) { 
            String paramName = "language";
            String query = "( name.language in ( :" + paramName + " ) or t.names.size = 0 ) and"
                    + "( subject.language in ( :" + paramName + " ) or t.subjects.size = 0 ) and"
                    + "( description.language in ( :" + paramName + " ) or t.descriptions.size = 0 )";
            stringQueryAdder.addToQueryBuilder(query, paramName, language);
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
        String query = queryBuilder.toString();
        logger.debug("QUERY: " + query);
        return persistenceContext.queryStringWithParametersInTransaction(query, params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }
    
    public int getCompletedTaskByUserId(String userId) {
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Completed);
        List<TaskSummary> tasksCompleted = getTasksAssignedAsPotentialOwnerByStatus(userId, statuses, "en-UK");
        return tasksCompleted.size();
    }

    public int getPendingTaskByUserId(String userId) {
        List<TaskSummary> tasksAssigned = getTasksAssignedAsPotentialOwner(userId, "en-UK");
        return tasksAssigned.size();
    }
    
    private static String VARIOUS_FIELDS_TASKSUM_QUERY = 
            "select distinct"
            + "  new org.jbpm.services.task.query.TaskSummaryImpl(t.id,"
            + "  t.taskData.processInstanceId,"
            + "  name.shortText," + "subject.shortText," + "description.shortText,"
            + "  t.taskData.status,"
            + "  t.priority,"
            + "  t.taskData.skipable,"
            + "  actualOwner," + "createdBy,"
            + "  t.taskData.createdOn," + "t.taskData.activationTime," + "t.taskData.expirationTime,"
            + "  t.taskData.processId," + "t.taskData.processSessionId,"
            + "  t.subTaskStrategy,"
            + "  t.taskData.parentId ) "
            + "from"
            + "  TaskImpl t "
            + "  left join t.taskData.actualOwner as actualOwner            "
            + "  left join t.taskData.createdBy as createdBy"
            + "  left join t.subjects as subject"
            + "  left join t.descriptions as description"
            + "  left join t.names as name, "
            + "  OrganizationalEntityImpl businessAdministrator, "
            + "  OrganizationalEntityImpl potentialOwners "
            + "where "
            + "t.archived = 0";
    
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
