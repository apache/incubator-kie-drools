package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-task-assigned-pot-owner-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskAssignedAsPotentialOwnerCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

	private static final long serialVersionUID = 5077599352603072633L;

	@XmlElement
    @XmlSchemaType(name="string")
	private String language;
	
    @XmlElement
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
	
	public GetTaskAssignedAsPotentialOwnerCommand(String userId, List<String> groupIds, String language, List<Status> status) {
		this.userId = userId;
		this.language = language;
		this.status = status;
		this.groupsIds = groupIds;
    }
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public List<Status> getStatuses() {
		return status;
	}
	
	public void setStatuses(List<Status> status) {
		this.status = status;
	}


	public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        if (groupsIds != null) {
        	return context.getTaskQueryService().getTasksAssignedAsPotentialOwnerByStatusByGroup(userId, groupsIds, status, language);
        }
        
        List<String> groupIds = doUserGroupCallbackOperation(userId, null, context);
        
        if (status == null) {
        	return context.getTaskQueryService().getTasksAssignedAsPotentialOwner(userId, groupIds, language);
        } else {
        	return context.getTaskQueryService().getTasksAssignedAsPotentialOwnerByStatusByGroup(userId, groupIds, status, language);
        }
    }

}
