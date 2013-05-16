package org.jbpm.services.task.commands;

import java.util.List;

import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

public class GetTasksOwnedCommand extends TaskCommand<List<TaskSummary>> {

	private String language;
	private List<Status> status;
	
	public GetTasksOwnedCommand() {
	}
	
	public GetTasksOwnedCommand(String userId, String language) {
		this.userId = userId;
		this.language = language;
    }

	public GetTasksOwnedCommand(String userId, String language, List<Status> status) {
		this.userId = userId;
		this.language = language;
		this.status = status;
    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	if (status == null) {
        		return context.getTaskService().getTasksOwned(language, language);
        	} else {
        		return context.getTaskService().getTasksOwnedByStatus(userId, status, language);
        	}
        }
        if (status == null) {
        	return context.getTaskQueryService().getTasksOwned(userId, language);
        } else {
        	return context.getTaskQueryService().getTasksOwnedByStatus(userId, status, language);
        }
    }

}
