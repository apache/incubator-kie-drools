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

import org.kie.api.task.TaskService;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.internal.task.query.AuditTaskQueryBuilder;
import org.kie.internal.task.query.TaskEventQueryBuilder;
import org.kie.internal.task.query.TaskVariableQueryBuilder;


public interface TaskAuditService {

    void setTaskService(TaskService taskService);

    List<TaskEvent> getAllTaskEvents(long taskId, QueryFilter filter);

    List<TaskEvent> getAllTaskEventsByProcessInstanceId(long processInstanceId, QueryFilter filter);

    List<AuditTask> getAllAuditTasks( QueryFilter filter);

    List<AuditTask> getAllAuditTasksByUser(String userId, QueryFilter filter);

    List<AuditTask> getAllGroupAuditTasksByUser(String userId, QueryFilter filter);

    List<AuditTask> getAllAdminAuditTasksByUser(String userId, QueryFilter filter);

    List<AuditTask> getAllAuditTasksByStatus(String userId, QueryFilter filter);

    TaskEventQueryBuilder taskEventQuery();

    TaskVariableQueryBuilder taskVariableQuery();

    AuditTaskQueryBuilder auditTaskQuery();

}