/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.commands;

import org.kie.api.runtime.Context;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

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
