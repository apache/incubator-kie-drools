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

package org.jbpm.services.task.impl.command;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.services.task.commands.ActivateTaskCommand;
import org.jbpm.services.task.commands.AddAttachmentCommand;
import org.jbpm.services.task.commands.AddCommentCommand;
import org.jbpm.services.task.commands.AddContentCommand;
import org.jbpm.services.task.commands.AddContentFromUserCommand;
import org.jbpm.services.task.commands.AddGroupCommand;
import org.jbpm.services.task.commands.AddTaskCommand;
import org.jbpm.services.task.commands.AddUserCommand;
import org.jbpm.services.task.commands.AddUsersGroupsCommand;
import org.jbpm.services.task.commands.ArchiveTasksCommand;
import org.jbpm.services.task.commands.CancelDeadlineCommand;
import org.jbpm.services.task.commands.ClaimNextAvailableTaskCommand;
import org.jbpm.services.task.commands.ClaimTaskCommand;
import org.jbpm.services.task.commands.CompleteTaskCommand;
import org.jbpm.services.task.commands.CompositeCommand;
import org.jbpm.services.task.commands.DelegateTaskCommand;
import org.jbpm.services.task.commands.DeleteAttachmentCommand;
import org.jbpm.services.task.commands.DeleteCommentCommand;
import org.jbpm.services.task.commands.DeleteContentCommand;
import org.jbpm.services.task.commands.DeleteFaultCommand;
import org.jbpm.services.task.commands.DeleteOutputCommand;
import org.jbpm.services.task.commands.DeployTaskDefCommand;
import org.jbpm.services.task.commands.ExecuteReminderCommand;
import org.jbpm.services.task.commands.ExitTaskCommand;
import org.jbpm.services.task.commands.FailTaskCommand;
import org.jbpm.services.task.commands.ForwardTaskCommand;
import org.jbpm.services.task.commands.GetActiveTasksCommand;
import org.jbpm.services.task.commands.GetAllAttachmentsCommand;
import org.jbpm.services.task.commands.GetAllCommentsCommand;
import org.jbpm.services.task.commands.GetAllContentCommand;
import org.jbpm.services.task.commands.GetAllTaskDefinitionsCommand;
import org.jbpm.services.task.commands.GetArchivedTasksCommand;
import org.jbpm.services.task.commands.GetAttachmentCommand;
import org.jbpm.services.task.commands.GetCommentCommand;
import org.jbpm.services.task.commands.GetCompletedTasksByUserCommand;
import org.jbpm.services.task.commands.GetCompletedTasksCommand;
import org.jbpm.services.task.commands.GetContentByIdCommand;
import org.jbpm.services.task.commands.GetContentByIdForUserCommand;
import org.jbpm.services.task.commands.GetContentMapForUserCommand;
import org.jbpm.services.task.commands.GetGroupCommand;
import org.jbpm.services.task.commands.GetGroupsCommand;
import org.jbpm.services.task.commands.GetOrgEntityCommand;
import org.jbpm.services.task.commands.GetPendingSubTasksCommand;
import org.jbpm.services.task.commands.GetPendingTasksByUserCommand;
import org.jbpm.services.task.commands.GetPotentialOwnersForTaskCommand;
import org.jbpm.services.task.commands.GetSubTasksCommand;
import org.jbpm.services.task.commands.GetTaskAssignedAsBusinessAdminCommand;
import org.jbpm.services.task.commands.GetTaskAssignedAsExcludedOwnerCommand;
import org.jbpm.services.task.commands.GetTaskAssignedAsInitiatorCommand;
import org.jbpm.services.task.commands.GetTaskAssignedAsPotentialOwnerCommand;
import org.jbpm.services.task.commands.GetTaskAssignedAsRecipientCommand;
import org.jbpm.services.task.commands.GetTaskAssignedAsStakeholderCommand;
import org.jbpm.services.task.commands.GetTaskAssignedByGroupsCommand;
import org.jbpm.services.task.commands.GetTaskByWorkItemIdCommand;
import org.jbpm.services.task.commands.GetTaskCommand;
import org.jbpm.services.task.commands.GetTaskContentCommand;
import org.jbpm.services.task.commands.GetTaskDefinitionCommand;
import org.jbpm.services.task.commands.GetTaskOwnedByExpDateBeforeDateCommand;
import org.jbpm.services.task.commands.GetTaskPropertyCommand;
import org.jbpm.services.task.commands.GetTasksByProcessInstanceIdCommand;
import org.jbpm.services.task.commands.GetTasksByStatusByProcessInstanceIdCommand;
import org.jbpm.services.task.commands.GetTasksOwnedCommand;
import org.jbpm.services.task.commands.GetUserCommand;
import org.jbpm.services.task.commands.GetUserInfoCommand;
import org.jbpm.services.task.commands.GetUsersCommand;
import org.jbpm.services.task.commands.NominateTaskCommand;
import org.jbpm.services.task.commands.ProcessSubTaskCommand;
import org.jbpm.services.task.commands.ReleaseTaskCommand;
import org.jbpm.services.task.commands.RemoveAllTasksCommand;
import org.jbpm.services.task.commands.RemoveGroupCommand;
import org.jbpm.services.task.commands.RemoveTaskCommand;
import org.jbpm.services.task.commands.RemoveTasksCommand;
import org.jbpm.services.task.commands.RemoveUserCommand;
import org.jbpm.services.task.commands.ResumeTaskCommand;
import org.jbpm.services.task.commands.SetTaskPropertyCommand;
import org.jbpm.services.task.commands.SkipTaskCommand;
import org.jbpm.services.task.commands.StartTaskCommand;
import org.jbpm.services.task.commands.StopTaskCommand;
import org.jbpm.services.task.commands.SuspendTaskCommand;
import org.jbpm.services.task.commands.UndeployTaskDefCommand;
import org.jbpm.services.task.events.TaskEventSupport;
import org.jbpm.services.task.impl.TaskContentRegistry;
import org.jbpm.services.task.impl.TaskSummaryQueryBuilderImpl;
import org.kie.api.command.Command;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.ExecutableRunner;
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

