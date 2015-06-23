/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;
import org.kie.api.task.model.Status;

@XmlRootElement(name = "get-task-assigned-as-business-admin-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskAssignedAsBusinessAdminCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

    private static final long serialVersionUID = -128903115964900028L;
    
    @XmlElement
    private List<Status> status;

    public GetTaskAssignedAsBusinessAdminCommand() {
    }

    public GetTaskAssignedAsBusinessAdminCommand(String userId) {
        this.userId = userId;

    }
    
    public GetTaskAssignedAsBusinessAdminCommand(String userId, List<Status> status) {
        this.userId = userId;
        this.status=status;
    }

    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        doUserGroupCallbackOperation(userId, null, context);
        if (status == null || status.isEmpty()){
            return context.getTaskQueryService().getTasksAssignedAsBusinessAdministrator(userId);
        }
        return context.getTaskQueryService().getTasksAssignedAsBusinessAdministratorByStatus(userId,status);
    }

}
