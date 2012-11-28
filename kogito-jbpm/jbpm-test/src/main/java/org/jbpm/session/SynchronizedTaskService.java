package org.jbpm.session;

import java.util.List;

import org.drools.persistence.SingleSessionCommandService;
import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.task.Attachment;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.FaultData;

public class SynchronizedTaskService implements TaskService {
	
	// TODO: when engine controls transaction boundaries, this is probably sufficient
	// however, with user transactions, we should make sure we sync on the ksession
	// until the transaction is committed
	
	private SingleSessionCommandService ksession;
	private TaskService taskService;
	
	public SynchronizedTaskService(SingleSessionCommandService ksession, TaskService taskService) {
		this.ksession = ksession;
		this.taskService = taskService;
	}

	public void activate(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.activate(arg0, arg1);
		}
	}

	public void addAttachment(long arg0, Attachment arg1, Content arg2) {
		synchronized (ksession) {
			taskService.addAttachment(arg0, arg1, arg2);
		}
	}
	
	public void addComment(long arg0, Comment arg1) {
		synchronized (ksession) {
			taskService.addComment(arg0, arg1);
		}
	}
	
	public void addTask(Task arg0, ContentData arg1) {
		synchronized (ksession) {
			taskService.addTask(arg0, arg1);
		}
	}
	
	public void claim(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.claim(arg0, arg1);
		}
	}
	
	public void claim(long arg0, String arg1, List<String> arg2) {
		synchronized (ksession) {
			taskService.claim(arg0, arg1, arg2);
		}
	}
	
	public void claimNextAvailable(String arg0, String arg1) {
		synchronized (ksession) {
			taskService.claimNextAvailable(arg0, arg1);
		}
	}
	
	public void claimNextAvailable(String arg0, List<String> arg1, String arg2) {
		synchronized (ksession) {
			taskService.claimNextAvailable(arg0, arg1, arg2);
		}
	}
	
	public void complete(long arg0, String arg1, ContentData arg2) {
		synchronized (ksession) {
			taskService.complete(arg0, arg1, arg2);
		}
	}
	
	public void completeWithResults(long arg0, String arg1, Object arg2) {
		synchronized (ksession) {
			taskService.completeWithResults(arg0, arg1, arg2);
		}
	}

	public boolean connect() {
		synchronized (ksession) {
			return taskService.connect();
		}
	}
	
	public boolean connect(String arg0, int arg1) {
		synchronized (ksession) {
			return taskService.connect(arg0, arg1);
		}
	}
	
	public void delegate(long arg0, String arg1, String arg2) {
		synchronized (ksession) {
			taskService.delegate(arg0, arg1, arg2);
		}
	}
	
	public void deleteAttachment(long arg0, long arg1, long arg2) {
		synchronized (ksession) {
			taskService.deleteAttachment(arg0, arg1, arg2);
		}
	}
	
	public void deleteComment(long arg0, long arg1) {
		synchronized (ksession) {
			taskService.deleteComment(arg0, arg1);
		}
	}
	
	public void deleteFault(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.deleteFault(arg0, arg1);
		}
	}
	
	public void deleteOutput(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.deleteOutput(arg0, arg1);
		}
	}
	
	public void disconnect() throws Exception {
		synchronized (ksession) {
			taskService.disconnect();
		}
	}
	
	public void exit(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.exit(arg0, arg1);
		}
	}
	
	public void fail(long arg0, String arg1, FaultData arg2) {
		synchronized (ksession) {
			taskService.fail(arg0, arg1, arg2);
		}
	}
	
	public void forward(long arg0, String arg1, String arg2) {
		synchronized (ksession) {
			taskService.forward(arg0, arg1, arg2);
		}
	}
	
	public Content getContent(long arg0) {
		synchronized (ksession) {
			return taskService.getContent(arg0);
		}
	}
	
	public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long arg0,
			String arg1, String arg2) {
		synchronized (ksession) {
			return taskService.getSubTasksAssignedAsPotentialOwner(arg0, arg1, arg2);
		}
	}
	
	public List<TaskSummary> getSubTasksByParent(long arg0) {
		synchronized (ksession) {
			return taskService.getSubTasksByParent(arg0);
		}
	}
	
	public Task getTask(long arg0) {
		synchronized (ksession) {
			return taskService.getTask(arg0);
		}
	}
	
	public Task getTaskByWorkItemId(long arg0) {
		synchronized (ksession) {
			return taskService.getTaskByWorkItemId(arg0);
		}
	}
	
	public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(
			String arg0, String arg1) {
		synchronized (ksession) {
			return taskService.getTasksAssignedAsBusinessAdministrator(arg0, arg1);
		}
	}
	
	public List<TaskSummary> getTasksAssignedAsExcludedOwner(String arg0,
			String arg1) {
		synchronized (ksession) {
			return taskService.getTasksAssignedAsExcludedOwner(arg0, arg1);
		}
	}
	
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String arg0,
			String arg1) {
		synchronized (ksession) {
			return taskService.getTasksAssignedAsPotentialOwner(arg0, arg1);
		}
	}
	
	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String arg0,
			List<String> arg1, String arg2) {
		synchronized (ksession) {
			return taskService.getTasksAssignedAsPotentialOwner(arg0, arg1, arg2);
		}
	}

	public List<TaskSummary> getTasksAssignedAsPotentialOwner(String arg0,
			List<String> arg1, String arg2, int arg3, int arg4) {
		synchronized (ksession) {
			return taskService.getTasksAssignedAsPotentialOwner(arg0, arg1, arg2, arg3, arg4);
		}
	}
	
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatus(
			String arg0, List<Status> arg1, String arg2) {
		synchronized (ksession) {
			return taskService.getTasksAssignedAsPotentialOwnerByStatus(arg0, arg1, arg2);
		}
	}
	
	public List<TaskSummary> getTasksAssignedAsPotentialOwnerByStatusByGroup(
			String arg0, List<String> arg1, List<Status> arg2, String arg3) {
		synchronized (ksession) {
			return taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup(arg0, arg1, arg2, arg3);
		}
	}
	
	public List<TaskSummary> getTasksAssignedAsRecipient(String arg0,
			String arg1) {
		synchronized (ksession) {
			return taskService.getTasksAssignedAsRecipient(arg0, arg1);
		}
	}
	
	public List<TaskSummary> getTasksAssignedAsTaskInitiator(String arg0,
			String arg1) {
		synchronized (ksession) {
			return taskService.getTasksAssignedAsTaskInitiator(arg0, arg1);
		}
	}
	
	public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String arg0,
			String arg1) {
		synchronized (ksession) {
			return taskService.getTasksAssignedAsTaskStakeholder(arg0, arg1);
		}
	}
	
	public List<TaskSummary> getTasksOwned(String arg0, String arg1) {
		synchronized (ksession) {
			return taskService.getTasksOwned(arg0, arg1);
		}
	}
	
	public List<TaskSummary> getTasksOwned(String arg0, List<Status> arg1,
			String arg2) {
		synchronized (ksession) {
			return taskService.getTasksOwned(arg0, arg1, arg2);
		}
	}
	
	public void nominate(long arg0, String arg1, List<OrganizationalEntity> arg2) {
		synchronized (ksession) {
			taskService.nominate(arg0, arg1, arg2);
		}
	}
	
	public List<?> query(String arg0, Integer arg1, Integer arg2) {
		synchronized (ksession) {
			return taskService.query(arg0, arg1, arg2);
		}
	}
	
	public void register(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.register(arg0, arg1);
		}
	}

	public void registerForEvent(EventKey arg0, boolean arg1,
			EventResponseHandler arg2) {
		synchronized (ksession) {
			taskService.registerForEvent(arg0, arg1, arg2);
		}
	}
	
	public void release(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.release(arg0, arg1);
		}
	}
	
	public void remove(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.remove(arg0, arg1);
		}
	}
	
	public void resume(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.resume(arg0, arg1);
		}
	}
	
	public void setDocumentContent(long arg0, Content arg1) {
		synchronized (ksession) {
			taskService.setDocumentContent(arg0, arg1);
		}
	}
	
	public void setFault(long arg0, String arg1, FaultData arg2) {
		synchronized (ksession) {
			taskService.setFault(arg0, arg1, arg2);
		}
	}
	
	public void setOutput(long arg0, String arg1, ContentData arg2) {
		synchronized (ksession) {
			taskService.setOutput(arg0, arg1, arg2);
		}
	}
	
	public void setPriority(long arg0, String arg1, int arg2) {
		synchronized (ksession) {
			taskService.setPriority(arg0, arg1, arg2);
		}
	}
	
	public void skip(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.skip(arg0, arg1);
		}
	}
	
	public void start(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.start(arg0, arg1);
		}
	}
	
	public void stop(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.stop(arg0, arg1);
		}
	}
	
	public void suspend(long arg0, String arg1) {
		synchronized (ksession) {
			taskService.suspend(arg0, arg1);
		}
	}
	
	public void unregisterForEvent(EventKey arg0) {
		synchronized (ksession) {
			taskService.unregisterForEvent(arg0);
		}
	}

	public List<TaskSummary> getTasksByStatusByProcessId(
			long processInstanceId, List<Status> status, String language) {
		synchronized (ksession) {
			return taskService.getTasksByStatusByProcessId(
				processInstanceId, status, language);
		}
	}

	public List<TaskSummary> getTasksByStatusByProcessIdByTaskName(
			long processInstanceId, List<Status> status, String taskName,
			String language) {
		synchronized (ksession) {
			return getTasksByStatusByProcessIdByTaskName(
				processInstanceId, status, taskName, language);
		}
	}

}