public class CommandBasedTaskService implements InternalTaskService, EventService<TaskLifeCycleEventListener> {

	private ExecutableRunner executor;
	private TaskEventSupport taskEventSupport;
	private Environment environment;

	private QueryFilter addLanguageFilter(String language) {
	   if( language == null ) {
	       return null;
	   }
	   QueryFilter filter = new QueryFilter();
	   filter.setCount(null);
	   filter.setOffset(null);
	   filter.setLanguage(language);
	   return filter;
	}

	public CommandBasedTaskService(ExecutableRunner executor, TaskEventSupport taskEventSupport, Environment environment) {
		this.executor = executor;
		this.taskEventSupport = taskEventSupport;
		this.environment = environment;
	}

	@Override
	public <T> T execute(Command<T> command) {
		return executor.execute(command);
	}

	public void activate(long taskId, String userId) {
		executor.execute(new ActivateTaskCommand(taskId, userId));
	}

	public void claim(long taskId, String userId) {
		executor.execute(new ClaimTaskCommand(taskId, userId));
	}

	// TODO: does not filter on language
	public void claimNextAvailable(String userId, String language) {
		executor.execute(new ClaimNextAvailableTaskCommand(userId));
	}

	public void complete(long taskId, String userId, Map<String, Object> data) {
		executor.execute(new CompositeCommand<Void>(
				new CompleteTaskCommand(taskId, userId, data),
				new ProcessSubTaskCommand(taskId, userId, data),
				new CancelDeadlineCommand(taskId, true, true)));
	}

	public void delegate(long taskId, String userId, String targetUserId) {
		executor.execute(new DelegateTaskCommand(taskId, userId, targetUserId));
	}

