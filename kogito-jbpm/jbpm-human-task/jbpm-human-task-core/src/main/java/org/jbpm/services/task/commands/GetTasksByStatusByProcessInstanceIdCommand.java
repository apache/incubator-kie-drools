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

@XmlRootElement(name = "get-tasks-by-status-by-process-instance-id-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTasksByStatusByProcessInstanceIdCommand extends TaskCommand<List<TaskSummary>> {

    private static final long serialVersionUID = -6059681013108594344L;

    @XmlElement(name = "process-instance-id")
    @XmlSchemaType(name = "long")
    private Long processInstanceId;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String taskName;

    @XmlElement
    private List<Status> statuses;

    public GetTasksByStatusByProcessInstanceIdCommand() {
    }

    public GetTasksByStatusByProcessInstanceIdCommand(long processInstanceId, List<Status> status) {
        this.processInstanceId = processInstanceId;
        this.statuses = status;
    }

    public GetTasksByStatusByProcessInstanceIdCommand(long processInstanceId, List<Status> status, String taskName) {
        this.processInstanceId = processInstanceId;
        this.statuses = status;
        this.taskName = taskName;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public List<Status> getStatus() {
        return statuses;
    }

    public void setStatus(List<Status> status) {
        this.statuses = status;
    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (taskName != null) {
            return context.getTaskQueryService().getTasksByStatusByProcessInstanceIdByTaskName(processInstanceId, statuses, taskName);
        } else {
            return context.getTaskQueryService().getTasksByStatusByProcessInstanceId(processInstanceId, statuses);
        }
    }

}
