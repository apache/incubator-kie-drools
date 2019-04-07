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
package org.jbpm.services.task.admin.listener;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.event.DefaultProcessEventListener;
import org.jbpm.services.task.commands.GetTasksForProcessCommand;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.InternalTaskService;


public class TaskCleanUpProcessEventListener extends DefaultProcessEventListener {

    private InternalTaskService taskService;
    
	public TaskCleanUpProcessEventListener(TaskService taskService) {
        this.taskService = (InternalTaskService) taskService;
    }
 
    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {        
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Error);
        statuses.add(Status.Failed);
        statuses.add(Status.Obsolete);
        statuses.add(Status.Suspended);
        statuses.add(Status.Completed);
        statuses.add(Status.Exited);
        List<TaskSummary> completedTasksByProcessId = ((InternalTaskService)taskService).execute(new GetTasksForProcessCommand(event.getProcessInstance().getId(), statuses, "en-UK"));
        // archive and remove
        taskService.archiveTasks(completedTasksByProcessId);
        taskService.removeTasks(completedTasksByProcessId);
    }

	public void setTaskService(InternalTaskService taskService) {
		this.taskService = taskService;
	}
    
}
