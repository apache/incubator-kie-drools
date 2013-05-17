package org.jbpm.services.task.commands;

import java.util.List;

import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

public class GetTaskAssignedAsPotentialOwnerCommand extends TaskCommand<List<TaskSummary>> {

	private String language;
	private List<Status> status;
	
	public GetTaskAssignedAsPotentialOwnerCommand() {
	}
	
	public GetTaskAssignedAsPotentialOwnerCommand(String userId, String language) {
		this.userId = userId;
		this.language = language;
    }

	public GetTaskAssignedAsPotentialOwnerCommand(String userId, String language, List<Status> status) {
		this.userId = userId;
		this.language = language;
		this.status = status;
    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	if (status == null) {
        		return context.getTaskService().getTasksAssignedAsPotentialOwner(userId, language);
        	} else {
        		return context.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(userId, status, language);
        	}
        }
        if (status == null) {
        	return context.getTaskQueryService().getTasksAssignedAsPotentialOwner(userId, language);
        } else {
        	return context.getTaskQueryService().getTasksAssignedAsPotentialOwnerByStatus(userId, status, language);
        }
    }

}
