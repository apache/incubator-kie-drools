package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

@XmlRootElement(name = "get-task-by-groups-item-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskAssignedByGroupsCommand extends TaskCommand<List<TaskSummary>> {

    private static final long serialVersionUID = 6296898155907765061L;

    public GetTaskAssignedByGroupsCommand() {
    }

    public GetTaskAssignedByGroupsCommand(List<String> groupIds) {
        this.groupsIds = groupIds;
    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        return context.getTaskQueryService().getTasksAssignedByGroups(groupsIds);
    }

}
