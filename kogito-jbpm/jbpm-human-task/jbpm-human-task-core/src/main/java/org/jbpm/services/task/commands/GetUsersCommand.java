package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.User;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-users-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetUsersCommand extends TaskCommand<List<User>> {

	private static final long serialVersionUID = -836520791223188840L;

	public GetUsersCommand() {
	}

    public List<User> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        return context.getTaskIdentityService().getUsers();
    }

}
