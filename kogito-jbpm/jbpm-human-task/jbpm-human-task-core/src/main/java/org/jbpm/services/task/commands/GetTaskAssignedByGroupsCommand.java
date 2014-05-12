package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

@XmlRootElement(name = "get-task-by-groups-item-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskAssignedByGroupsCommand extends TaskCommand<List<TaskSummary>> {

    private static final long serialVersionUID = 6296898155907765061L;

    @XmlElement
    private List<String> groupIds;

    public GetTaskAssignedByGroupsCommand() {
    }

    public GetTaskAssignedByGroupsCommand(List<String> groupIds) {
        this.groupIds = groupIds;

    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        return context.getTaskQueryService().getTasksAssignedByGroups(groupIds);
    }

}
