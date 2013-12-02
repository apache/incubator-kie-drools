package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.Task;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-task-by-work-item-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskByWorkItemIdCommand extends TaskCommand<Task> {

    @XmlElement
    @XmlSchemaType(name="long")
	private Long workItemId;
	
	public GetTaskByWorkItemIdCommand() {
	}
	
	public GetTaskByWorkItemIdCommand(Long workItemId) {
		this.workItemId = workItemId;
    }
	
    public Long getWorkItemId() {
		return workItemId;
	}

	public void setWorkItemId(Long workItemId) {
		this.workItemId = workItemId;
	}

	public Task execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	return context.getTaskService().getTaskByWorkItemId(workItemId);
        }
        return context.getTaskQueryService().getTaskByWorkItemId(workItemId);
    }

}
