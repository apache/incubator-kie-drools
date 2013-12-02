package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.Group;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-group-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetGroupCommand extends TaskCommand<Group> {

	private static final long serialVersionUID = -836520791223188840L;

	private String groupId;
	
	public GetGroupCommand() {
	}
	
	public GetGroupCommand(String groupId) {
		this.groupId = groupId;
    }

    public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Group execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        return context.getTaskIdentityService().getGroupById(groupId);
    }

}
