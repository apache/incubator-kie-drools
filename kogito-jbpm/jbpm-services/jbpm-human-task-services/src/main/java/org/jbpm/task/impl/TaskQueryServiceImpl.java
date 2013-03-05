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

package org.jbpm.task.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.api.TaskQueryService;
import org.jbpm.task.query.TaskSummary;

/**
 *
 */
@Named
@Transactional
@ApplicationScoped
public class TaskQueryServiceImpl implements TaskQueryService {

    @Inject
    private JbpmServicesPersistenceManager pm;

    public TaskQueryServiceImpl() {
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksAssignedAsBusinessAdministrator",
                pm.addParametersToMap("userId", userId, "language", language));
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksAssignedAsExcludedOwner", 
                pm.addParametersToMap("userId", userId, "language", language));
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwner", 
                pm.addParametersToMap("userId", userId, "language", language));
                
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerWithGroups", 
                pm.addParametersToMap("userId", userId, "groupIds", groupIds, "language", language));
    }

    public List<TaskSummary> getTasksAssignedByGroup(String groupId, String language) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroup", 
                pm.addParametersToMap("groupId", groupId, "language", language));
    }

    public List<TaskSummary> getTasksAssignedByGroupsByExpirationDateOptional(List<String> groupIds, String language, Date expirationDate) {
        List tasksByGroups = (List<TaskSummary>)pm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroupsByExpirationDateOptional", 
                pm.addParametersToMap("groupIds", groupIds, "expirationDate", expirationDate));
                
        return collectTasksByPotentialOwners(tasksByGroups, language);
    }  
    
    protected List<TaskSummary> collectTasksByPotentialOwners(List tasksByGroups, String language) {
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
            List<TaskSummary> tasks = (List<TaskSummary>)pm.queryWithParametersInTransaction("TaskSummariesByIds", 
                    pm.addParametersToMap("taskIds", tasksIds, "language", language));
                    

            for (TaskSummary ts : tasks) {
                ts.setPotentialOwners(potentialOwners.get(ts.getId()));
            }
            return tasks;
        }
        return new ArrayList<TaskSummary>();
    }
    
    public List<TaskSummary> getTasksAssignedByGroupsByExpirationDate(List<String> groupIds, String language, Date expirationDate) {

        List tasksByGroups = (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroupsByExpirationDate", 
                pm.addParametersToMap("groupIds", groupIds, "expirationDate", expirationDate));
        return collectTasksByPotentialOwners(tasksByGroups, language);
    }        
            
    public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds, String language) {

        List tasksByGroups = (List) pm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByGroups", 
                pm.addParametersToMap("groupIds", groupIds));
                
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
            List<TaskSummary> tasks = (List<TaskSummary>) pm.queryWithParametersInTransaction("TaskSummariesByIds", 
                        pm.addParametersToMap("taskIds", tasksIds, "language", language));

            for (TaskSummary ts : tasks) {
                ts.setPotentialOwners(potentialOwners.get(ts.getId()));
            }
            return tasks;
        }
        return new ArrayList<TaskSummary>();
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResults) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerWithGroups", 
                                    pm.addParametersToMap("userId", userId, "groupIds", groupIds, "language", language, 
                                                    "firstResult", firstResult, "maxResults", maxResults));
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksAssignedAsRecipient", 
                pm.addParametersToMap("userId", userId, "language", language));
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        return (List<TaskSummary>)  pm.queryWithParametersInTransaction("TasksAssignedAsTaskInitiator", 
                pm.addParametersToMap("userId", userId, "language", language));
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksAssignedAsTaskStakeholder", 
                pm.addParametersToMap("userId", userId,"language", language));
    }

    public List<TaskSummary> getTasksOwned(String userId) {
        return (List<TaskSummary>)pm.queryWithParametersInTransaction("TasksOwned", 
                pm.addParametersToMap("userId", userId, "language", "en-UK"));

    }
    
    public List<TaskSummary> getTasksOwnedByExpirationDate(String userId,  List<Status> status, Date expirationDate) {

        return (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksOwnedWithParticularStatusByExpirationDate",
                          pm.addParametersToMap("userId", userId, "status", status, "expirationDate", expirationDate, "language", "en-UK"));

    }
    

    public List<TaskSummary> getTasksOwned(String userId, List<Status> status, String language) {

        List<TaskSummary> taskOwned = (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksOwnedWithParticularStatus", 
                pm.addParametersToMap("userId", userId, "status", status, "language", language));

        if (!taskOwned.isEmpty()) {
            Set<Long> tasksIds = new HashSet<Long>();
            for (TaskSummary ts : taskOwned) {
                tasksIds.add(ts.getId());
            }

            List tasksPotentialOwners = (List)pm.queryWithParametersInTransaction("TasksOwnedPotentialOwnersByTaskIds",
                        pm.addParametersToMap("taskIds", tasksIds));

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
                ts.setPotentialOwners(potentialOwners.get(ts.getId()));
            }
        } else {
            return new ArrayList<TaskSummary>(0);
        }

        return taskOwned;
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByStatus", 
                                        pm.addParametersToMap("userId", userId ,"language", language,"status", status));
                
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksAssignedAsPotentialOwnerByStatusByGroup", 
                                        pm.addParametersToMap("userId", userId, "groupIds", groupIds, "status", status, "language", language));
                
    }

    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("SubTasksAssignedAsPotentialOwner",
                                        pm.addParametersToMap("parentId", parentId, "userId", userId, "language", language));
                
    }

    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("GetSubTasksByParentTaskId", 
                pm.addParametersToMap("parentId", parentId, "language", "en-UK")); //@TODO: FIX THIS!
                
    }

    public int getPendingSubTasksByParent(long parentId) {
        return  ((List<TaskSummary>)pm.queryWithParametersInTransaction("GetSubTasksByParentTaskId", 
                                pm.addParametersToMap("parentId", parentId, "language", "en-UK"))).size();
    }

    public Task getTaskInstanceById(long taskId) {
        Task taskInstance = pm.find(Task.class, taskId);
        return taskInstance;

    }

    public Task getTaskByWorkItemId(long workItemId) {
        List<Task> tasks = (List<Task>)pm.queryWithParametersInTransaction("TaskByWorkItemId", 
                                pm.addParametersToMap("workItemId", workItemId,"maxResults", 1));
        if (tasks.isEmpty())
            return null;
        else 
            return (Task) (tasks.get(0));
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> status, Date expirationDate) {
        return (List<TaskSummary>) pm.queryWithParametersInTransaction("TasksOwnedWithParticularStatusByExpirationDateOptional",
                    pm.addParametersToMap("userId", userId, "status", status, "expirationDate", expirationDate, "language", "en-UK")); //@TODO: FIX LANGUANGE
        
    }
}
