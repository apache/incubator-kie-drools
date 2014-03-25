/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.TaskAdminService;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.InternalTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TaskAdminServiceImpl implements TaskAdminService {

    private static final Logger logger = LoggerFactory.getLogger(TaskAdminServiceImpl.class);
    
    private TaskPersistenceContext persistenceContext;

    public TaskAdminServiceImpl() {
    }

    public TaskAdminServiceImpl(TaskPersistenceContext persistenceContext) {
    	this.persistenceContext = persistenceContext;
    }
    
    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public List<TaskSummary> getActiveTasks() {
        HashMap<String, Object> params = persistenceContext.addParametersToMap(
                "status", Arrays.asList(Status.InProgress),
                "language", "en-UK");

        return persistenceContext.queryWithParametersInTransaction("TasksByStatus", params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getActiveTasks(Date since) {
        HashMap<String, Object> params = persistenceContext.addParametersToMap(
                "status", Arrays.asList(Status.InProgress),
                "language", "en-UK",
                "since", since);

        return persistenceContext.queryWithParametersInTransaction("TasksByStatus", params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getCompletedTasks() {
        HashMap<String, Object> params = persistenceContext.addParametersToMap(
                "status", Arrays.asList(Status.Completed),
                "language", "en-UK");

        return persistenceContext.queryWithParametersInTransaction("TasksByStatus", params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getCompletedTasks(Date since) {
        HashMap<String, Object> params = persistenceContext.addParametersToMap(
                "status", Arrays.asList(Status.Completed),
                "language", "en-UK",
                "since", since);

        return persistenceContext.queryWithParametersInTransaction("TasksByStatusSince", params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getCompletedTasksByProcessId(Long processId) {
        HashMap<String, Object> params = persistenceContext.addParametersToMap(
                "status", Arrays.asList(Status.Completed),
                "language", "en-UK",
                "processInstanceId", processId);

        return persistenceContext.queryWithParametersInTransaction("TasksByStatusByProcessId", params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public int archiveTasks(List<TaskSummary> tasks) {
        int archivedTasks = 0;
        for (TaskSummary sum : tasks) {
            long taskId = sum.getId();
            Task task = persistenceContext.findTask(taskId);
            ((InternalTask) task).setArchived(true);
            persistenceContext.persist(task);
            archivedTasks++;

        }
        return archivedTasks;
    }

    public List<TaskSummary> getArchivedTasks() {
        HashMap<String, Object> params = persistenceContext.addParametersToMap(
                "language", "en-UK");
        return persistenceContext.queryWithParametersInTransaction("ArchivedTasks", params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public int removeTasks(List<TaskSummary> tasks) {
        int removedTasks = 0;

        for (TaskSummary sum : tasks) {
            long taskId = sum.getId();
            // Only remove archived tasks
            Task task = persistenceContext.findTask(taskId);
            Content content = persistenceContext.findContent(task.getTaskData().getDocumentContentId());
            if (isArchived(((InternalTask) task))) {
                persistenceContext.remove(task);
                if (content != null) {
                    persistenceContext.remove(content);
                }
                removedTasks++;
            } else {
                logger.warn(" The Task cannot be removed if it wasn't archived first !!");
            }
        }

        return removedTasks;
    }

    private static boolean isArchived(InternalTask task) { 
        Short archived = task.getArchived();
        if (archived == null) {
            return false;
        }
        return (archived == 1) ? Boolean.TRUE : Boolean.FALSE; 
    }
    
    public int removeAllTasks() {
        List<Task> tasks = persistenceContext.queryStringInTransaction("select t from TaskImpl t",
                ClassUtil.<List<Task>>castClass(List.class));
        int count = 0;
        for (Task t : tasks) {
            persistenceContext.removeTask(t);
            count++;
        }
        return count;
    }
}
