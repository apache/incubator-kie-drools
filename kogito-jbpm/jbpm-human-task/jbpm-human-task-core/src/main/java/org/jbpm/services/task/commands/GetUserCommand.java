package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.User;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-user-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetUserCommand extends TaskCommand<User> {

	private static final long serialVersionUID = -836520791223188840L;

	public GetUserCommand() {
	}
	
	public GetUserCommand(String userId) {
		this.userId = userId;
    }

    public User execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        return context.getTaskIdentityService().getUserById(userId);
    }

}
