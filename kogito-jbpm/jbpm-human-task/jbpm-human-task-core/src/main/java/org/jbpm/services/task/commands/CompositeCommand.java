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

package org.jbpm.services.task.commands;

import org.kie.api.runtime.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.List;

@XmlRootElement(name="composite-command")
@XmlAccessorType(XmlAccessType.FIELD)
public class CompositeCommand<T> extends TaskCommand<T> {
	
	private static final long serialVersionUID = -5591247478243819049L;

	@XmlElements(value={
	            @XmlElement(name="activate-task", type=ActivateTaskCommand.class),
	            @XmlElement(name="add-attachment", type=AddAttachmentCommand.class),
	            @XmlElement(name="add-comment", type=AddCommentCommand.class),
	            @XmlElement(name="add-content", type=AddContentCommand.class),
	            @XmlElement(name="add-content-from-user", type=AddContentFromUserCommand.class),
	            @XmlElement(name="add-group", type=AddGroupCommand.class),
	            @XmlElement(name="add-task", type=AddTaskCommand.class),
	            @XmlElement(name="add-user", type=AddUserCommand.class),
	            @XmlElement(name="add-users-groups", type=AddUsersGroupsCommand.class),
	            @XmlElement(name="archive-tasks", type=ArchiveTasksCommand.class),
	            @XmlElement(name="cancel-deadline", type=CancelDeadlineCommand.class),
	            @XmlElement(name="claim-next-available-task", type=ClaimNextAvailableTaskCommand.class),
	            @XmlElement(name="claim-task", type=ClaimTaskCommand.class),
	            @XmlElement(name="complete-task", type=CompleteTaskCommand.class),
	            @XmlElement(name="delegate-task", type=DelegateTaskCommand.class),
	            @XmlElement(name="delete-attachment", type=DeleteAttachmentCommand.class),
	            @XmlElement(name="delete-comment", type=DeleteCommentCommand.class),
	            @XmlElement(name="delete-content", type=DeleteContentCommand.class),
	            @XmlElement(name="delete-fault", type=DeleteFaultCommand.class),
	            @XmlElement(name="delete-output", type=DeleteOutputCommand.class),
	            @XmlElement(name="deploy-task-def", type=DeployTaskDefCommand.class),
	            @XmlElement(name="execute-deadlines", type=ExecuteDeadlinesCommand.class),
	            @XmlElement(name="execute-task-rules", type=ExecuteTaskRulesCommand.class),
	            @XmlElement(name="exit-task", type=ExitTaskCommand.class),
	            @XmlElement(name="fail-task", type=FailTaskCommand.class),
	            @XmlElement(name="forward-task", type=ForwardTaskCommand.class),
	            @XmlElement(name="get-active-tasks", type=GetActiveTasksCommand.class),
	            @XmlElement(name="get-all-attachments", type=GetAllAttachmentsCommand.class),
	            @XmlElement(name="get-all-comments", type=GetAllCommentsCommand.class),
	            @XmlElement(name="get-all-content", type=GetAllContentCommand.class),
	            @XmlElement(name="get-all-task-definitions", type=GetAllTaskDefinitionsCommand.class),
	            @XmlElement(name="get-archived-tasks", type=GetArchivedTasksCommand.class),
	            @XmlElement(name="get-attachment", type=GetAttachmentCommand.class),
	            @XmlElement(name="get-comment", type=GetCommentCommand.class),
	            @XmlElement(name="get-completed-tasks-by-user", type=GetCompletedTasksByUserCommand.class),
	            @XmlElement(name="get-completed-tasks", type=GetCompletedTasksCommand.class),
	            @XmlElement(name="get-content", type=GetContentByIdCommand.class),
	            @XmlElement(name="get-content-by-id-for-user", type=GetContentByIdForUserCommand.class),
	            @XmlElement(name="get-content-map-for-user", type=GetContentMapForUserCommand.class),
	            @XmlElement(name="get-group", type=GetGroupCommand.class),
	            @XmlElement(name="get-groups", type=GetGroupsCommand.class),
	            @XmlElement(name="get-org-entity", type=GetOrgEntityCommand.class),
	            @XmlElement(name="get-pending-sub-tasks", type=GetPendingSubTasksCommand.class),
	            @XmlElement(name="get-pending-tasks-by-user", type=GetPendingTasksByUserCommand.class),
	            @XmlElement(name="get-potential-ownders-for-task", type=GetPotentialOwnersForTaskCommand.class),
	            @XmlElement(name="get-sub-tasks", type=GetSubTasksCommand.class),
	            @XmlElement(name="get-task-assigned-as-business-admin", type=GetTaskAssignedAsBusinessAdminCommand.class),
	            @XmlElement(name="get-task-assigned-as-excluded-owner", type=GetTaskAssignedAsExcludedOwnerCommand.class),
	            @XmlElement(name="get-task-assigned-as-initiator", type=GetTaskAssignedAsInitiatorCommand.class),
	            @XmlElement(name="get-task-assigned-as-potential-owner-by-exp-date", type=GetTaskAssignedAsPotentialOwnerByExpDateCommand.class),
	            @XmlElement(name="get-task-assigned-as-potential-owner", type=GetTaskAssignedAsPotentialOwnerCommand.class),
	            @XmlElement(name="get-task-assigned-as-potential-owner-paging", type=GetTaskAssignedAsPotentialOwnerPagingCommand.class),
	            @XmlElement(name="get-task-assigned-as-recipient", type=GetTaskAssignedAsRecipientCommand.class),
	            @XmlElement(name="get-task-assigned-as-stakeholeder", type=GetTaskAssignedAsStakeholderCommand.class),
	            @XmlElement(name="get-task-assigned-by-groups", type=GetTaskAssignedByGroupsCommand.class),
	            @XmlElement(name="get-task-by-work-item-id", type=GetTaskByWorkItemIdCommand.class),
	            @XmlElement(name="get-task", type=GetTaskCommand.class),
	            @XmlElement(name="get-task-content", type=GetTaskContentCommand.class),
				@XmlElement(name="get-user-task-command", type=GetUserTaskCommand.class),
	            @XmlElement(name="get-task-definition", type=GetTaskDefinitionCommand.class),
	            @XmlElement(name="get-task-owned-by-exp-date-before-date", type=GetTaskOwnedByExpDateBeforeDateCommand.class),
	            @XmlElement(name="get-task-owned-by-exp-date", type=GetTaskOwnedByExpDateCommand.class),
	            @XmlElement(name="get-task-property", type=GetTaskPropertyCommand.class),
	            @XmlElement(name="get-tasks-by-process-instance-id", type=GetTasksByProcessInstanceIdCommand.class),
	            @XmlElement(name="get-tasks-by-status-by-process-instance-id", type=GetTasksByStatusByProcessInstanceIdCommand.class),
	            @XmlElement(name="get-tasks-for-process", type=GetTasksForProcessCommand.class),
	            @XmlElement(name="get-tasks-owned", type=GetTasksOwnedCommand.class),
	            @XmlElement(name="get-user", type=GetUserCommand.class),
	            @XmlElement(name="get-user-info", type=GetUserInfoCommand.class),
	            @XmlElement(name="get-user", type=GetUsersCommand.class),
	            @XmlElement(name="init-deadlines", type=InitDeadlinesCommand.class),
	            @XmlElement(name="nominate-task", type=NominateTaskCommand.class),
	            @XmlElement(name="process-sub-task", type=ProcessSubTaskCommand.class),
	            @XmlElement(name="release-task", type=ReleaseTaskCommand.class),
	            @XmlElement(name="remove-all-tasks", type=RemoveAllTasksCommand.class),
	            @XmlElement(name="remove-group", type=RemoveGroupCommand.class),
	            @XmlElement(name="remove-task", type=RemoveTaskCommand.class),
	            @XmlElement(name="remove-tasks", type=RemoveTasksCommand.class),
	            @XmlElement(name="remove-user", type=RemoveUserCommand.class),
	            @XmlElement(name="resume-task", type=ResumeTaskCommand.class),
	            @XmlElement(name="set-task-property", type=SetTaskPropertyCommand.class),
	            @XmlElement(name="skip-task", type=SkipTaskCommand.class),
	            @XmlElement(name="start-task", type=StartTaskCommand.class),
	            @XmlElement(name="stop-task", type=StopTaskCommand.class),
	            @XmlElement(name="suspend-task", type=SuspendTaskCommand.class),
	            @XmlElement(name="undeploy-task-def", type=UndeployTaskDefCommand.class),
	            @XmlElement(name="task-query", type=TaskSummaryQueryCommand.class),
	            @XmlElement(name="execute-reminder-command", type=ExecuteReminderCommand.class)
    } )
	private TaskCommand<T> mainCommand;

	@XmlElement
	private List<TaskCommand<?>> commands;
	
	public CompositeCommand() {
		
	}
	
	public CompositeCommand(TaskCommand<T> mainCommand, TaskCommand<?>...commands) {
		this.mainCommand = mainCommand;
		this.commands = Arrays.asList(commands);		
	}

	@Override
	public T execute(Context context) {
		if (commands != null) {
			for (TaskCommand<?> cmd : commands) {
				cmd.execute(context);
			}
		}
		return mainCommand.execute(context);
	}

	public TaskCommand<T> getMainCommand() {
		return mainCommand;
	}

	public void setMainCommand(TaskCommand<T> mainCommand) {
		this.mainCommand = mainCommand;
	}

	public List<TaskCommand<?>> getCommands() {
		return commands;
	}

	public void setCommands(List<TaskCommand<?>> commands) {
		this.commands = commands;
	}

	@Override
	public Long getTaskId() {
		if ( mainCommand != null) {
			return mainCommand.getTaskId();	
		}
		return this.taskId;
	}

}
