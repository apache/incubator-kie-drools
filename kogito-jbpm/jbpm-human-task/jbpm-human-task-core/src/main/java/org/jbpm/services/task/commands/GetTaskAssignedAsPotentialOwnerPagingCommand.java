package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-task-assigned-pot-owner-paging-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskAssignedAsPotentialOwnerPagingCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

	private static final long serialVersionUID = 5077599352603072633L;

	@XmlElement
    @XmlSchemaType(name="string")
	private String language;
	
	@XmlElement
	private Integer firstResult;
	
	@XmlElement
	private Integer maxResults;
    
	public GetTaskAssignedAsPotentialOwnerPagingCommand() {
	}
	
	
	public GetTaskAssignedAsPotentialOwnerPagingCommand(String userId, List<String> groupIds, 
			String language, int firstResult, int maxResults) {
		this.userId = userId;
		this.language = language;
		this.groupsIds = groupIds;
		this.firstResult = firstResult;
		this.maxResults = maxResults;
    }
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}


	public Integer getFirstResult() {
		return firstResult;
	}


	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}


	public Integer getMaxResults() {
		return maxResults;
	}


	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}


	public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        
    	return context.getTaskQueryService().getTasksAssignedAsPotentialOwner(userId, groupsIds, language, firstResult, maxResults);
        
    }

}
