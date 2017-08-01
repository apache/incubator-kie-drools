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
import java.util.List;

@XmlRootElement(name = "get-tasks-owned-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTasksOwnedCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

    private static final long serialVersionUID = -1763215272466075367L;

    @XmlElement
    private List<Status> statuses;
   
    @XmlElement(type=QueryFilter.class)
    private QueryFilter filter;

    public GetTasksOwnedCommand() {
    }

    public GetTasksOwnedCommand(String userId) {
        this.userId = userId;

    }

    public GetTasksOwnedCommand(String userId, List<Status> status) {
        this.userId = userId;
        this.statuses = status;
    }
    
    public GetTasksOwnedCommand(String userId, List<Status> status, QueryFilter filter) {
        this.userId = userId;
        this.statuses = status;
        this.filter = filter;
    }

    public List<Status> getStatus() {
        return statuses;
    }

    public QueryFilter getFilter() {
        return filter;
    }


    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        doUserGroupCallbackOperation(userId, null, context);
        return context.getTaskQueryService().getTasksOwned(userId, statuses, filter);
        
    }

}
