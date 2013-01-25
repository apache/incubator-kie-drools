/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.impl;

import java.util.ArrayList;
import java.util.Collections;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        return em.createNamedQuery("TasksAssignedAsPotentialOwner").setParameter("userId", userId)
                .setParameter("language", language)
                .getResultList();
    }
    //@TODO: There is no test for this method! 

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

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        return em.createNamedQuery("TasksAssignedAsRecipient").setParameter("userId", userId)
                .setParameter("language", language)
                .getResultList();
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskSummary> getTasksOwned(String userId) {

        List<TaskSummary> taskOwned = em.createNamedQuery("TasksOwned")
                .setParameter("userId", userId)
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        return (Task) em.createNamedQuery("TaskByWorkItemId").setParameter("workItemId", workItemId).setMaxResults(1).getResultList().get(0);

    }
}
