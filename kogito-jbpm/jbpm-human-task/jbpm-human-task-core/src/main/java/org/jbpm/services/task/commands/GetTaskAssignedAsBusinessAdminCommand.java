package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-tasks-assigned-bus-admin-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskAssignedAsBusinessAdminCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

	private static final long serialVersionUID = -128903115964900028L;

	@XmlElement
    @XmlSchemaType(name="string")
	private String language;
	
	public GetTaskAssignedAsBusinessAdminCommand() {
	}
	
	public GetTaskAssignedAsBusinessAdminCommand(String userId, String language) {
		this.userId = userId;
		this.language = language;
    }

    public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        doUserGroupCallbackOperation(userId, null, context);
        return context.getTaskQueryService().getTasksAssignedAsBusinessAdministrator(userId, language);
    }

}
