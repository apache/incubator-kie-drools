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

@XmlRootElement(name="get-task-owned-exp-date-before-date-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskOwnedByExpDateBeforeDateCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

	private static final long serialVersionUID = 5077599352603072633L;
	
    @XmlElement
	private List<Status> status;
    
    @XmlElement
    private Date expirationDate;
	
	public GetTaskOwnedByExpDateBeforeDateCommand() {
	}
	
	public GetTaskOwnedByExpDateBeforeDateCommand(String userId, List<Status> status, Date expirationDate) {
		this.userId = userId;
		this.status = status;
		this.expirationDate = expirationDate;
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


	public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        
    	return context.getTaskQueryService().getTasksOwnedByExpirationDateBeforeSpecifiedDate(userId, status, expirationDate);
    
    }

}
