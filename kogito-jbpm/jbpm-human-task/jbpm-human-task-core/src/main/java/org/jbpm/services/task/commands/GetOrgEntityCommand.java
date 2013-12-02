package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-org-entity-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetOrgEntityCommand extends TaskCommand<OrganizationalEntity> {

	private static final long serialVersionUID = -836520791223188840L;

	private String id;
	
	public GetOrgEntityCommand() {
	}
	
	public GetOrgEntityCommand(String id) {
		this.id = id;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OrganizationalEntity execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        return context.getTaskIdentityService().getOrganizationalEntityById(id);
    }

}
