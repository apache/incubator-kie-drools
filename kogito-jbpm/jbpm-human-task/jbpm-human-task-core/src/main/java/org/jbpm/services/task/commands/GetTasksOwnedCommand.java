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

@XmlRootElement(name="get-task-by-owner-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTasksOwnedCommand extends TaskCommand<List<TaskSummary>> {

    @XmlElement
    @XmlSchemaType(name="string")
	private String language;
    
    @XmlElement
	private List<Status> status;
	
	public GetTasksOwnedCommand() {
	}
	
	public GetTasksOwnedCommand(String userId, String language) {
		this.userId = userId;
		this.language = language;
    }

	public GetTasksOwnedCommand(String userId, String language, List<Status> status) {
		this.userId = userId;
		this.language = language;
		this.status = status;
    }

    public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<Status> getStatus() {
		return status;
	}

	public void setStatus(List<Status> status) {
		this.status = status;
	}

	public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	if (status == null) {
        		return context.getTaskService().getTasksOwned(userId, language);
        	} else {
        		return context.getTaskService().getTasksOwnedByStatus(userId, status, language);
        	}
        }
        if (status == null) {
        	return context.getTaskQueryService().getTasksOwned(userId, language);
        } else {
        	return context.getTaskQueryService().getTasksOwnedByStatus(userId, status, language);
        }
    }

}
