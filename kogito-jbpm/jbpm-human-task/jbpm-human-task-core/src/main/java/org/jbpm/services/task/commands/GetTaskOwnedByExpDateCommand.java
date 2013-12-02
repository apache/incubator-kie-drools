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

@XmlRootElement(name="get-task-owned-exp-date-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskOwnedByExpDateCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

	private static final long serialVersionUID = 5077599352603072633L;
	
    @XmlElement
	private List<Status> status;
    
    @XmlElement
    private Date expirationDate;
    
    @XmlElement
    private boolean optional;
	
	public GetTaskOwnedByExpDateCommand() {
	}
	
	public GetTaskOwnedByExpDateCommand(String userId, List<Status> status, Date expirationDate, boolean optional) {
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
        
        if (optional) {
        	return context.getTaskQueryService().getTasksOwnedByExpirationDateOptional(userId, status, expirationDate);
        } else {
        	return context.getTaskQueryService().getTasksOwnedByExpirationDate(userId, status, expirationDate);
        }
    }

}
