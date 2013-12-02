package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.TaskDef;

@XmlRootElement(name="get-task-definition-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskDefinitionCommand extends TaskCommand<TaskDef> {

	private static final long serialVersionUID = 5077599352603072633L;

	@XmlElement
    @XmlSchemaType(name="string")
	private String definitionId;
	
	public GetTaskDefinitionCommand() {
	}
	
	public GetTaskDefinitionCommand(String definitionId) {
		this.definitionId = definitionId;
    }

	public String getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(String definitionId) {
		this.definitionId = definitionId;
	}

	public TaskDef execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
    	return context.getTaskDefService().getTaskDefById(definitionId);

    }

}
