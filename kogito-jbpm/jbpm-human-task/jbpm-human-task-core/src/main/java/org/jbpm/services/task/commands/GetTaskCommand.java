package org.jbpm.services.task.commands;

import java.util.List;

import javax.enterprise.util.AnnotationLiteral;

import org.jbpm.services.task.events.AfterTaskClaimedEvent;
import org.jbpm.services.task.events.BeforeTaskClaimedEvent;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.InternalTaskData;

public class GetTaskCommand extends TaskCommand<Task> {

	public GetTaskCommand() {
	}
	
	public GetTaskCommand(long taskId) {
		this.taskId = taskId;
    }

    public Task execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	return context.getTaskService().getTaskById(taskId);
        }
        return context.getTaskQueryService().getTaskInstanceById(taskId);
    }

}
