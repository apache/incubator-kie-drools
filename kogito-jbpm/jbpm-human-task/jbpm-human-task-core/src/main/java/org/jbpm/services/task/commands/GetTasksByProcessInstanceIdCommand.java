package org.jbpm.services.task.commands;

import java.util.List;

import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

public class GetTasksByProcessInstanceIdCommand extends TaskCommand<List<Long>> {

	private long processInstanceId;
	
	public GetTasksByProcessInstanceIdCommand() {
	}
	
	public GetTasksByProcessInstanceIdCommand(long processInstanceId) {
		this.processInstanceId = processInstanceId;
    }

    public long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public List<Long> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
    		return context.getTaskService().getTasksByProcessInstanceId(processInstanceId);
        }
    	return context.getTaskQueryService().getTasksByProcessInstanceId(processInstanceId);
    }

}
