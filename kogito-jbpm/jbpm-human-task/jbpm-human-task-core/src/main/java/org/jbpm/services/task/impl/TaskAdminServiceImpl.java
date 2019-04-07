/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
                "status", Arrays.asList(Status.InProgress));

        return persistenceContext.queryWithParametersInTransaction("TasksByStatus", params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getActiveTasks(Date since) {
        HashMap<String, Object> params = persistenceContext.addParametersToMap(
                "status", Arrays.asList(Status.InProgress),
                "since", since);

        return persistenceContext.queryWithParametersInTransaction("TasksByStatusSince", params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getCompletedTasks() {
        HashMap<String, Object> params = persistenceContext.addParametersToMap(
                "status", Arrays.asList(Status.Completed));

        return persistenceContext.queryWithParametersInTransaction("TasksByStatus", params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getCompletedTasks(Date since) {
        HashMap<String, Object> params = persistenceContext.addParametersToMap(
                "status", Arrays.asList(Status.Completed),
                "since", since);

        return persistenceContext.queryWithParametersInTransaction("TasksByStatusSince", params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public List<TaskSummary> getCompletedTasksByProcessId(Long processId) {
        HashMap<String, Object> params = persistenceContext.addParametersToMap(
                "status", Arrays.asList(Status.Completed),
                "processInstanceId", processId);

        return persistenceContext.queryWithParametersInTransaction("TasksByStatusByProcessId", params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public int archiveTasks(List<TaskSummary> tasks) {
        int archivedTasks = 0;
        for (TaskSummary sum : tasks) {
            long taskId = sum.getId();
            Task task = persistenceContext.findTask(taskId);
            if (task != null) {
	            ((InternalTask) task).setArchived(true);
	            persistenceContext.updateTask(task);
	            archivedTasks++;
            }
        }
        return archivedTasks;
    }

    public List<TaskSummary> getArchivedTasks() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        return persistenceContext.queryWithParametersInTransaction("ArchivedTasks", params,
                ClassUtil.<List<TaskSummary>>castClass(List.class));
    }

    public int removeTasks(List<TaskSummary> tasks) {
        int removedTasks = 0;

        for (TaskSummary sum : tasks) {
            long taskId = sum.getId();
            // Only remove archived tasks
            Task task = persistenceContext.findTask(taskId);
        	if (task != null) {
	            Content content = persistenceContext.findContent(task.getTaskData().getDocumentContentId());
	            Content outputContent = persistenceContext.findContent(task.getTaskData().getOutputContentId());
	            if (((InternalTask) task).isArchived()) {
	                persistenceContext.remove(task);
	                if (content != null) {
	                    persistenceContext.remove(content);
	                }
	                if (outputContent != null) {
	                	persistenceContext.remove(outputContent);
	                }
	                removedTasks++;
	            } else {
	                logger.warn(" The Task cannot be removed if it wasn't archived first !!");
	            }
            } 
        }

        return removedTasks;
    }

    public int removeAllTasks() {
        List<Task> tasks = persistenceContext.queryInTransaction("GetAllTasks", 
                ClassUtil.<List<Task>>castClass(List.class));
        int count = 0;
        for (Task t : tasks) {
            persistenceContext.removeTask(t);
            count++;
        }
        return count;
    }
}
