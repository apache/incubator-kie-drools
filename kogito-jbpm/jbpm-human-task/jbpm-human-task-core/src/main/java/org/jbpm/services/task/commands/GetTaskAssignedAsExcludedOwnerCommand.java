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

@XmlRootElement(name = "get-task-assigned-excluded-owner-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskAssignedAsExcludedOwnerCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

    private static final long serialVersionUID = 5077599352603072633L;

    @XmlElement
    private List<Status> status;

    public GetTaskAssignedAsExcludedOwnerCommand() {
    }

    public GetTaskAssignedAsExcludedOwnerCommand(String userId) {
        this.userId = userId;

    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        return context.getTaskQueryService().getTasksAssignedAsExcludedOwner(userId);

    }

}
