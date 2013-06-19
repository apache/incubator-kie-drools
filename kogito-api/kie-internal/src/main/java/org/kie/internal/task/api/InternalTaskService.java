/*
 * Copyright 2013 JBoss Inc
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
package org.kie.internal.task.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.CommandExecutor;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.internal.task.api.model.TaskDef;
import org.kie.internal.task.api.model.TaskEvent;

/**
 * The Task Service Entry Point serves as 
 *  facade of all the other services, providing a single entry point
 *  to access to all the services
 */
public interface InternalTaskService extends TaskService, CommandExecutor {
    
    void addGroup(Group group);

    void addUser(User user);

    int archiveTasks(List<TaskSummary> tasks);

    void claim(long taskId, String userId, List<String> groupIds);

    void claimNextAvailable(String userId, List<String> groupIds, String language);

    void deleteFault(long taskId, String userId);

    void deleteOutput(long taskId, String userId);

    void deployTaskDef(TaskDef def);
    
    List<TaskSummary> getActiveTasks();

    List<TaskSummary> getActiveTasks(Date since);

    List<TaskDef> getAllTaskDef(String filter);

    List<TaskSummary> getArchivedTasks();

    List<TaskSummary> getCompletedTasks();

    List<TaskSummary> getCompletedTasks(Date since);

    List<TaskSummary> getCompletedTasksByProcessId(Long processId);

    Group getGroupById(String groupId);

    List<Group> getGroups();

    List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language);

    List<TaskSummary> getSubTasksByParent(long parentId);
    
    int getPendingSubTasksByParent(long parentId);

    TaskDef getTaskDefById(String id);

    List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<Status> statuses, Date expirationDate);
    
    List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<Status> statuses, Date expirationDate);
    
    List<TaskSummary> getTasksOwnedByExpirationDate(String userId, List<Status> statuses, Date expirationDate);
    
    List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<Status> statuses, Date expirationDate);
    
    List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResults);

    List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language);

    List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language);

    List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language);

    List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language);
    
    List<TaskSummary> getTasksOwnedByExpirationDateBeforeSpecifiedDate(String userId, List<Status> status, Date date);
    
    List<TaskSummary> getTasksByStatusByProcessInstanceIdByTaskName(long processInstanceId, List<Status> status, String taskName, String language);
    
    Map<Long, List<OrganizationalEntity>> getPotentialOwnersForTaskIds(List<Long> taskIds);
    
    User getUserById(String userId);

    List<User> getUsers();

    long addTask(Task task, ContentData data);

    void remove(long taskId, String userId);

    void removeGroup(String groupId);

    int removeTasks(List<TaskSummary> tasks);

    void removeUser(String userId);

    void setFault(long taskId, String userId, FaultData fault);

    void setOutput(long taskId, String userId, Object outputContentData);

    void setPriority(long taskId, int priority);
    
    void setTaskNames(long taskId, List<I18NText> taskNames);

    void undeployTaskDef(String id);

    List<TaskEvent> getTaskEventsById(long taskId);

    UserInfo getUserInfo();

    void setUserInfo(UserInfo userInfo);

    void addUsersAndGroups(Map<String, User> users, Map<String, Group> groups);

    int removeAllTasks();

    long addContent(long taskId, Content content);
    
    long addContent(long taskId, Map<String, Object> params);

    void deleteContent(long taskId, long contentId);

    List<Content> getAllContentByTaskId(long taskId);

    long addAttachment(long taskId, Attachment attachment, Content content);

    void deleteAttachment(long taskId, long attachmentId);

    List<Attachment> getAllAttachmentsByTaskId(long taskId);

    void removeTaskEventsById(long taskId);

    OrganizationalEntity getOrganizationalEntityById(String entityId);

    void setExpirationDate(long taskId, Date date);

    void setDescriptions(long taskId, List<I18NText> descriptions);

    void setSkipable(long taskId, boolean skipable);

    void setSubTaskStrategy(long taskId, SubTasksStrategy strategy);

    int getPriority(long taskId);

    Date getExpirationDate(long taskId);

    List<I18NText> getDescriptions(long taskId);

    boolean isSkipable(long taskId);
    
    SubTasksStrategy getSubTaskStrategy(long taskId);

    Task getTaskInstanceById(long taskId);
    
    int getCompletedTaskByUserId(String userId);

    int getPendingTaskByUserId(String userId);
    
    List<TaskSummary> getTasksAssignedByGroup(String groupId, String language); 
    
    List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds, String language); 
    
    long addComment(long taskId, Comment comment);

    void deleteComment(long taskId, long commentId);

    List<Comment> getAllCommentsByTaskId(long taskId);

    Comment getCommentById(long commentId);
    
    Map<String, Object> getTaskContent(long taskId);
}
