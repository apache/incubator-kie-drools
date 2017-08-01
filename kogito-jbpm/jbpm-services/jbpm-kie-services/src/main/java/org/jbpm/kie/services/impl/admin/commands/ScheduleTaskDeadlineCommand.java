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

import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskDeadlinesService.DeadlineType;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.InternalTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class ScheduleTaskDeadlineCommand extends UserGroupCallbackTaskCommand<Long> {

    private static final long serialVersionUID = -1856489382099976731L;
    private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskDeadlineCommand.class);
    
    private DeadlineType type;
    private Deadline deadline;
    private String timeExpression;

    public ScheduleTaskDeadlineCommand(String userId, long taskId, DeadlineType type, Deadline deadline, String timeExpression) {
        super();
        setUserId(userId);
        setTaskId(taskId);
        this.type = type;
        this.deadline = deadline;
        this.timeExpression = timeExpression;
    }

    @Override
    public Long execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);  
        TaskPersistenceContext persistenceContext = context.getPersistenceContext();
        
        if (!isBusinessAdmin(userId, task.getPeopleAssignments().getBusinessAdministrators(), context)) {
            throw new PermissionDeniedException("User " + userId + " is not business admin of task " + taskId);
        }
        logger.debug("About to schedule {} on a task {}", deadline, task);
        Deadlines deadlines = ((InternalTask)task).getDeadlines();              
        
        if (type.equals(DeadlineType.START)) {
            deadlines.getStartDeadlines().add(deadline);
        } else {
            deadlines.getEndDeadlines().add(deadline);
        }
        doCallbackOperationForTaskDeadlines(deadlines, context);
        
        persistenceContext.persistDeadline(deadline);        
        persistenceContext.updateTask(task);
        logger.debug("Task updated and deadline stored with id {}", deadline.getId());
        
        TaskDeadlinesService deadlinesService = context.getTaskDeadlinesService();
        long fireAfterDuration = DateTimeUtils.parseDuration(timeExpression);        
        deadline.setDate(new Date(System.currentTimeMillis() + fireAfterDuration));
        
        logger.debug("Deadline expiration time set to {} and duration {}", deadline.getDate(), fireAfterDuration);
        deadlinesService.schedule(taskId, deadline.getId(), fireAfterDuration, type);
        logger.debug("Deadline on task {} successfully scheduled to fire at {}", task, deadline.getDate());
        
        return deadline.getId();
    }

}