	public void exit(long taskId, String userId) {
		executor.execute(new CompositeCommand<Void>(
				new ExitTaskCommand(taskId, userId),
				new CancelDeadlineCommand(taskId, true, true)));
	}

	public void fail(long taskId, String userId, Map<String, Object> faultData) {
		executor.execute(new CompositeCommand<Void>(
				new FailTaskCommand(taskId, userId, faultData),
				new CancelDeadlineCommand(taskId, true, true)));
	}

	public void forward(long taskId, String userId, String targetEntityId) {
		executor.execute(new ForwardTaskCommand(taskId, userId, targetEntityId));
	}

	public Task getTaskByWorkItemId(long workItemId) {
		return executor.execute(new GetTaskByWorkItemIdCommand(workItemId));
	}

	public Task getTaskById(long taskId) {
		return executor.execute(new GetTaskCommand(taskId));
	}

	// TODO: does not filter on language
	public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
		return executor.execute(new GetTaskAssignedAsBusinessAdminCommand(userId));
	}

	// TODO: does not filter on language
	public List<TaskSummary> getTasksAssignedAsBusinessAdministratorByStatus(String userId, String language ,List<Status> statuses) {
        return executor.execute(new GetTaskAssignedAsBusinessAdminCommand(userId,statuses));
    }

	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
	    QueryFilter filter = addLanguageFilter(language);
		return getTasksAssignedAsPotentialOwner(userId, null, null, filter);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds) {
	    return getTasksAssignedAsPotentialOwner(userId, groupIds, null, null);
	}

	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, List<Status> status, QueryFilter filter) {
		return executor.execute(new GetTaskAssignedAsPotentialOwnerCommand(userId, groupIds, status, filter));
	}

	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(
			String userId, List<Status> status, String language) {
	    QueryFilter filter = addLanguageFilter(language);
		return getTasksAssignedAsPotentialOwner(userId, null, status, filter);
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDate(
			String userId, List<Status> statuses, Date expirationDate) {
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("expirationDate", expirationDate);
		return getTasksAssignedAsPotentialOwner(userId, null, statuses,
                        new QueryFilter( "t.taskData.expirationTime = :expirationDate", params, "t.id", false));
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(
			String userId, List<Status> statuses, Date expirationDate) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("expirationDate", expirationDate);
		return getTasksAssignedAsPotentialOwner(userId, null, statuses,
                        new QueryFilter( "(t.taskData.expirationTime = :expirationDate or t.taskData.expirationTime is null)", params, "t.id", false));
	}

	@Override
	// TODO: does not filter on language
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResults) {
		return getTasksAssignedAsPotentialOwner(userId, groupIds, null, new QueryFilter(firstResult, maxResults));
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(
			String userId, List<String> groupIds, List<Status> status) {
		return getTasksAssignedAsPotentialOwner(userId, groupIds, status, null);
	}

        public List<TaskSummary> getTasksOwned(String userId,
			List<Status> status, QueryFilter filter) {
		return executor.execute(new GetTasksOwnedCommand(userId, status, filter));
	}

	public List<TaskSummary> getTasksOwned(String userId, String language) {
	    QueryFilter filter = addLanguageFilter(language);
		return getTasksOwned(userId, null, filter);
	}

	public List<TaskSummary> getTasksOwnedByStatus(String userId,
			List<Status> status, String language) {
	    QueryFilter filter = addLanguageFilter(language);
		return getTasksOwned(userId, status, filter);
	}

	// TODO: does not filter on language
	public List<TaskSummary> getTasksByStatusByProcessInstanceId(
			long processInstanceId, List<Status> status, String language) {
		return executor.execute(new GetTasksByStatusByProcessInstanceIdCommand(processInstanceId, status));
	}

	public List<Long> getTasksByProcessInstanceId(long processInstanceId) {
		return executor.execute(new GetTasksByProcessInstanceIdCommand(processInstanceId));
	}

	@Override
    public TaskSummaryQueryBuilder taskSummaryQuery(String userId) {
        return new TaskSummaryQueryBuilderImpl(userId, this);
    }

    public long addTask(Task task, Map<String, Object> params) {
		return executor.execute(new AddTaskCommand(task, params));
	}

	public void release(long taskId, String userId) {
		executor.execute(new ReleaseTaskCommand(taskId, userId));
	}

	public void resume(long taskId, String userId) {
		executor.execute(new ResumeTaskCommand(taskId, userId));
	}

	public void skip(long taskId, String userId) {
		executor.execute(new CompositeCommand<Void>(
				new SkipTaskCommand(taskId, userId),
				new ProcessSubTaskCommand(taskId, userId),
				new CancelDeadlineCommand(taskId, true, true)));
	}

	public void start(long taskId, String userId) {
		executor.execute(new CompositeCommand<Void>(
				new StartTaskCommand(taskId, userId),
				new CancelDeadlineCommand(taskId, true, false)));
	}

	public void stop(long taskId, String userId) {
		executor.execute(new StopTaskCommand(taskId, userId));
	}

	public void suspend(long taskId, String userId) {
		executor.execute(new SuspendTaskCommand(taskId, userId));
	}

	public void nominate(long taskId, String userId,
			List<OrganizationalEntity> potentialOwners) {
		executor.execute(new NominateTaskCommand(taskId, userId, potentialOwners));
	}

	public Content getContentById(long contentId) {
		return executor.execute(new GetContentByIdCommand(contentId));
	}

	public Attachment getAttachmentById(long attachId) {
		return executor.execute(new GetAttachmentCommand(attachId));
	}

	@Override
	public void addGroup(Group group) {
		executor.execute(new AddGroupCommand(group.getId()));
	}

	@Override
	public void addUser(User user) {
		executor.execute(new AddUserCommand(user.getId()));
	}

	@Override
	public int archiveTasks(List<TaskSummary> tasks) {
		return executor.execute(new ArchiveTasksCommand(tasks));
	}

	@Override
	public void deleteFault(long taskId, String userId) {
		executor.execute(new DeleteFaultCommand(taskId, userId));
	}

	@Override
	public void deleteOutput(long taskId, String userId) {
		executor.execute(new DeleteOutputCommand(taskId, userId));
	}

	@Override
	public void deployTaskDef(TaskDef def) {
		this.executor.execute(new DeployTaskDefCommand(def));
	}

	@Override
	public List<TaskSummary> getActiveTasks() {
		return executor.execute(new GetActiveTasksCommand());
	}

	@Override
	public List<TaskSummary> getActiveTasks(Date since) {
		return executor.execute(new GetActiveTasksCommand(since));
	}

	@Override
	public List<TaskDef> getAllTaskDef(String filter) {
		return executor.execute(new GetAllTaskDefinitionsCommand(filter));
	}

	@Override
	public List<TaskSummary> getArchivedTasks() {
		return executor.execute(new GetArchivedTasksCommand());
	}

	@Override
	public List<TaskSummary> getCompletedTasks() {
		return executor.execute(new GetCompletedTasksCommand());
	}

	@Override
	public List<TaskSummary> getCompletedTasks(Date since) {
		return executor.execute(new GetCompletedTasksCommand(since));
	}

	@Override
	public List<TaskSummary> getCompletedTasksByProcessId(Long processId) {
		return executor.execute(new GetCompletedTasksCommand(processId));
	}

	@Override
	public Group getGroupById(String groupId) {
		return executor.execute(new GetGroupCommand(groupId));
	}

	@Override
	public List<Group> getGroups() {
		return executor.execute(new GetGroupsCommand());
	}

	@Override
	public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId) {
		return executor.execute(new GetSubTasksCommand(parentId, userId));
	}

	@Override
	public List<TaskSummary> getSubTasksByParent(long parentId) {
		return executor.execute(new GetSubTasksCommand(parentId));
	}

	@Override
	public int getPendingSubTasksByParent(long parentId) {
		return executor.execute(new GetPendingSubTasksCommand(parentId));
	}

	@Override
	public TaskDef getTaskDefById(String id) {
		return executor.execute(new GetTaskDefinitionCommand(id));
	}

	@Override
	public List<TaskSummary> getTasksOwnedByExpirationDate(String userId,
			List<Status> statuses, Date expirationDate) {

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("expirationDate", expirationDate);
                return getTasksOwned(userId, statuses, new QueryFilter("t.taskData.expirationTime = :expirationDate", params, "t.id", false));
	}

	@Override
	public List<TaskSummary> getTasksOwnedByExpirationDateOptional(
			String userId, List<Status> statuses, Date expirationDate) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("expirationDate", expirationDate);
                return getTasksOwned(userId, statuses, new QueryFilter("(t.taskData.expirationTime = :expirationDate or t.taskData.expirationTime is null)", params, "t.id", false));
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId) {
		return executor.execute(new GetTaskAssignedAsExcludedOwnerCommand(userId));
	}


	@Override
	public List<TaskSummary> getTasksAssignedAsRecipient(String userId) {
		return executor.execute(new GetTaskAssignedAsRecipientCommand(userId));
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId) {
		return executor.execute(new GetTaskAssignedAsInitiatorCommand(userId));
	}

	@Override
	public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId) {
		return executor.execute(new GetTaskAssignedAsStakeholderCommand(userId));
	}

	@Override
	public List<TaskSummary> getTasksOwnedByExpirationDateBeforeSpecifiedDate(
			String userId, List<Status> status, Date date) {
		return executor.execute(new GetTaskOwnedByExpDateBeforeDateCommand(userId, status, date));
	}

	@Override
	public List<TaskSummary> getTasksByStatusByProcessInstanceIdByTaskName(
			long processInstanceId, List<Status> status, String taskName) {
		return executor.execute(new GetTasksByStatusByProcessInstanceIdCommand(processInstanceId, status, taskName));
	}

	@Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByProcessId( String userId, String processId ) {
	    return this.taskSummaryQuery(userId).intersect().potentialOwner(userId).processId(processId)
	            .build().getResultList();
    }

    @Override
	public Map<Long, List<OrganizationalEntity>> getPotentialOwnersForTaskIds(List<Long> taskIds) {
		return executor.execute(new GetPotentialOwnersForTaskCommand(taskIds));
	}

	@Override
	public User getUserById(String userId) {
		return executor.execute(new GetUserCommand(userId));
	}

	@Override
	public List<User> getUsers() {
		return executor.execute(new GetUsersCommand());
	}

	@Override
	public long addTask(Task task, ContentData data) {
		return executor.execute(new AddTaskCommand(task, data));
	}

	@Override
	public void remove(long taskId, String userId) {
		executor.execute(new RemoveTaskCommand(taskId, userId));
	}

	@Override
	public void removeGroup(String groupId) {
		executor.execute(new RemoveGroupCommand(groupId));
	}

	@Override
	public int removeTasks(List<TaskSummary> tasks) {
		return executor.execute(new RemoveTasksCommand(tasks));
	}

	@Override
	public void removeUser(String userId) {
		executor.execute(new RemoveUserCommand(userId));
	}

	@Override
	public void setFault(long taskId, String userId, FaultData fault) {
		executor.execute(new SetTaskPropertyCommand(taskId, userId, SetTaskPropertyCommand.FAULT_PROPERTY, fault));
	}

	@Override
	public void setOutput(long taskId, String userId, Object outputContentData) {
		executor.execute(new SetTaskPropertyCommand(taskId, userId, SetTaskPropertyCommand.OUTPUT_PROPERTY, outputContentData));
	}

	@Override
	public void setPriority(long taskId, int priority) {
		executor.execute(new SetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.PRIORITY_PROPERTY, priority));
	}

	@Override
	public void setTaskNames(long taskId, List<I18NText> taskNames) {
		executor.execute(new SetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.TASK_NAMES_PROPERTY, taskNames));
	}

	@Override
	public void undeployTaskDef(String id) {
		executor.execute(new UndeployTaskDefCommand(id));
	}


	@Override
	public UserInfo getUserInfo() {
		return executor.execute(new GetUserInfoCommand());
	}

	@Override
	public void setUserInfo(UserInfo userInfo) {
		throw new UnsupportedOperationException("Set UserInfo object on TaskService creation");
	}

	@Override
	public void addUsersAndGroups(Map<String, User> users, Map<String, Group> groups) {
		executor.execute(new AddUsersGroupsCommand(users, groups));
	}

	@Override
	public int removeAllTasks() {
		return executor.execute(new RemoveAllTasksCommand());
	}

	@Override
	public long addContent(long taskId, Content content) {
		return executor.execute(new AddContentCommand(taskId, content));
	}

	@Override
	public long addContent(long taskId, Map<String, Object> params) {
		return executor.execute(new AddContentCommand(taskId, params));
	}

    @Override
    public long setDocumentContentFromUser( long taskId, String userId, byte [] content ) {
        AddContentFromUserCommand cmd = new AddContentFromUserCommand(taskId, userId);
        cmd.setDocumentContentBytes(content);
		return executor.execute(cmd);
    }

    @Override
    public long addOutputContentFromUser( long taskId, String userId, Map<String, Object> params ) {
        AddContentFromUserCommand cmd = new AddContentFromUserCommand(taskId, userId);
        cmd.setOutputContentMap(params);
		return executor.execute(cmd);
    }

	@Override
	public void deleteContent(long taskId, long contentId) {
		executor.execute(new DeleteContentCommand(taskId, contentId));
	}

	@Override
	public List<Content> getAllContentByTaskId(long taskId) {
		return executor.execute(new GetAllContentCommand(taskId));
	}

    @Override
    public Content getContentByIdForUser( long contentId, String userId ) {
        GetContentByIdForUserCommand cmd = new GetContentByIdForUserCommand(contentId);
        cmd.setContentId(contentId);
        cmd.setUserId(userId);
        return executor.execute(cmd);
    }

    @Override
    public Map<String, Object> getOutputContentMapForUser( long taskId, String userId ) {
        return executor.execute(new GetContentMapForUserCommand(taskId, userId));
    }

	@Override
	public long addAttachment(long taskId, Attachment attachment, Content content) {
		return executor.execute(new AddAttachmentCommand(taskId, attachment, content));
	}

	@Override
	public void deleteAttachment(long taskId, long attachmentId) {
		executor.execute(new DeleteAttachmentCommand(taskId, attachmentId));
	}

	@Override
	public List<Attachment> getAllAttachmentsByTaskId(long taskId) {
		return executor.execute(new GetAllAttachmentsCommand(taskId));
	}


	@Override
	public OrganizationalEntity getOrganizationalEntityById(String entityId) {
		return executor.execute(new GetOrgEntityCommand(entityId));
	}

	@Override
	public void setExpirationDate(long taskId, Date date) {
		executor.execute(new SetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.EXPIRATION_DATE_PROPERTY, date));
	}

	@Override
	public void setDescriptions(long taskId, List<I18NText> descriptions) {
		executor.execute(new SetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.DESCRIPTION_PROPERTY, descriptions));
	}

	@Override
	public void setSkipable(long taskId, boolean skipable) {
		executor.execute(new SetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.SKIPPABLE_PROPERTY, skipable));
	}

	@Override
	public void setSubTaskStrategy(long taskId, SubTasksStrategy strategy) {
		executor.execute(new SetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.SUB_TASK_STRATEGY_PROPERTY, strategy));
	}

	@Override
	public int getPriority(long taskId) {
		return (Integer) executor.execute(new GetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.PRIORITY_PROPERTY));
	}

	@Override
	public Date getExpirationDate(long taskId) {
		return (Date) executor.execute(new GetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.EXPIRATION_DATE_PROPERTY));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<I18NText> getDescriptions(long taskId) {
		return (List<I18NText>) executor.execute(new GetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.DESCRIPTION_PROPERTY));
	}

	@Override
	public boolean isSkipable(long taskId) {
		return (Boolean) executor.execute(new GetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.SKIPPABLE_PROPERTY));
	}

	@Override
	public SubTasksStrategy getSubTaskStrategy(long taskId) {
		return (SubTasksStrategy) executor.execute(
				new GetTaskPropertyCommand(taskId, null, SetTaskPropertyCommand.SUB_TASK_STRATEGY_PROPERTY));
	}

	@Override
	public Task getTaskInstanceById(long taskId) {
		return executor.execute(new GetTaskCommand(taskId));
	}

	@Override
	public int getCompletedTaskByUserId(String userId) {
		return executor.execute(new GetCompletedTasksByUserCommand(userId));
	}

	@Override
	public int getPendingTaskByUserId(String userId) {
		return executor.execute(new GetPendingTasksByUserCommand(userId));
	}

	@Override
	public List<TaskSummary> getTasksAssignedByGroup(String groupId) {
		return executor.execute(new GetTaskAssignedByGroupsCommand(Collections.singletonList(groupId)));
	}

	@Override
	public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds) {
		return executor.execute(new GetTaskAssignedByGroupsCommand(groupIds));
	}

	@Override
	public Long addComment(long taskId, Comment comment) {
		return executor.execute(new AddCommentCommand(taskId, comment));
	}

	@Override
    public Long addComment( long taskId, String addedByUserId, String commentText ) {
		return executor.execute(new AddCommentCommand(taskId, addedByUserId, commentText));
    }

    @Override
	public void deleteComment(long taskId, long commentId) {
		executor.execute(new DeleteCommentCommand(taskId, commentId));
	}

	@Override
	public List<Comment> getAllCommentsByTaskId(long taskId) {
		return executor.execute(new GetAllCommentsCommand(taskId));
	}

	@Override
	public Comment getCommentById(long commentId) {
		return executor.execute(new GetCommentCommand(commentId));
	}

	@Override
	public Map<String, Object> getTaskContent(long taskId) {
		return executor.execute(new GetTaskContentCommand(taskId));
	}

	// marshaller context methods

	@Override
	public void addMarshallerContext(String ownerId, ContentMarshallerContext context) {
		TaskContentRegistry.get().addMarshallerContext(ownerId, context);
	}

	@Override
	public void removeMarshallerContext(String ownerId) {
		TaskContentRegistry.get().removeMarshallerContext(ownerId);
	}

	@Override
	public ContentMarshallerContext getMarshallerContext(Task task) {
		return TaskContentRegistry.get().getMarshallerContext(task);
	}

	@Override
	public void removeTaskEventsById(long taskId) {
		throw new UnsupportedOperationException(Thread.currentThread().getStackTrace()[1].getMethodName() + " is not supported on the " +
		        this.getClass().getSimpleName());
	}

	@Override
	public List<TaskEvent> getTaskEventsById(long taskId) {
		throw new UnsupportedOperationException(Thread.currentThread().getStackTrace()[1].getMethodName() + " is not supported on the " +
		        this.getClass().getSimpleName());
	}

	// notification service methods

	@Override
	public void registerTaskEventListener(TaskLifeCycleEventListener listener) {
		taskEventSupport.addEventListener(listener);
	}

	@Override
	public List<TaskLifeCycleEventListener> getTaskEventListeners() {
		return taskEventSupport.getEventListeners();
	}

	@Override
	public void clearTaskEventListeners() {
		taskEventSupport.clear();
	}

	@Override
	public void removeTaskEventListener(TaskLifeCycleEventListener listener) {
		taskEventSupport.removeEventListener(listener);
	}

	public void executeReminderForTask(long taskId,String fromUser){
		executor.execute(new ExecuteReminderCommand(taskId,fromUser));
	}

    public Environment getEnvironment() {
        return environment;
    }

}

