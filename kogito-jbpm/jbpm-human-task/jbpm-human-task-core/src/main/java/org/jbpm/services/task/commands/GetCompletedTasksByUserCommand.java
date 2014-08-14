package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.internal.command.Context;

@XmlRootElement(name="get-completed-tasks-by-user-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetCompletedTasksByUserCommand extends TaskCommand<Integer> {

	private static final long serialVersionUID = -836520791223188840L;

	public GetCompletedTasksByUserCommand() {
	}
	
	public GetCompletedTasksByUserCommand(String userId) {
		this.userId = userId;
    }

    public Integer execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        return context.getTaskQueryService().getCompletedTaskByUserId(userId);
    }

}
