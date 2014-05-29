package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

@XmlRootElement(name = "get-sub-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetSubTasksCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

    private static final long serialVersionUID = 5077599352603072633L;

    @XmlElement
    @XmlSchemaType(name = "long")
    private Long parentId;

    public GetSubTasksCommand() {
    }

    public GetSubTasksCommand(Long parentId) {
        this.parentId = parentId;
    }

    public GetSubTasksCommand(Long parentId, String userId) {
        this.parentId = parentId;
        this.userId = userId;

    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (userId != null) {
            doCallbackUserOperation(userId, context);
            doUserGroupCallbackOperation(userId, null, context);
            return context.getTaskQueryService().getSubTasksAssignedAsPotentialOwner(parentId, userId);
        } else {
            return context.getTaskQueryService().getSubTasksByParent(parentId);
        }

    }

}
