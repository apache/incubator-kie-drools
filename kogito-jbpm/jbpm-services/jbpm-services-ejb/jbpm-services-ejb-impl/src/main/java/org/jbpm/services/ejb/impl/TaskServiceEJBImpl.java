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

package org.jbpm.services.ejb.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.runtime.manager.impl.identity.UserDataServiceProvider;
import org.jbpm.services.ejb.TaskServiceEJBLocal;
import org.jbpm.services.task.HumanTaskConfigurator;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.kie.api.command.Command;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
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
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.QueryFilter;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.internal.task.api.model.TaskDef;
import org.kie.internal.task.api.model.TaskEvent;

@Stateless
public class TaskServiceEJBImpl implements InternalTaskService, TaskService, TaskServiceEJBLocal {
	
	
	private InternalTaskService delegate;
	
	@PersistenceUnit(name="org.jbpm.domain")
	private EntityManagerFactory emf;
	
	@PostConstruct
	public void configureDelegate() {
		UserGroupCallback callback = UserDataServiceProvider.getUserGroupCallback();
		
		HumanTaskConfigurator configurator = HumanTaskServiceFactory.newTaskServiceConfigurator()
                .entityManagerFactory( emf )
                .userGroupCallback( callback );
		
		delegate = (InternalTaskService) configurator.getTaskService();
	}
	
