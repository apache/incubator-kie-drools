package org.jbpm.services.task.impl.command;

import java.util.List;
import java.util.Map;

import org.jbpm.services.task.commands.*;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

public class CommandBasedTaskService implements TaskService {

	private CommandExecutor executor;
	
	public CommandBasedTaskService(CommandExecutor executor) {
		this.executor = executor;
	}
	
	public void activate(long taskId, String userId) {
		executor.execute(new ActivateTaskCommand(taskId, userId));
	}

	public void claim(long taskId, String userId) {
		executor.execute(new ClaimTaskCommand(taskId, userId));
	}

	public void claimNextAvailable(String userId, String language) {
		executor.execute(new ClaimNextAvailableTaskCommand(userId, language));
	}

	public void complete(long taskId, String userId, Map<String, Object> data) {
		executor.execute(new CompleteTaskCommand(taskId, userId, data));
	}

	public void delegate(long taskId, String userId, String targetUserId) {
		executor.execute(new DelegateTaskCommand(taskId, userId, targetUserId));
	}

	public void exit(long taskId, String userId) {
		executor.execute(new ExitTaskCommand(taskId, userId));
	}

	public void fail(long taskId, String userId, Map<String, Object> faultData) {
		executor.execute(new FailTaskCommand(taskId, userId, faultData));
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

	public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(
			String userId, String language) {
		return executor.execute(new GetTaskAssignedAsBusinessAdminCommand(userId, language));
	}

	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
		return executor.execute(new GetTaskAssignedAsPotentialOwnerCommand(userId, language));
	}

	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(
			String userId, List<Status> status, String language) {
		return executor.execute(new GetTaskAssignedAsPotentialOwnerCommand(userId, language, status));
	}

	public List<TaskSummary> getTasksOwned(String userId, String language) {
		return executor.execute(new GetTasksOwnedCommand(userId, language));
	}

	public List<TaskSummary> getTasksOwnedByStatus(String userId,
			List<Status> status, String language) {
		return executor.execute(new GetTasksOwnedCommand(userId, language, status));
	}

	public List<TaskSummary> getTasksByStatusByProcessInstanceId(
			long processInstanceId, List<Status> status, String language) {
		return executor.execute(new GetTasksByStatusByProcessInstanceIdCommand(processInstanceId, language, status));
	}

	public List<Long> getTasksByProcessInstanceId(long processInstanceId) {
		return executor.execute(new GetTasksByProcessInstanceIdCommand(processInstanceId));
	}
	
    @Override
    public List<TaskSummary> getTasksByVariousFields(List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds,
            List<String> busAdmins, List<String> potOwners, List<String> taskOwners, List<Status> statuses, 
            List<String> language, boolean union) {
		return executor.execute(new GetTasksByVariousFieldsCommand(workItemIds, taskIds, procInstIds, 
		        busAdmins, potOwners, taskOwners, 
		        statuses, language, union));
    }

    @Override
    public List<TaskSummary> getTasksByVariousFields(Map<String, List<?>> parameters, boolean union) {
		return executor.execute(new GetTasksByVariousFieldsCommand(parameters, union));
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
		executor.execute(new SkipTaskCommand(taskId, userId));
	}

	public void start(long taskId, String userId) {
		executor.execute(new StartTaskCommand(taskId, userId));
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
		return executor.execute(new GetContentCommand(contentId));
	}

	public Attachment getAttachmentById(long attachId) {
		return executor.execute(new GetAttachmentCommand(attachId));
	}



}
