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

package org.jbpm.services.task.audit.commands;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.internal.task.api.AuditTask;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;


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
        List<AuditTask> groupTasks = persistenceContext.queryWithParametersInTransaction("getAllAdminAuditTasksByUser",
                persistenceContext.addParametersToMap("firstResult", filter.getOffset(),
                        "maxResults", filter.getCount()),
                ClassUtil.<List<AuditTask>>castClass(List.class));
        
        //We need an optimal way to do this
        List<AuditTask> filteredTasks = new ArrayList<AuditTask>();
        for(AuditTask at : groupTasks){
            if(at.getBusinessAdministrators().contains(userId)){
                filteredTasks.add(at);
            }else{
                for(String g : groupIds){
                   if(at.getBusinessAdministrators().contains(g)){
                       filteredTasks.add(at);
                   }
                }
            }
            
        }
        
        return filteredTasks;
    }

}
