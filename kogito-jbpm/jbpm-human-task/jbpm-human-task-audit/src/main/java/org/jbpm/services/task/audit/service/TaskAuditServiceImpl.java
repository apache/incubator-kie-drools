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
import org.jbpm.services.task.audit.commands.GetAllGroupAuditTasksAdminCommand;
import org.jbpm.services.task.audit.commands.GetAllGroupAuditTasksByDueDateCommand;
import org.jbpm.services.task.audit.commands.GetAllGroupAuditTasksByStatusByDueDateCommand;
import org.jbpm.services.task.audit.commands.GetAllGroupAuditTasksByStatusCommand;
import org.jbpm.services.task.audit.commands.GetAllGroupAuditTasksCommand;
import org.jbpm.services.task.audit.commands.GetAllHistoryAuditTasksByUserCommand;
import org.jbpm.services.task.audit.commands.GetAllHistoryAuditTasksCommand;
import org.jbpm.services.task.audit.commands.GetAllUserAuditTasksAdminCommand;
import org.jbpm.services.task.audit.commands.GetAllUserAuditTasksByDueDateCommand;
import org.jbpm.services.task.audit.commands.GetAllUserAuditTasksByStatusByDueDateCommand;
import org.jbpm.services.task.audit.commands.GetAllUserAuditTasksByStatusCommand;
import org.jbpm.services.task.audit.commands.GetAllUserAuditTasksCommand;
import org.jbpm.services.task.audit.commands.GetAuditEventsCommand;
import org.jbpm.services.task.audit.commands.GetGroupAuditTaskByIdCommand;
import org.jbpm.services.task.audit.commands.GetUserAuditTaskByIdCommand;
import org.jbpm.services.task.audit.impl.model.api.GroupAuditTask;
import org.jbpm.services.task.audit.impl.model.api.HistoryAuditTask;
import org.jbpm.services.task.audit.impl.model.api.UserAuditTask;
import org.kie.api.task.TaskService;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.model.TaskEvent;

/**
 *
 * @author salaboy
 */
public class TaskAuditServiceImpl implements TaskAuditService {
    
    private InternalTaskService taskService;
    
    @Override
    public List<TaskEvent> getAllTaskEvents(long taskId, int offset, int count) {
        return taskService.execute(new GetAuditEventsCommand(taskId, offset, count));
    }
    
    @Override
    public List<UserAuditTask> getAllUserAuditTasks(String userId, int offset, int count) {
        return taskService.execute(new GetAllUserAuditTasksCommand(userId , offset, count));
    }
    
    @Override
    public List<UserAuditTask> getAllUserAuditTasksByStatus(String userId, List<String> statuses, int offset, int count) {
        List<UserAuditTask> execute = null;
        try{
             execute = taskService.execute(new GetAllUserAuditTasksByStatusCommand(userId, statuses, offset, count));
        }catch (Exception e){
            e.printStackTrace();
        }
        return execute;
    }
    
    @Override
    public List<UserAuditTask> getAllUserAuditTasksByDueDate(String userId, Date dueDate, int offset, int count) {
        return taskService.execute(new GetAllUserAuditTasksByDueDateCommand(userId, dueDate, offset, count));
    }
    
    @Override
    public List<UserAuditTask> getAllUserAuditTasksByStatusByDueDate(String userId, List<String> statuses, Date dueDate, int offset, int count) {
        return taskService.execute(new GetAllUserAuditTasksByStatusByDueDateCommand(userId, statuses, dueDate, offset, count));
    }
    
    @Override
    public List<UserAuditTask> getAllUserAuditTasksByStatusByDueDateOptional(String userId, List<String> statuses, Date dueDate, int offset, int count) {
        return taskService.execute(new GetAllUserAuditTasksByStatusByDueDateCommand(userId, statuses, dueDate, offset, count));
    }
    
    @Override
    public List<GroupAuditTask> getAllGroupAuditTasks(String groupIds, int offset, int count) {
        return taskService.execute(new GetAllGroupAuditTasksCommand(groupIds, offset, count));
    }

    @Override
    public List<GroupAuditTask> getAllGroupAuditTasksByStatus(String groupIds, List<String> statuses, int offset, int count) {
        return taskService.execute(new GetAllGroupAuditTasksByStatusCommand(groupIds, statuses, offset, count));
    }

    @Override
    public List<GroupAuditTask> getAllGroupAuditTasksByDueDate(String groupIds, Date dueDate, int offset, int count) {
        return taskService.execute(new GetAllGroupAuditTasksByDueDateCommand(groupIds, dueDate, offset, count));
    }

    @Override
    public List<GroupAuditTask> getAllGroupAuditTasksByStatusByDueDate(String groupIds, List<String> statuses, Date dueDate, int offset, int count) {
        return taskService.execute(new GetAllGroupAuditTasksByStatusByDueDateCommand(groupIds, statuses, dueDate, offset, count));
    }

    @Override
    public List<GroupAuditTask> getAllGroupAuditTasksByStatusByDueDateOptional(String groupIds, List<String> statuses, Date dueDate, int offset, int count) {
        return taskService.execute(new GetAllGroupAuditTasksByStatusByDueDateCommand(groupIds, statuses, dueDate, offset, count));
    }
  
    @Override
    public List<UserAuditTask> getAllUserAuditTasksAdmin(int offset, int count) {
        return taskService.execute(new GetAllUserAuditTasksAdminCommand( offset, count));
    }

    @Override
    public List<GroupAuditTask> getAllGroupAuditTasksAdmin( int offset, int count) {
        return taskService.execute(new GetAllGroupAuditTasksAdminCommand( offset, count));
    }

    @Override
    public List<HistoryAuditTask> getAllHistoryAuditTasks( int offset, int count) {
        return taskService.execute(new GetAllHistoryAuditTasksCommand( offset, count));
    }

    @Override
    public List<HistoryAuditTask> getAllHistoryAuditTasksByUser(String userId, int offset, int count) {
        return taskService.execute(new GetAllHistoryAuditTasksByUserCommand(userId, offset, count));
    }
    
    @Override
    public void setTaskService(TaskService taskService) {
        this.taskService = (InternalTaskService) taskService;
    }

    @Override
    public UserAuditTask getUserAuditTaskById(long taskId) {
        return taskService.execute(new GetUserAuditTaskByIdCommand( taskId ));
    }

    @Override
    public GroupAuditTask getGroupAuditTaskById(long taskId) {
        return taskService.execute(new GetGroupAuditTaskByIdCommand( taskId ));
    }
    
}
