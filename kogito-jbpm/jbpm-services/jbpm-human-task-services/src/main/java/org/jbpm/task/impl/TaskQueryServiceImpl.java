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
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;
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
    private EntityManager em;

    public TaskQueryServiceImpl() {
    }

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        return em.createNamedQuery("TasksAssignedAsBusinessAdministrator").setParameter("userId", userId)
                .setParameter("language", language)
                .getResultList();
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        return em.createNamedQuery("TasksAssignedAsExcludedOwner").setParameter("userId", userId)
                .setParameter("language", language)
                .getResultList();
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        return em.createNamedQuery("TasksAssignedAsPotentialOwner").setParameter("userId", userId)
                .setParameter("language", language)
                .getResultList();
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language) {
        return em.createNamedQuery("TasksAssignedAsPotentialOwnerWithGroups").setParameter("userId", userId)
                .setParameter("groupIds", groupIds)
                .setParameter("language", language)
                .getResultList();
    }

    public List<TaskSummary> getTasksAssignedByGroup(String groupId, String language) {
        return em.createNamedQuery("TasksAssignedAsPotentialOwnerByGroup")
                .setParameter("groupId", groupId)
                .setParameter("language", language)
                .getResultList();
    }

    
    public List<TaskSummary> getTasksAssignedByGroupsByExpirationDate(List<String> groupIds, String language, Date expirationDate) {

        List tasksByGroups = em.createNamedQuery("TasksAssignedAsPotentialOwnerByGroupsByExpirationDate")
                .setParameter("groupIds", groupIds)
                .setParameter("expirationDate", expirationDate)
                .getResultList();
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
            List<TaskSummary> tasks = em.createNamedQuery("TaskSummariesByIds")
                    .setParameter("taskIds", tasksIds)
                    .setParameter("language", language)
                    .getResultList();

            for (TaskSummary ts : tasks) {
                ts.setPotentialOwners(potentialOwners.get(ts.getId()));
            }
            return tasks;
        }
        return new ArrayList<TaskSummary>();
    }        
            
    public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds, String language) {

        List tasksByGroups = em.createNamedQuery("TasksAssignedAsPotentialOwnerByGroups")
                .setParameter("groupIds", groupIds)
                .getResultList();
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
            List<TaskSummary> tasks = em.createNamedQuery("TaskSummariesByIds")
                    .setParameter("taskIds", tasksIds)
                    .setParameter("language", language)
                    .getResultList();

            for (TaskSummary ts : tasks) {
                ts.setPotentialOwners(potentialOwners.get(ts.getId()));
            }
            return tasks;
        }
        return new ArrayList<TaskSummary>();
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResults) {
        return em.createNamedQuery("TasksAssignedAsPotentialOwnerWithGroups").setParameter("userId", userId)
                .setParameter("groupIds", groupIds)
                .setParameter("language", language)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        return em.createNamedQuery("TasksAssignedAsRecipient").setParameter("userId", userId)
                .setParameter("language", language)
                .getResultList();
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        return em.createNamedQuery("TasksAssignedAsTaskInitiator").setParameter("userId", userId)
                .setParameter("language", language)
                .getResultList();
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        return em.createNamedQuery("TasksAssignedAsTaskStakeholder").setParameter("userId", userId)
                .setParameter("language", language)
                .getResultList();
    }

    public List<TaskSummary> getTasksOwned(String userId) {

        List<TaskSummary> taskOwned = em.createNamedQuery("TasksOwned")
                .setParameter("userId", userId)
                .setParameter("language", "en-UK").getResultList();

        return taskOwned;

    }
    
    public List<TaskSummary> getTasksOwnedByExpirationDate(String userId,  List<Status> status, Date expirationDate) {

        List<TaskSummary> taskOwned = em.createNamedQuery("TasksOwnedWithParticularStatusByExpirationDate")
                .setParameter("userId", userId)
                .setParameter("status", status)
                .setParameter("expirationDate", expirationDate)
                .setParameter("language", "en-UK").getResultList();

        return taskOwned;

    }
    

    public List<TaskSummary> getTasksOwned(String userId, List<Status> status, String language) {

        List<TaskSummary> taskOwned = em.createNamedQuery("TasksOwnedWithParticularStatus")
                .setParameter("userId", userId)
                .setParameter("status", status)
                .setParameter("language", language).getResultList();

        if (!taskOwned.isEmpty()) {
            Set<Long> tasksIds = new HashSet<Long>();
            for (TaskSummary ts : taskOwned) {
                tasksIds.add(ts.getId());
            }

            List tasksPotentialOwners = em.createNamedQuery("TasksOwnedPotentialOwnersByTaskIds")
                    .setParameter("taskIds", tasksIds)
                    .getResultList();

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
        return em.createNamedQuery("TasksAssignedAsPotentialOwnerByStatus").setParameter("userId", userId)
                .setParameter("language", language)
                .setParameter("status", status)
                .getResultList();
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language) {
        return em.createNamedQuery("TasksAssignedAsPotentialOwnerByStatusByGroup").setParameter("userId", userId)
                .setParameter("groupIds", groupIds)
                .setParameter("status", status)
                .setParameter("language", language)
                .getResultList();
    }

    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        return em.createNamedQuery("SubTasksAssignedAsPotentialOwner").setParameter("parentId", parentId)
                .setParameter("userId", userId)
                .setParameter("language", language)
                .getResultList();
    }

    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return em.createNamedQuery("GetSubTasksByParentTaskId").setParameter("parentId", parentId)
                .setParameter("language", "en-UK") //@TODO: FIX THIS!
                .getResultList();
    }

    public int getPendingSubTasksByParent(long parentId) {
        return em.createNamedQuery("GetSubTasksByParentTaskId").setParameter("parentId", parentId)
                .setParameter("language", "en-UK")
                .getResultList().size();
    }

    public Task getTaskInstanceById(long taskId) {
        Task taskInstance = em.find(Task.class, taskId);
        return taskInstance;

    }

    public Task getTaskByWorkItemId(long workItemId) {
        List<Task> tasks = em.createNamedQuery("TaskByWorkItemId").setParameter("workItemId", workItemId).setMaxResults(1).getResultList();
        if (tasks.isEmpty())
            return null;
        else 
            return (Task) (tasks.get(0));
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> status, Date expirationDate) {
        List<TaskSummary> taskOwned = em.createNamedQuery("TasksOwnedWithParticularStatusByExpirationDateOptional")
                    .setParameter("userId", userId)
                    .setParameter("status", status)
                    .setParameter("expirationDate", expirationDate)
                    .setParameter("language", "en-UK").getResultList();
        
        return taskOwned;
    }
}
