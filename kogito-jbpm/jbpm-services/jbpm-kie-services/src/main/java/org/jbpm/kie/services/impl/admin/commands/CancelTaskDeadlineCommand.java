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

package org.jbpm.kie.services.impl.admin.commands;

import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.task.model.Task;
import org.kie.api.runtime.Context;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.TaskDeadlinesService.DeadlineType;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.InternalTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CancelTaskDeadlineCommand extends UserGroupCallbackTaskCommand<Void> {

    private static final long serialVersionUID = -1856489382099976731L;
    private static final Logger logger = LoggerFactory.getLogger(CancelTaskDeadlineCommand.class);
    
    private Long deadlineId;

    public CancelTaskDeadlineCommand(String userId, long taskId, Long deadlineId) {
        super();
        setUserId(userId);
        setTaskId(taskId);
        this.deadlineId = deadlineId;
    }

    @Override
    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);  
        
        if (!isBusinessAdmin(userId, task.getPeopleAssignments().getBusinessAdministrators(), context)) {
            throw new PermissionDeniedException("User " + userId + " is not business admin of task " + taskId);
        }
        logger.debug("About to cancel deadline {} on a task {}", deadlineId, task);
        Deadlines deadlines = ((InternalTask)task).getDeadlines();              
        DeadlineType type = DeadlineType.START;
        
        Deadline deadline = deadlines.getStartDeadlines().stream().filter(d -> deadlineId.equals(d.getId())).findFirst().orElse(null);
        if (deadline == null) {
            deadline = deadlines.getEndDeadlines().stream().filter(d -> deadlineId.equals(d.getId())).findFirst().orElse(null);
            type = DeadlineType.END;
        }
        
        TaskPersistenceContext persistenceContext = context.getPersistenceContext();
        TaskDeadlinesService deadlinesService = context.getTaskDeadlinesService();
        
        deadlinesService.unschedule(taskId, deadline, type);
        persistenceContext.removeDeadline(deadline);
        
        return null;
    }

}
