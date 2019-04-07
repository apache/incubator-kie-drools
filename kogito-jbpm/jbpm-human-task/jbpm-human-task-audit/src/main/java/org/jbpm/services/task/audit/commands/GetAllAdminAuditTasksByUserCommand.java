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

package org.jbpm.services.task.audit.commands;

import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.runtime.Context;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@XmlRootElement(name = "get-all-admin-audit-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllAdminAuditTasksByUserCommand extends UserGroupCallbackTaskCommand<List<AuditTask>> {

    private QueryFilter filter;
    

    public GetAllAdminAuditTasksByUserCommand() {
        this.filter = new QueryFilter(0, 0);
    }

    public GetAllAdminAuditTasksByUserCommand(String userId, QueryFilter filter) {
        super.userId = userId;
        this.filter = filter;
    }

    @Override
    public List<AuditTask> execute(Context context) {
        TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();

        boolean userExists = doCallbackUserOperation(userId, (TaskContext) context);
        List<String> groupIds = doUserGroupCallbackOperation(userId, null, (TaskContext) context);
        //Adding the user to check for groups and user as Business Administrators
        groupIds.add(userId);
        List<AuditTask> groupTasks = persistenceContext.queryWithParametersInTransaction("getAllAdminAuditTasksByUser",
                persistenceContext.addParametersToMap("businessAdmins", groupIds,"firstResult", filter.getOffset(),
                        "maxResults", filter.getCount()),
                ClassUtil.<List<AuditTask>>castClass(List.class));
   
        return groupTasks;
    }

}
