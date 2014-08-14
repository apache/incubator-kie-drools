package org.jbpm.services.task.commands;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-task-assigned-as-potential-owner-by-exp-date-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskAssignedAsPotentialOwnerByExpDateCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

	private static final long serialVersionUID = 5077599352603072633L;
	
    @XmlElement
	private List<Status> status;
    
    @XmlElement
    private Date expirationDate;
    
    @XmlElement
    private boolean optional;
	
	public GetTaskAssignedAsPotentialOwnerByExpDateCommand() {
	}
	
	public GetTaskAssignedAsPotentialOwnerByExpDateCommand(String userId, List<Status> status, Date expirationDate, boolean optional) {
		this.userId = userId;
		this.status = status;
		this.expirationDate = expirationDate;
		this.optional = optional;
    }
	
	public List<Status> getStatuses() {
		return status;
	}
	
	public void setStatuses(List<Status> status) {
		this.status = status;
	}

    public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        List<String> groupIds = doUserGroupCallbackOperation(userId, null, context);
        
        if (optional) {
        	return context.getTaskQueryService().getTasksAssignedAsPotentialOwnerByExpirationDateOptional(userId, groupIds, status, expirationDate);
        } else {
        	return context.getTaskQueryService().getTasksAssignedAsPotentialOwnerByExpirationDate(userId, groupIds, status, expirationDate);
        }
    }

}
