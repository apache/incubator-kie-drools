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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

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
        
        return tpm.createQuery("TasksByStatus").setParameter("status", Status.InProgress).setParameter("language", "en-UK").getResultList();
    }

    public List<TaskSummary> getActiveTasks(Date since) {
        
        return tpm.createQuery("TasksByStatus").setParameter("status", Status.InProgress).setParameter("language", "en-UK").setParameter("since", since).getResultList();
    }

    public List<TaskSummary> getCompletedTasks() {
        return tpm.createQuery("TasksByStatus").setParameter("status", Status.Completed).setParameter("language", "en-UK").getResultList();
    }

    public List<TaskSummary> getCompletedTasks(Date since) {
        return tpm.createQuery("TasksByStatusSince").setParameter("status", Status.Completed).setParameter("language", "en-UK").setParameter("since", since).getResultList();
    }
    public List<TaskSummary> getCompletedTasksByProcessId(Long processId) {
        
        return tpm.createQuery("TasksByStatusByProcessId")
                .setParameter("status", Status.Completed)
                .setParameter("language", "en-UK")
                .setParameter("processId", processId).getResultList();
    }

    public int archiveTasks(List<TaskSummary> tasks) {
        int archivedTasks = 0;
        tpm.beginTransaction();
        for (TaskSummary sum : tasks) {
            long taskId = sum.getId();
            Task task = (Task) tpm.findEntity(Task.class, taskId);
            task.setArchived(true);
            tpm.saveEntity(task);
            archivedTasks++;

        }
        tpm.endTransaction(true);
        return archivedTasks;
    }

    public int removeTasks(List<TaskSummary> tasks) {
        int removedTasks = 0;
        tpm.beginTransaction();
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
        tpm.endTransaction(true);
        return removedTasks;
    }

    public List<TaskSummary> getArchivedTasks() {
        return tpm.createQuery("ArchivedTasks").setParameter("language", "en-UK").getResultList();
    }

    public void dispose() {
        this.tpm.endPersistenceContext();
    }
    
    
}
