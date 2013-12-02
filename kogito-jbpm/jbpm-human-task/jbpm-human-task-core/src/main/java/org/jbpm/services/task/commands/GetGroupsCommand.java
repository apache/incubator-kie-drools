package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.Group;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-groups-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetGroupsCommand extends TaskCommand<List<Group>> {

	private static final long serialVersionUID = -836520791223188840L;

	public GetGroupsCommand() {
	}

	public List<Group> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        return context.getTaskIdentityService().getGroups();
    }

}
