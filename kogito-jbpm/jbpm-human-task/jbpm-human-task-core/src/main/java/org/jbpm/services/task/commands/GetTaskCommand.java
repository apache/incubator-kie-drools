package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.Task;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskCommand extends TaskCommand<Task> {

	private static final long serialVersionUID = -836520791223188840L;

	public GetTaskCommand() {
	}
	
	public GetTaskCommand(long taskId) {
		this.taskId = taskId;
    }

    public Task execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        return context.getTaskQueryService().getTaskInstanceById(taskId);
    }

}
