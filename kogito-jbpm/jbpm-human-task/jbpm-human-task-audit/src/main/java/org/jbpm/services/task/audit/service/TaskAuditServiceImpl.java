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

package org.jbpm.services.task.audit.service;

import java.util.List;

import org.jbpm.services.task.audit.commands.GetAllAdminAuditTasksByUserCommand;
import org.jbpm.services.task.audit.commands.GetAllAuditTasksByStatusCommand;
import org.jbpm.services.task.audit.commands.GetAllGroupAuditTasksByUserCommand;
import org.jbpm.services.task.audit.commands.GetAllHistoryAuditTasksByUserCommand;
import org.jbpm.services.task.audit.commands.GetAllHistoryAuditTasksCommand;
import org.jbpm.services.task.audit.commands.GetAuditEventsByProcessInstanceIdCommand;
import org.jbpm.services.task.audit.commands.GetAuditEventsCommand;
import org.kie.api.task.TaskService;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.internal.task.query.AuditTaskQueryBuilder;
import org.kie.internal.task.query.TaskEventQueryBuilder;
import org.kie.internal.task.query.TaskVariableQueryBuilder;

/**
 *
 */
  public class TaskAuditServiceImpl implements TaskAuditService {

    private InternalTaskService taskService;

    @Override
    public List<TaskEvent> getAllTaskEvents(long taskId, QueryFilter filter) {
        return taskService.execute(new GetAuditEventsCommand(taskId, filter));
    }


    @Override
    public List<TaskEvent> getAllTaskEventsByProcessInstanceId(long processInstanceId, QueryFilter filter) {
        return taskService.execute(new GetAuditEventsByProcessInstanceIdCommand(processInstanceId, filter));
    }


    @Override
    public List<AuditTask> getAllAuditTasks( QueryFilter filter) {
        return taskService.execute(new GetAllHistoryAuditTasksCommand(filter));
    }

    @Override
    public List<AuditTask> getAllAuditTasksByUser(String userId, QueryFilter filter) {
        return taskService.execute(new GetAllHistoryAuditTasksByUserCommand(userId, filter));
    }

    @Override
    public void setTaskService(TaskService taskService) {
        this.taskService = (InternalTaskService) taskService;
    }

    @Override
    public List<AuditTask> getAllGroupAuditTasksByUser(String userId, QueryFilter filter) {
        return taskService.execute(new GetAllGroupAuditTasksByUserCommand(userId, filter));
    }

    @Override
    public List<AuditTask> getAllAdminAuditTasksByUser(String userId, QueryFilter filter) {
        return taskService.execute(new GetAllAdminAuditTasksByUserCommand(userId, filter));
    }

    @Override
    public List<AuditTask> getAllAuditTasksByStatus(String userId, QueryFilter filter) {
        return taskService.execute(new GetAllAuditTasksByStatusCommand(userId, filter));
    }

    public TaskVariableQueryBuilder taskVariableQuery()  {
        return new TaskVariableQueryBuilderImpl(taskService);
    }

    @Override
    public TaskEventQueryBuilder taskEventQuery() {
        return new TaskEventQueryBuilderImpl(taskService);
    }

    @Override
    public AuditTaskQueryBuilder auditTaskQuery() {
        return new AuditTaskQueryBuilderImpl(taskService);
    }

}
