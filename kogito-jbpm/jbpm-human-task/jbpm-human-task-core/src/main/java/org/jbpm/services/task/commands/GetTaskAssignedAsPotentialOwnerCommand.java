package org.jbpm.services.task.commands;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;
import org.kie.internal.query.QueryFilter;

@XmlRootElement(name = "get-task-assigned-as-potential-owner-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskAssignedAsPotentialOwnerCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

    private static final long serialVersionUID = 5077599352603072633L;

    @XmlElement
    private List<Status> statuses;
   
    @XmlElement(type=QueryFilter.class)
    private QueryFilter filter;

    public GetTaskAssignedAsPotentialOwnerCommand() {
    }
    
    public GetTaskAssignedAsPotentialOwnerCommand(String userId) {
        this.userId = userId;
    }
    
    public GetTaskAssignedAsPotentialOwnerCommand(String userId, List<Status> status) {
        this.userId = userId;
        this.statuses = status;
    }

     public GetTaskAssignedAsPotentialOwnerCommand(String userId, List<String> groupIds, List<Status> status) {
        this.userId = userId;
        this.statuses = status;
        this.groupIds = groupIds;
    }
    
    public GetTaskAssignedAsPotentialOwnerCommand(String userId, List<String> groupIds, List<Status> status, QueryFilter filter) {
        this.userId = userId;
        this.statuses = status;
        this.groupIds = groupIds;
        this.filter = filter;
    }

    public List<Status> getStatuses() {
        return statuses;
    }


    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        if(statuses == null ){
            statuses = new ArrayList<Status>();
            statuses.add(Status.Ready);
            statuses.add(Status.InProgress);
            statuses.add(Status.Reserved);
        }
        if(groupIds == null){
            groupIds = doUserGroupCallbackOperation(userId, null, context);
        }
        return context.getTaskQueryService().getTasksAssignedAsPotentialOwner(userId, groupIds, statuses, filter);
       
    }

}