	// implemented methods
	@Override
	public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
		return delegate.getTasksAssignedAsBusinessAdministrator(userId, language);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
		return delegate.getTasksAssignedAsPotentialOwner(userId, language);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language) {
		return delegate.getTasksAssignedAsPotentialOwnerByStatus(userId, status, language);
	}

	@Override
	public List<TaskSummary> getTasksOwned(String userId, String language) {
		return delegate.getTasksOwned(userId, language);
	}

	@Override
	public List<TaskSummary> getTasksOwnedByStatus(String userId, List<Status> status, String language) {
		return delegate.getTasksOwnedByStatus(userId, status, language);
	}

	@Override
	public List<TaskSummary> getTasksByStatusByProcessInstanceId(long processInstanceId, List<Status> status, String language) {
		return delegate.getTasksByStatusByProcessInstanceId(processInstanceId, status, language);
	}

	@Override
	public List<Long> getTasksByProcessInstanceId(long processInstanceId) {
		return delegate.getTasksByProcessInstanceId(processInstanceId);
	}

	@Override
	public List<TaskSummary> getActiveTasks() {
		return delegate.getActiveTasks();
	}

	@Override
	public List<TaskSummary> getActiveTasks(Date since) {
		return delegate.getActiveTasks(since);
	}

	@Override
	public List<TaskSummary> getTasksOwned(String userId, List<Status> status, QueryFilter filter) {
		return delegate.getTasksOwned(userId, status, filter);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId,
			List<String> groupIds, List<Status> status, QueryFilter filter) {
		return delegate.getTasksAssignedAsPotentialOwner(userId, groupIds, status, filter);
	}

	@Override
	public List<TaskSummary> getArchivedTasks() {
		return delegate.getArchivedTasks();
	}

	@Override
	public List<TaskSummary> getCompletedTasks() {
		return delegate.getCompletedTasks();
	}

	@Override
	public List<TaskSummary> getCompletedTasks(Date since) {
		return delegate.getCompletedTasks(since);
	}

	@Override
	public List<TaskSummary> getCompletedTasksByProcessId(Long processId) {
		return delegate.getCompletedTasksByProcessId(processId);
	}
	@Override
	public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId) {
		return delegate.getSubTasksAssignedAsPotentialOwner(parentId, userId);
	}

	@Override
	public List<TaskSummary> getSubTasksByParent(long parentId) {
		return delegate.getSubTasksByParent(parentId);
	}

	@Override
	public int getPendingSubTasksByParent(long parentId) {
		return delegate.getPendingSubTasksByParent(parentId);
	}
	
	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(
			String userId, List<Status> statuses, Date expirationDate) {
		return delegate.getTasksAssignedAsPotentialOwnerByExpirationDate(userId, statuses, expirationDate);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(
			String userId, List<Status> statuses, Date expirationDate) {
		return delegate.getTasksAssignedAsPotentialOwnerByExpirationDateOptional(userId, statuses, expirationDate);
	}

	@Override
	public List<TaskSummary> getTasksOwnedByExpirationDate(String userId,
			List<Status> statuses, Date expirationDate) {
		return delegate.getTasksOwnedByExpirationDate(userId, statuses, expirationDate);
	}

	@Override
	public List<TaskSummary> getTasksOwnedByExpirationDateOptional(
			String userId, List<Status> statuses, Date expirationDate) {
		return delegate.getTasksOwnedByExpirationDateOptional(userId, statuses, expirationDate);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId) {
		return delegate.getTasksAssignedAsExcludedOwner(userId);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds) {
		return delegate.getTasksAssignedAsPotentialOwner(userId, groupIds);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId,
			List<String> groupIds, int firstResult, int maxResults) {
		return delegate.getTasksAssignedAsPotentialOwner(userId, groupIds, firstResult, maxResults);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(
			String userId, List<String> groupIds, List<Status> status) {
		return delegate.getTasksAssignedAsPotentialOwnerByStatusByGroup(userId, groupIds, status);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsRecipient(String userId) {
		return delegate.getTasksAssignedAsRecipient(userId);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId) {
		return delegate.getTasksAssignedAsTaskInitiator(userId);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId) {
		return delegate.getTasksAssignedAsTaskStakeholder(userId);
	}

	@Override
	public List<TaskSummary> getTasksOwnedByExpirationDateBeforeSpecifiedDate(
			String userId, List<Status> status, Date date) {
		return delegate.getTasksOwnedByExpirationDateBeforeSpecifiedDate(userId, status, date);
	}

	@Override
	public List<TaskSummary> getTasksByStatusByProcessInstanceIdByTaskName(
			long processInstanceId, List<Status> status, String taskName) {
		return delegate.getTasksByStatusByProcessInstanceIdByTaskName(processInstanceId, status, taskName);
	}

	@Override
	public Map<Long, List<OrganizationalEntity>> getPotentialOwnersForTaskIds(
			List<Long> taskIds) {
		return delegate.getPotentialOwnersForTaskIds(taskIds);
	}
	@Override
	public List<TaskSummary> getTasksAssignedByGroup(String groupId) {
		return delegate.getTasksAssignedByGroup(groupId);
	}

	@Override
	public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds) {
		return delegate.getTasksAssignedByGroups(groupIds);
	}
	@Override
	public List<TaskSummary> getTasksByVariousFields(List<Long> workItemIds,
			List<Long> taskIds, List<Long> procInstIds, List<String> busAdmins,
			List<String> potOwners, List<String> taskOwners,
			List<Status> status, boolean union) {
		return delegate.getTasksByVariousFields(workItemIds, taskIds, procInstIds, 
				busAdmins, potOwners, taskOwners, status, union);
	}

	@Override
	public List<TaskSummary> getTasksByVariousFields(
			Map<String, List<?>> parameters, boolean union) {
		return delegate.getTasksByVariousFields(parameters, union);
	}

	// unsupported method

	@Override
	public void activate(long taskId, String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void claim(long taskId, String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void claimNextAvailable(String userId, String language) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void complete(long taskId, String userId, Map<String, Object> data) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void delegate(long taskId, String userId, String targetUserId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void exit(long taskId, String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void fail(long taskId, String userId, Map<String, Object> faultData) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void forward(long taskId, String userId, String targetEntityId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public Task getTaskByWorkItemId(long workItemId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public Task getTaskById(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}	

	@Override
	public long addTask(Task task, Map<String, Object> params) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void release(long taskId, String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void resume(long taskId, String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void skip(long taskId, String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void start(long taskId, String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void stop(long taskId, String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void suspend(long taskId, String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public Content getContentById(long contentId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public Attachment getAttachmentById(long attachId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public Map<String, Object> getTaskContent(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public <T> T execute(Command<T> command) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void addGroup(Group group) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void addUser(User user) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public int archiveTasks(List<TaskSummary> tasks) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void claim(long taskId, String userId, List<String> groupIds) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void claimNextAvailable(String userId, List<String> groupIds) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void deleteFault(long taskId, String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void deleteOutput(long taskId, String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void deployTaskDef(TaskDef def) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public List<TaskDef> getAllTaskDef(String filter) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public Group getGroupById(String groupId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public List<Group> getGroups() {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public TaskDef getTaskDefById(String id) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}


	@Override
	public User getUserById(String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public List<User> getUsers() {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public long addTask(Task task, ContentData data) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void remove(long taskId, String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void removeGroup(String groupId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public int removeTasks(List<TaskSummary> tasks) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void removeUser(String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void setFault(long taskId, String userId, FaultData fault) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void setOutput(long taskId, String userId, Object outputContentData) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void setPriority(long taskId, int priority) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void setTaskNames(long taskId, List<I18NText> taskNames) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void undeployTaskDef(String id) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public List<TaskEvent> getTaskEventsById(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public UserInfo getUserInfo() {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void setUserInfo(UserInfo userInfo) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void addUsersAndGroups(Map<String, User> users,
			Map<String, Group> groups) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public int removeAllTasks() {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public long addContent(long taskId, Content content) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public long addContent(long taskId, Map<String, Object> params) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void deleteContent(long taskId, long contentId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public List<Content> getAllContentByTaskId(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public long addAttachment(long taskId, Attachment attachment,
			Content content) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void deleteAttachment(long taskId, long attachmentId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public List<Attachment> getAllAttachmentsByTaskId(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void removeTaskEventsById(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public OrganizationalEntity getOrganizationalEntityById(String entityId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void setExpirationDate(long taskId, Date date) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void setDescriptions(long taskId, List<I18NText> descriptions) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void setSkipable(long taskId, boolean skipable) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void setSubTaskStrategy(long taskId, SubTasksStrategy strategy) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public int getPriority(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public Date getExpirationDate(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public List<I18NText> getDescriptions(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public boolean isSkipable(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public SubTasksStrategy getSubTaskStrategy(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public Task getTaskInstanceById(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public int getCompletedTaskByUserId(String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public int getPendingTaskByUserId(String userId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public long addComment(long taskId, Comment comment) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void deleteComment(long taskId, long commentId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public List<Comment> getAllCommentsByTaskId(long taskId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public Comment getCommentById(long commentId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void addMarshallerContext(String ownerId,
			ContentMarshallerContext context) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public void removeMarshallerContext(String ownerId) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}

	@Override
	public ContentMarshallerContext getMarshallerContext(Task task) {
		throw new UnsupportedOperationException("This method is not supported, use UserTaskService instead");
	}


}
