package org.jbpm.services.task.commands;

import org.kie.api.task.model.Task;
import org.kie.internal.command.Context;

public class GetTaskByWorkItemIdCommand extends TaskCommand<Task> {

	private long workItemId;
	
	public GetTaskByWorkItemIdCommand() {
	}
	
	public GetTaskByWorkItemIdCommand(long workItemId) {
		this.workItemId = workItemId;
    }
	
    public long getWorkItemId() {
		return workItemId;
	}

	public void setWorkItemId(long workItemId) {
		this.workItemId = workItemId;
	}

	public Task execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	return context.getTaskService().getTaskByWorkItemId(workItemId);
        }
        return context.getTaskQueryService().getTaskByWorkItemId(workItemId);
    }

}
