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
package org.jbpm.task.admin.listener;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.event.DefaultProcessEventListener;
import org.jbpm.task.Status;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.query.TaskSummary;

import org.kie.api.event.process.ProcessCompletedEvent;

/**
 *
 * @author salaboy
 */
public class TaskCleanUpProcessEventListener extends DefaultProcessEventListener {

    private TaskServiceEntryPoint taskService;
    
    public TaskCleanUpProcessEventListener(TaskServiceEntryPoint taskService) {
        this.taskService = taskService;
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
        List<TaskSummary> completedTasksByProcessId = taskService.getTasksByStatusByProcessId(event.getProcessInstance().getId(), statuses, "en-UK");
        taskService.archiveTasks(completedTasksByProcessId);
        taskService.removeTasks(completedTasksByProcessId);
        
        
    }
}
