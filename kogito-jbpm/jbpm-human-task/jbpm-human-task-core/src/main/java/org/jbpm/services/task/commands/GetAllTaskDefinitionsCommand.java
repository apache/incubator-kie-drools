package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.TaskDef;

@XmlRootElement(name="get-all-task-definitions-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllTaskDefinitionsCommand extends TaskCommand<List<TaskDef>> {

	private static final long serialVersionUID = 5077599352603072633L;

	@XmlElement
	@XmlSchemaType(name="string")
	private String filter;

	public GetAllTaskDefinitionsCommand() {
	}
	
	public GetAllTaskDefinitionsCommand(String filter) {
		this.filter = filter;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public List<TaskDef> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
        return context.getTaskDefService().getAllTaskDef(filter);
    }

}
