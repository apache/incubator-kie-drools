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

@XmlRootElement(name="get-task-by-proc-inst-id-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTasksByProcessInstanceIdCommand extends TaskCommand<List<Long>> {

    @XmlElement
    @XmlSchemaType(name="long")
	private Long processInstanceId;
	
	public GetTasksByProcessInstanceIdCommand() {
	}
	
	public GetTasksByProcessInstanceIdCommand(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
    }

    public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public List<Long> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
    		return context.getTaskService().getTasksByProcessInstanceId(processInstanceId);
        }
    	return context.getTaskQueryService().getTasksByProcessInstanceId(processInstanceId);
    }

}
