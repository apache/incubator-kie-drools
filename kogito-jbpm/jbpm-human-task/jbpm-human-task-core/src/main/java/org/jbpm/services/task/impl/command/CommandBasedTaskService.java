package org.jbpm.services.task.impl.command;

import java.util.List;
import java.util.Map;

import org.jbpm.services.task.commands.ActivateTaskCommand;
import org.jbpm.services.task.commands.ClaimNextAvailableTaskCommand;
import org.jbpm.services.task.commands.ClaimTaskCommand;
import org.jbpm.services.task.commands.CompleteTaskCommand;
import org.jbpm.services.task.commands.DelegateTaskCommand;
import org.jbpm.services.task.commands.ExitTaskCommand;
import org.jbpm.services.task.commands.FailTaskCommand;
import org.jbpm.services.task.commands.ForwardTaskCommand;
import org.jbpm.services.task.commands.NominateTaskCommand;
import org.jbpm.services.task.commands.ReleaseTaskCommand;
import org.jbpm.services.task.commands.ResumeTaskCommand;
import org.jbpm.services.task.commands.SkipTaskCommand;
import org.jbpm.services.task.commands.StartTaskCommand;
import org.jbpm.services.task.commands.StopTaskCommand;
import org.jbpm.services.task.commands.SuspendTaskCommand;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.TaskCommandExecutor;

public class CommandBasedTaskService implements TaskService {

	private TaskCommandExecutor executor;
	
	public CommandBasedTaskService(TaskCommandExecutor executor) {
		this.executor = executor;
	}
	
	public void activate(long taskId, String userId) {
		executor.executeTaskCommand(new ActivateTaskCommand(taskId, userId));
	}

	public void claim(long taskId, String userId) {
		executor.executeTaskCommand(new ClaimTaskCommand(taskId, userId));
	}

	public void claimNextAvailable(String userId, String language) {
		executor.executeTaskCommand(new ClaimNextAvailableTaskCommand(userId, language));
	}

	public void complete(long taskId, String userId, Map<String, Object> data) {
		executor.executeTaskCommand(new CompleteTaskCommand(taskId, userId, data));
	}

	public void delegate(long taskId, String userId, String targetUserId) {
		executor.executeTaskCommand(new DelegateTaskCommand(taskId, userId, targetUserId));
	}

	public void exit(long taskId, String userId) {
		executor.executeTaskCommand(new ExitTaskCommand(taskId, userId));
	}

	public void fail(long taskId, String userId, Map<String, Object> faultData) {
		executor.executeTaskCommand(new FailTaskCommand(taskId, userId, faultData));
	}

	public void forward(long taskId, String userId, String targetEntityId) {
		executor.executeTaskCommand(new ForwardTaskCommand(taskId, userId, targetEntityId));
	}

	public Task getTaskByWorkItemId(long workItemId) {
		throw new UnsupportedOperationException();
	}

	public Task getTaskById(long taskId) {
		throw new UnsupportedOperationException();
	}

	public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(
			String userId, String language) {
		throw new UnsupportedOperationException();
	}

	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId,
			String language) {
		throw new UnsupportedOperationException();
	}

	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(
			String userId, List<Status> status, String language) {
		throw new UnsupportedOperationException();
	}

	public List<TaskSummary> getTasksOwned(String userId, String language) {
		throw new UnsupportedOperationException();
	}

	public List<TaskSummary> getTasksOwnedByStatus(String userId,
			List<Status> status, String language) {
		throw new UnsupportedOperationException();
	}

	public List<TaskSummary> getTasksByStatusByProcessInstanceId(
			long processInstanceId, List<Status> status, String language) {
		throw new UnsupportedOperationException();
	}

	public List<Long> getTasksByProcessInstanceId(long processInstanceId) {
		throw new UnsupportedOperationException();
	}

	public long addTask(Task task, Map<String, Object> params) {
		throw new UnsupportedOperationException();
	}

	public void release(long taskId, String userId) {
		executor.executeTaskCommand(new ReleaseTaskCommand(taskId, userId));
	}

	public void resume(long taskId, String userId) {
		executor.executeTaskCommand(new ResumeTaskCommand(taskId, userId));		
	}

	public void skip(long taskId, String userId) {
		executor.executeTaskCommand(new SkipTaskCommand(taskId, userId));
	}

	public void start(long taskId, String userId) {
		executor.executeTaskCommand(new StartTaskCommand(taskId, userId));
	}

	public void stop(long taskId, String userId) {
		executor.executeTaskCommand(new StopTaskCommand(taskId, userId));
	}

	public void suspend(long taskId, String userId) {
		executor.executeTaskCommand(new SuspendTaskCommand(taskId, userId));
	}

	public void nominate(long taskId, String userId,
			List<OrganizationalEntity> potentialOwners) {
		executor.executeTaskCommand(new NominateTaskCommand(taskId, userId, potentialOwners));
	}

	public Content getContentById(long contentId) {
		throw new UnsupportedOperationException();
	}

	public Attachment getAttachmentById(long attachId) {
		throw new UnsupportedOperationException();
	}

}
