package org.jbpm.services.task.commands;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.internal.query.QueryFilter;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

@XmlRootElement(name = "get-task-assigned-pot-owner-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskAssignedAsPotentialOwnerCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

    private static final long serialVersionUID = 5077599352603072633L;

    @XmlElement
    private List<Status> status;
    
    private QueryFilter filter;

    public GetTaskAssignedAsPotentialOwnerCommand() {
    }
    
    public GetTaskAssignedAsPotentialOwnerCommand(String userId) {
        this.userId = userId;
    }
    
    public GetTaskAssignedAsPotentialOwnerCommand(String userId, List<Status> status) {
        this.userId = userId;
        this.status = status;
    }

     public GetTaskAssignedAsPotentialOwnerCommand(String userId, List<String> groupIds, List<Status> status) {
        this.userId = userId;
        this.status = status;
        this.groupsIds = groupIds;
    }
    
    public GetTaskAssignedAsPotentialOwnerCommand(String userId, List<String> groupIds, List<Status> status, QueryFilter filter) {
        this.userId = userId;
        this.status = status;
        this.groupsIds = groupIds;
        this.filter = filter;
    }

    public List<Status> getStatuses() {
        return status;
    }


    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        if(status == null ){
            status = new ArrayList<Status>();
            status.add(Status.Ready);
            status.add(Status.InProgress);
            status.add(Status.Reserved);
        }
        if(groupsIds == null){
            groupsIds = doUserGroupCallbackOperation(userId, null, context);
        }
        return context.getTaskQueryService().getTasksAssignedAsPotentialOwner(userId, groupsIds, status, filter);
       
    }

}
