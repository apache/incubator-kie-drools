package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.internal.command.Context;

@XmlRootElement(name="get-pending-sub-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetPendingSubTasksCommand extends TaskCommand<Integer> {

	private static final long serialVersionUID = 5077599352603072633L;

    @XmlElement
    @XmlSchemaType(name="long")
	private Long parentId;
	
	public GetPendingSubTasksCommand() {
	}
	
	public GetPendingSubTasksCommand(Long parentId) {
		this.parentId = parentId;
	}
		
    public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
    	return context.getTaskQueryService().getPendingSubTasksByParent(parentId);
        
    	

    }

}
