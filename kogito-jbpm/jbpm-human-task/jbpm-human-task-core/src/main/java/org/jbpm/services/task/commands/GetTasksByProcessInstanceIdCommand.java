package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.internal.command.Context;

@XmlRootElement(name="get-task-by-proc-inst-id-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTasksByProcessInstanceIdCommand extends TaskCommand<List<Long>> {

	private static final long serialVersionUID = -2328845811017055632L;

	@XmlElement(name="process-instance-id")
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
    	return context.getTaskQueryService().getTasksByProcessInstanceId(processInstanceId);
    }

}
