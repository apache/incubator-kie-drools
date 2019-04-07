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
package org.jbpm.runtime.manager.impl.task;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.PersistableRunner;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.api.task.TaskLifeCycleEventListener;
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
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.EventService;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.internal.task.api.model.TaskDef;
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.internal.task.query.TaskSummaryQueryBuilder;

import java.lang.reflect.InvocationHandler;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * Fully synchronized <code>TaskService</code> implementation used by the <code>SingletonRuntimeManager</code>.
 * Synchronization is done on <code>CommandService</code> of the <code>KieSession</code> to ensure correctness
 * until transaction completion.
 *
 * TODO: use the java {@link InvocationHandler}/proxy mechanism to make this class *much* shorter..
 */
// TODO: use the Ink
public class SynchronizedTaskService
            implements InternalTaskService, EventService<TaskLifeCycleEventListener> {


	private Object ksession;
	private InternalTaskService taskService;

	public SynchronizedTaskService(KieSession ksession, InternalTaskService taskService) {
	    if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
	        this.ksession = ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
	    } else {
	        this.ksession = ksession;
	    }
		this.taskService = taskService;
	}


    @Override
    public void activate(long taskId, String userId) {
        synchronized (ksession) {
            taskService.activate(taskId, userId);
        }
    }

    @Override
    public void addGroup(Group group) {
        synchronized (ksession) {
            taskService.addGroup(group);
        }
    }

    @Override
    public void addUser(User user) {
        synchronized (ksession) {
            taskService.addUser(user);
        }
    }

    @Override
    public int archiveTasks(List<TaskSummary> tasks) {
        synchronized (ksession) {
            return taskService.archiveTasks(tasks);
        }
    }

    @Override
    public void claim(long taskId, String userId) {
        synchronized (ksession) {
            taskService.claim(taskId, userId);
        }
    }

    @Override
    public void claimNextAvailable(String userId, String language) {
        synchronized (ksession) {
            taskService.claimNextAvailable(userId, language);
        }
    }

    @Override
    public void complete(long taskId, String userId, Map<String, Object> data) {
        synchronized (ksession) {
            taskService.complete(taskId, userId, data);
        }
    }

    @Override
    public void delegate(long taskId, String userId, String targetUserId) {
        synchronized (ksession) {
            taskService.delegate(taskId, userId, targetUserId);
        }
    }

    @Override
    public void deleteFault(long taskId, String userId) {
        synchronized (ksession) {
            taskService.deleteFault(taskId, userId);
        }
    }

    @Override
    public void deleteOutput(long taskId, String userId) {
        synchronized (ksession) {
            taskService.deleteOutput(taskId, userId);
        }
    }

    @Override
    public void deployTaskDef(TaskDef def) {
        synchronized (ksession) {
            taskService.deployTaskDef(def);
        }
    }

    @Override
    public void exit(long taskId, String userId) {
        synchronized (ksession) {
            taskService.exit(taskId, userId);
        }
    }

    @Override
    public void fail(long taskId, String userId, Map<String, Object> faultData) {
        synchronized (ksession) {
            taskService.fail(taskId, userId, faultData);
        }
    }

    @Override
    public void forward(long taskId, String userId, String targetEntityId) {
        synchronized (ksession) {
            taskService.forward(taskId, userId, targetEntityId);
        }
    }

    @Override
    public List<TaskSummary> getActiveTasks() {
        synchronized (ksession) {
            return taskService.getActiveTasks();
        }
    }

    @Override
    public List<TaskSummary> getActiveTasks(Date since) {
        synchronized (ksession) {
            return taskService.getActiveTasks(since);
        }
    }

    @Override
    public List<TaskDef> getAllTaskDef(String filter) {
        synchronized (ksession) {
            return taskService.getAllTaskDef(filter);
        }
    }

    @Override
    public List<TaskSummary> getArchivedTasks() {
        synchronized (ksession) {
            return taskService.getArchivedTasks();
        }
    }

    @Override
    public List<TaskSummary> getCompletedTasks() {
        synchronized (ksession) {
            return taskService.getCompletedTasks();
        }
    }

    @Override
    public List<TaskSummary> getCompletedTasks(Date since) {
        synchronized (ksession) {
            return taskService.getCompletedTasks(since);
        }
    }

    @Override
    public List<TaskSummary> getCompletedTasksByProcessId(Long processId) {
        synchronized (ksession) {
            return taskService.getCompletedTasksByProcessId(processId);
        }
    }

    @Override
    public Group getGroupById(String groupId) {
        synchronized (ksession) {
            return taskService.getGroupById(groupId);
        }
    }

    @Override
    public List<Group> getGroups() {
        synchronized (ksession) {
            return taskService.getGroups();
        }
    }

    @Override
    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId,
            String userId) {
        synchronized (ksession) {
            return taskService.getSubTasksAssignedAsPotentialOwner(parentId, userId);
        }
    }

    @Override
    public List<TaskSummary> getSubTasksByParent(long parentId) {
        synchronized (ksession) {
            return taskService.getSubTasksByParent(parentId);
        }
    }

    @Override
    public int getPendingSubTasksByParent(long parentId) {
        synchronized (ksession) {
            return taskService.getPendingSubTasksByParent(parentId);
        }
    }

    @Override
    public Task getTaskByWorkItemId(long workItemId) {
        synchronized (ksession) {
            return taskService.getTaskByWorkItemId(workItemId);
        }
    }

    @Override
    public TaskDef getTaskDefById(String id) {
        synchronized (ksession) {
            return taskService.getTaskDefById(id);
        }
    }

    @Override
    public Task getTaskById(long taskId) {
        synchronized (ksession) {
            return taskService.getTaskById(taskId);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(
            String userId, String language) {
        synchronized (ksession) {
            return taskService.getTasksAssignedAsBusinessAdministrator(userId, language);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId) {
        synchronized (ksession) {
            return  taskService.getTasksAssignedAsExcludedOwner(userId);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId,
            List<String> groupIds) {
        synchronized (ksession) {
            return  taskService.getTasksAssignedAsPotentialOwner(userId, groupIds);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResults) {
        synchronized (ksession) {
           return  taskService.getTasksAssignedAsPotentialOwner(userId, groupIds, language, firstResult, maxResults);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId,
            String language) {
        synchronized (ksession) {
            return  taskService.getTasksAssignedAsPotentialOwner(userId, language);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(
            String userId, List<Status> status, String language) {
        synchronized (ksession) {
            return  taskService.getTasksAssignedAsPotentialOwnerByStatus(userId, status, language);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(
            String userId, List<String> groupIds, List<Status> status) {
        synchronized (ksession) {
            return  taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup(userId, groupIds, status);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsRecipient(String userId) {
        synchronized (ksession) {
            return  taskService.getTasksAssignedAsRecipient(userId);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId) {
        synchronized (ksession) {
            return  taskService.getTasksAssignedAsTaskInitiator(userId);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId) {
        synchronized (ksession) {
            return  taskService.getTasksAssignedAsTaskStakeholder(userId);
        }
    }

    @Override
    public List<TaskSummary> getTasksOwned(String userId, String language) {
        synchronized (ksession) {
            return  taskService.getTasksOwned(userId, language);
        }
    }

    @Override
    public List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status,
            String language) {
        synchronized (ksession) {
            return  taskService.getTasksOwnedByStatus(userId, status, language);
        }
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDate(String userId,
            List<Status> statuses, Date expirationDate) {
        synchronized (ksession) {
            return  taskService.getTasksOwnedByExpirationDate(userId, statuses, expirationDate);
        }
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(
            String userId, List<Status> statuses, Date expirationDate) {
        synchronized (ksession) {
            return  taskService.getTasksOwnedByExpirationDateOptional(userId, statuses, expirationDate);
        }
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateBeforeSpecifiedDate(String userId, List<Status> status, Date date) {
        synchronized (ksession) {
            return taskService.getTasksOwnedByExpirationDateBeforeSpecifiedDate(userId, status, date);
        }
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceId(
            long processInstanceId, List<Status> status, String language) {
        synchronized (ksession) {
            return  taskService.getTasksByStatusByProcessInstanceId(processInstanceId, status, language);
        }
    }

    @Override
    public List<TaskSummary> getTasksByStatusByProcessInstanceIdByTaskName(
            long processInstanceId, List<Status> status, String taskName) {
        synchronized (ksession) {
            return  taskService.getTasksByStatusByProcessInstanceIdByTaskName(processInstanceId, status, taskName);
        }
    }

    @Override
    public List<Long> getTasksByProcessInstanceId(long processInstanceId) {
        synchronized (ksession) {
            return  taskService.getTasksByProcessInstanceId(processInstanceId);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByProcessId( String userId, String processId ) {
        synchronized (ksession) {
            return  taskService.getTasksAssignedAsPotentialOwnerByProcessId(userId, processId);
        }
    }

    @Override
    public User getUserById(String userId) {
        synchronized (ksession) {
            return  taskService.getUserById(userId);
        }
    }

    @Override
    public List<User> getUsers() {
        synchronized (ksession) {
            return  taskService.getUsers();
        }
    }

    @Override
    public long addTask(Task task, Map<String, Object> params) {
        synchronized (ksession) {
            return  taskService.addTask(task, params);
        }
    }

    @Override
    public long addTask(Task task, ContentData data) {
        synchronized (ksession) {
            return  taskService.addTask(task, data);
        }
    }

    @Override
    public void release(long taskId, String userId) {
        synchronized (ksession) {
            taskService.release(taskId, userId);
        }
    }

    @Override
    public void remove(long taskId, String userId) {
        synchronized (ksession) {
            taskService.remove(taskId, userId);
        }
    }

    @Override
    public void removeGroup(String groupId) {
        synchronized (ksession) {
            taskService.removeGroup(groupId);
        }
    }

    @Override
    public int removeTasks(List<TaskSummary> tasks) {
        synchronized (ksession) {
            return  taskService.removeTasks(tasks);
        }
    }

    @Override
    public void removeUser(String userId) {
        synchronized (ksession) {
            taskService.removeUser(userId);
        }
    }

    @Override
    public void resume(long taskId, String userId) {
        synchronized (ksession) {
            taskService.resume(taskId, userId);
        }
    }

    @Override
    public void setFault(long taskId, String userId, FaultData fault) {
        synchronized (ksession) {
            taskService.setFault(taskId, userId, fault);
        }
    }

    @Override
    public void setOutput(long taskId, String userId, Object outputContentData) {
        synchronized (ksession) {
            taskService.setOutput(taskId, userId, outputContentData);
        }
    }

    @Override
    public void setPriority(long taskId, int priority) {
        synchronized (ksession) {
            taskService.setPriority(taskId, priority);
        }
    }

    @Override
    public void setTaskNames(long taskId, List<I18NText> taskNames) {
        synchronized (ksession) {
            taskService.setTaskNames(taskId, taskNames);
        }
    }

    @Override
    public void skip(long taskId, String userId) {
        synchronized (ksession) {
            taskService.skip(taskId, userId);
        }
    }

    @Override
    public void start(long taskId, String userId) {
        synchronized (ksession) {
            taskService.start(taskId, userId);
        }
    }

    @Override
    public void stop(long taskId, String userId) {
        synchronized (ksession) {
            taskService.stop(taskId, userId);
        }
    }

    @Override
    public void suspend(long taskId, String userId) {
        synchronized (ksession) {
            taskService.suspend(taskId, userId);
        }
    }

    @Override
    public void undeployTaskDef(String id) {
        synchronized (ksession) {
            taskService.undeployTaskDef(id);
        }
    }

    @Override
    public List<TaskEvent> getTaskEventsById(long taskId) {
        synchronized (ksession) {
            return  taskService.getTaskEventsById(taskId);
        }
    }

    @Override
    public UserInfo getUserInfo() {
        synchronized (ksession) {
            return  taskService.getUserInfo();
        }
    }

    @Override
    public void setUserInfo(UserInfo userInfo) {
        synchronized (ksession) {
            taskService.setUserInfo(userInfo);
        }
    }

    @Override
    public void addUsersAndGroups(Map<String, User> users,
            Map<String, Group> groups) {
        synchronized (ksession) {
            taskService.addUsersAndGroups(users, groups);
        }
    }

    @Override
    public void nominate(long taskId, String userId,
            List<OrganizationalEntity> potentialOwners) {
        synchronized (ksession) {
            taskService.nominate(taskId, userId, potentialOwners);
        }
    }

    @Override
    public int removeAllTasks() {
        synchronized (ksession) {
            return  taskService.removeAllTasks();
        }
    }

    @Override
    public long addContent(long taskId, Content content) {
        synchronized (ksession) {
            return  taskService.addContent(taskId, content);
        }
    }

    @Override
    public long addContent(long taskId, Map<String, Object> params) {
        synchronized (ksession) {
            return  taskService.addContent(taskId, params);
        }
    }

    @Override
    public void deleteContent(long taskId, long contentId) {
        synchronized (ksession) {
            taskService.deleteContent(taskId, contentId);
        }
    }

    @Override
    public List<Content> getAllContentByTaskId(long taskId) {
        synchronized (ksession) {
            return  taskService.getAllContentByTaskId(taskId);
        }
    }

    @Override
    public Content getContentById(long contentId) {
        synchronized (ksession) {
            return  taskService.getContentById(contentId);
        }
    }

    @Override
    public long addAttachment(long taskId, Attachment attachment,
            Content content) {
        synchronized (ksession) {
            return  taskService.addAttachment(taskId, attachment, content);
        }
    }

    @Override
    public void deleteAttachment(long taskId, long attachmentId) {
        synchronized (ksession) {
            taskService.deleteAttachment(taskId, attachmentId);
        }
    }

    @Override
    public List<Attachment> getAllAttachmentsByTaskId(long taskId) {
        synchronized (ksession) {
            return  taskService.getAllAttachmentsByTaskId(taskId);
        }
    }

    @Override
    public Attachment getAttachmentById(long attachId) {
        synchronized (ksession) {
            return  taskService.getAttachmentById(attachId);
        }
    }

    @Override
    public void removeTaskEventsById(long taskId) {
        synchronized (ksession) {
            taskService.removeTaskEventsById(taskId);
        }
    }

    @Override
    public OrganizationalEntity getOrganizationalEntityById(String entityId) {
        synchronized (ksession) {
            return  taskService.getOrganizationalEntityById(entityId);
        }
    }

    @Override
    public void setExpirationDate(long taskId, Date date) {
        synchronized (ksession) {
            taskService.setExpirationDate(taskId, date);
        }
    }

    @Override
    public void setDescriptions(long taskId, List<I18NText> descriptions) {
        synchronized (ksession) {
            taskService.setDescriptions(taskId, descriptions);
        }
    }

    @Override
    public void setSkipable(long taskId, boolean skipable) {
        synchronized (ksession) {
            taskService.setSkipable(taskId, skipable);
        }
    }

    @Override
    public void setSubTaskStrategy(long taskId, SubTasksStrategy strategy) {
        synchronized (ksession) {
            taskService.setSubTaskStrategy(taskId, strategy);
        }
    }

    @Override
    public int getPriority(long taskId) {
        synchronized (ksession) {
            return  taskService.getPriority(taskId);
        }
    }

    @Override
    public Date getExpirationDate(long taskId) {
        synchronized (ksession) {
            return  taskService.getExpirationDate(taskId);
        }
    }

    @Override
    public List<I18NText> getDescriptions(long taskId) {
        synchronized (ksession) {
            return  taskService.getDescriptions(taskId);
        }
    }

    @Override
    public boolean isSkipable(long taskId) {
        synchronized (ksession) {
            return  taskService.isSkipable(taskId);
        }
    }

    @Override
    public SubTasksStrategy getSubTaskStrategy(long taskId) {
        synchronized (ksession) {
            return  taskService.getSubTaskStrategy(taskId);
        }
    }

    @Override
    public Task getTaskInstanceById(long taskId) {
        synchronized (ksession) {
            return  taskService.getTaskInstanceById(taskId);
        }
    }

    @Override
    public int getCompletedTaskByUserId(String userId) {
        synchronized (ksession) {
            return  taskService.getCompletedTaskByUserId(userId);
        }
    }

    @Override
    public int getPendingTaskByUserId(String userId) {
        synchronized (ksession) {
            return  taskService.getPendingTaskByUserId(userId);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedByGroup(String groupId) {
        synchronized (ksession) {
            return  taskService.getTasksAssignedByGroup(groupId);
        }
    }

    @Override
    public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds) {
        synchronized (ksession) {
            return  taskService.getTasksAssignedByGroups(groupIds);
        }
    }

    @Override
    public Long addComment(long taskId, Comment comment) {
        synchronized (ksession) {
            return  taskService.addComment(taskId, comment);
        }
    }

    @Override
    public Long addComment( long taskId, String addedByUserId, String commentText ) {
        synchronized (ksession) {
            return  taskService.addComment(taskId, addedByUserId, commentText);
        }
    }

    @Override
    public void deleteComment(long taskId, long commentId) {
        synchronized (ksession) {
            taskService.deleteComment(taskId, commentId);
        }
    }

    @Override
    public List<Comment> getAllCommentsByTaskId(long taskId) {
        synchronized (ksession) {
            return  taskService.getAllCommentsByTaskId(taskId);
        }
    }

    @Override
    public Comment getCommentById(long commentId) {
        synchronized (ksession) {
            return  taskService.getCommentById(commentId);
        }
    }

    @Override
    public Map<String, Object> getTaskContent(long taskId) {
        synchronized (ksession) {
            return  taskService.getTaskContent(taskId);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerTaskEventListener(TaskLifeCycleEventListener taskLifecycleEventListener) {
        synchronized (ksession) {
            ((EventService<TaskLifeCycleEventListener>)taskService).registerTaskEventListener(taskLifecycleEventListener);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskLifeCycleEventListener> getTaskEventListeners() {
        synchronized (ksession) {
            return ((EventService<TaskLifeCycleEventListener>) taskService).getTaskEventListeners();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void clearTaskEventListeners() {
        synchronized (ksession) {
            ((EventService<TaskLifeCycleEventListener>) taskService).clearTaskEventListeners();
        }
    }

	@SuppressWarnings("unchecked")
	@Override
	public void removeTaskEventListener(TaskLifeCycleEventListener listener) {
		synchronized (ksession) {
            ((EventService<TaskLifeCycleEventListener>) taskService).removeTaskEventListener(listener);
        }
	}

	@Override
	public <T> T execute(Command<T> command) {
		synchronized (ksession) {
			return taskService.execute(command);
		}
	}

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(String userId, List<Status> statuses, Date expirationDate) {
       synchronized (ksession) {
            return  taskService.getTasksAssignedAsPotentialOwnerByExpirationDate(userId, statuses, expirationDate);
       }
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId, List<Status> statuses, Date expirationDate) {
       synchronized (ksession) {
            return  taskService.getTasksAssignedAsPotentialOwnerByExpirationDateOptional(userId, statuses, expirationDate);
       }
    }

    @Override
    public Map<Long, List<OrganizationalEntity>> getPotentialOwnersForTaskIds(List<Long> taskIds) {
       synchronized (ksession) {
            return  taskService.getPotentialOwnersForTaskIds(taskIds);
       }
    }


    @Override
    public void addMarshallerContext(String ownerId, ContentMarshallerContext context) {
       synchronized (ksession) {
           if (taskService != null) {
               taskService.addMarshallerContext(ownerId, context);
           }
       }
    }


    @Override
    public void removeMarshallerContext(String ownerId) {
       synchronized (ksession) {
           if (taskService != null) {
               taskService.removeMarshallerContext(ownerId);
           }
       }
    }


    @Override
    public ContentMarshallerContext getMarshallerContext(Task task) {
        synchronized (ksession) {
            if (taskService != null) {
                return taskService.getMarshallerContext(task);
            }

            return null;
        }
    }

	@Override
	public List<TaskSummary> getTasksOwned(String userId, List<Status> status, QueryFilter filter) {
	    return taskService.getTasksOwned(userId, status, filter);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, List<Status> status, QueryFilter filter) {
	    return taskService.getTasksAssignedAsPotentialOwner(userId, groupIds, status, filter);
	}

    @Override
    public TaskSummaryQueryBuilder taskSummaryQuery( String userId ) {
        return taskService.taskSummaryQuery(userId);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsBusinessAdministratorByStatus( String userId, String language, List<Status> statuses ) {
        synchronized( ksession ) {
            return taskService.getTasksAssignedAsBusinessAdministratorByStatus(userId, language, statuses);
        }
    }

    @Override
    public void executeReminderForTask( long taskId, String initiator ) {
        synchronized( ksession ) {
            taskService.executeReminderForTask(taskId, initiator);
        }

    }

    @Override
    public long setDocumentContentFromUser( long taskId, String userId, byte[] byteContent ) {
        synchronized( ksession ) {
            return taskService.setDocumentContentFromUser(taskId, userId, byteContent);
        }
    }

    @Override
    public long addOutputContentFromUser( long taskId, String userId, Map<String, Object> params ) {
        synchronized( ksession ) {
            return taskService.addOutputContentFromUser(taskId, userId, params);
        }
    }

    @Override
    public Content getContentByIdForUser( long contentId, String userId ) {
        synchronized( ksession ) {
            return taskService.getContentByIdForUser(contentId, userId);
        }
    }

    @Override
    public Map<String, Object> getOutputContentMapForUser( long taskId, String userId ) {
        synchronized( ksession ) {
            return taskService.getOutputContentMapForUser(taskId, userId);
        }
    }


}
