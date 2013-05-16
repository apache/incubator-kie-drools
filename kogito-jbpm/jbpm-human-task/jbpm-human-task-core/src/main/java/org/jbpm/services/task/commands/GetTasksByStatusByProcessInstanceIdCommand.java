package org.jbpm.services.task.commands;

import java.util.List;

import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

public class GetTasksByStatusByProcessInstanceIdCommand extends TaskCommand<List<TaskSummary>> {

	private long processInstanceId;
	private String language;
	private List<Status> status;
	
	public GetTasksByStatusByProcessInstanceIdCommand() {
	}
	
	public GetTasksByStatusByProcessInstanceIdCommand(long processInstanceId, String language, List<Status> status) {
		this.processInstanceId = processInstanceId;
		this.language = language;
		this.status = status;
    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
    		return context.getTaskService().getTasksByStatusByProcessInstanceId(processInstanceId, status, language);
        }
    	return context.getTaskQueryService().getTasksByStatusByProcessInstanceId(processInstanceId, status, language);
    }

}
