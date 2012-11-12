/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.task.admin;

import static org.jbpm.task.service.persistence.TaskPersistenceManager.addParametersToMap;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.persistence.TaskPersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TasksAdminImpl implements TasksAdmin {
    
    protected TaskPersistenceManager tpm;
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceSession.class);

    public TasksAdminImpl(TaskPersistenceManager tpm) {
        this.tpm = tpm;
    }

    public List<TaskSummary> getActiveTasks() {
        HashMap<String, Object> params = addParametersToMap(
                "status", Status.InProgress,
                "language", "en-UK");
        
        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("TasksByStatus", params);
    }

    public List<TaskSummary> getActiveTasks(Date since) {
        HashMap<String, Object> params = addParametersToMap(
                "status", Status.InProgress,
                "language", "en-UK",
                "since", since);
                
        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("TasksByStatus", params);
    }

    public List<TaskSummary> getCompletedTasks() {
        HashMap<String, Object> params = addParametersToMap(
                "status", Status.Completed,
                "language", "en-UK");
        
        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("TasksByStatus", params);
    }

    public List<TaskSummary> getCompletedTasks(Date since) {
        HashMap<String, Object> params = addParametersToMap(
                "status", Status.Completed,
                "language", "en-UK",
                "since", since);
        
        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("TasksByStatusSince", params);
    }
    public List<TaskSummary> getCompletedTasksByProcessId(Long processId) {
        HashMap<String, Object> params = addParametersToMap(
                "status", Arrays.asList(Status.Completed),
                "language", "en-UK",
                "processInstanceId", processId);
        
        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("TasksByStatusByProcessId", params);
    }

    public int archiveTasks(List<TaskSummary> tasks) {
        int archivedTasks = 0;
        boolean txOwner = tpm.beginTransaction();
        for (TaskSummary sum : tasks) {
            long taskId = sum.getId();
            Task task = (Task) tpm.findEntity(Task.class, taskId);
            task.setArchived(true);
            tpm.saveEntity(task);
            archivedTasks++;

        }
        tpm.endTransaction(txOwner);
        return archivedTasks;
    }

    public int removeTasks(List<TaskSummary> tasks) {
        int removedTasks = 0;
        boolean txOwner = tpm.beginTransaction();
        for (TaskSummary sum : tasks) {
            long taskId = sum.getId();
            // Only remove archived tasks
            Task task = (Task) tpm.findEntity(Task.class, taskId);
            if (task.isArchived()) {
                tpm.deleteEntity(task);

                removedTasks++;
            } else {
                logger.error(" The Task cannot be removed if it wasn't archived first !!");
            }
        }
        tpm.endTransaction(txOwner);
        return removedTasks;
    }

    public List<TaskSummary> getArchivedTasks() {
        HashMap<String, Object> params = addParametersToMap(
                "language", "en-UK");
        return (List<TaskSummary>) tpm.queryWithParametersInTransaction("ArchivedTasks", params);
    }

    public void dispose() {
        this.tpm.endPersistenceContext();
    }
    
    
}
