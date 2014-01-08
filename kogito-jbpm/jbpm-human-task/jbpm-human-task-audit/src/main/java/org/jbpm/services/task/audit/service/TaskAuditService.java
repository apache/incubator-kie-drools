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

import java.util.Date;
import java.util.List;
import org.jbpm.services.task.audit.impl.model.api.GroupAuditTask;
import org.jbpm.services.task.audit.impl.model.api.HistoryAuditTask;
import org.jbpm.services.task.audit.impl.model.api.UserAuditTask;
import org.kie.api.task.TaskService;
import org.kie.internal.task.api.model.TaskEvent;

/**
 *
 * @author salaboy
 */
public interface TaskAuditService {
    void setTaskService(TaskService taskService);
    
    List<TaskEvent> getAllTaskEvents(long taskId, int offset, int count);
    
    List<UserAuditTask> getAllUserAuditTasksAdmin(int offset, int count);
    List<UserAuditTask> getAllUserAuditTasks(String userId, int offset, int count);
    List<UserAuditTask> getAllUserAuditTasksByStatus(String userId, List<String> statuses, int offset, int count);
    List<UserAuditTask> getAllUserAuditTasksByDueDate(String userId, Date dueDate, int offset, int count);
    List<UserAuditTask> getAllUserAuditTasksByStatusByDueDate(String userId, List<String> statuses, Date dueDate, int offset, int count);
    List<UserAuditTask> getAllUserAuditTasksByStatusByDueDateOptional(String userId, List<String> statuses, Date dueDate, int offset, int count);
    
    
    List<GroupAuditTask> getAllGroupAuditTasksAdmin(int offset, int count);
    List<GroupAuditTask> getAllGroupAuditTasks(String groupIds, int offset, int count);
    List<GroupAuditTask> getAllGroupAuditTasksByStatus(String groupIds, List<String> statuses, int offset, int count);
    List<GroupAuditTask> getAllGroupAuditTasksByDueDate(String groupIds, Date dueDate, int offset, int count);
    List<GroupAuditTask> getAllGroupAuditTasksByStatusByDueDate(String groupIds, List<String> statuses, Date dueDate, int offset, int count);
    List<GroupAuditTask> getAllGroupAuditTasksByStatusByDueDateOptional(String groupIds, List<String> statuses, Date dueDate, int offset, int count);
    
    List<HistoryAuditTask> getAllHistoryAuditTasks( int offset, int count);
    List<HistoryAuditTask> getAllHistoryAuditTasksByUser(String userId, int offset, int count);
    
    
}
