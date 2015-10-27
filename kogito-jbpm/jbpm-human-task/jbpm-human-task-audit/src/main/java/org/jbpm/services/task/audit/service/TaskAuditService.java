/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.services.task.audit.service;

import java.util.List;

import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.api.TaskVariable;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.model.TaskEvent;


public interface TaskAuditService {
    
    void setTaskService(TaskService taskService);
    
    List<TaskEvent> getAllTaskEvents(long taskId, QueryFilter filter);
    
    List<TaskEvent> getAllTaskEventsByProcessInstanceId(long processInstanceId, QueryFilter filter);
    
    List<AuditTask> getAllAuditTasks( QueryFilter filter);
    
    List<AuditTask> getAllAuditTasksByUser(String userId, QueryFilter filter);
    
    List<AuditTask> getAllGroupAuditTasksByUser(String userId, QueryFilter filter);
    
    List<AuditTask> getAllAdminAuditTasksByUser(String userId, QueryFilter filter);
    
    List<AuditTask> getAllAuditTasksByStatus(String userId, QueryFilter filter);
    
    List<TaskVariable> getTaskInputVariables(long taskId, QueryFilter filter);
    
    List<TaskVariable> getTaskOutputVariables(long taskId, QueryFilter filter);
    
    List<TaskSummary> getTasksByVariableName(String userId, String variableName, List<Status> statuses, QueryFilter filter);
    
    List<TaskSummary> getTasksByVariableNameAndValue(String userId, String variableName, String variableValue, List<Status> statuses, QueryFilter filter);
      
}
