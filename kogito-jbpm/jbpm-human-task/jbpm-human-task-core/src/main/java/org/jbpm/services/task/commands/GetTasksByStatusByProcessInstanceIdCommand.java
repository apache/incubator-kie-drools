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

@XmlRootElement(name="get-task-by-status-proc-inst-id-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTasksByStatusByProcessInstanceIdCommand extends TaskCommand<List<TaskSummary>> {

    @XmlElement(name="process-instance-id")
    @XmlSchemaType(name="long")
	private Long processInstanceId;
	
    @XmlElement
    @XmlSchemaType(name="string")
	private String language;
	
    @XmlElement
	private List<Status> status;
	
	public GetTasksByStatusByProcessInstanceIdCommand() {
	}
	
	public GetTasksByStatusByProcessInstanceIdCommand(long processInstanceId, String language, List<Status> status) {
		this.processInstanceId = processInstanceId;
		this.language = language;
		this.status = status;
    }

    public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
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
    		return context.getTaskService().getTasksByStatusByProcessInstanceId(processInstanceId, status, language);
        }
    	return context.getTaskQueryService().getTasksByStatusByProcessInstanceId(processInstanceId, status, language);
    }

}
