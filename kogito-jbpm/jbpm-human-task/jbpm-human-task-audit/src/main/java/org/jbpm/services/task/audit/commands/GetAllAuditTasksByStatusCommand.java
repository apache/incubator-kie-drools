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


@XmlRootElement(name = "get-all-audit-tasks-by-status-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllAuditTasksByStatusCommand extends UserGroupCallbackTaskCommand<List<AuditTask>> {

    private QueryFilter filter;
    

    public GetAllAuditTasksByStatusCommand() {
        this.filter = new QueryFilter(0, 0);
    }

    public GetAllAuditTasksByStatusCommand(String userId, QueryFilter filter) {
        super.userId = userId;
        this.filter = filter;
    }

    @Override
    public List<AuditTask> execute(Context context) {
        TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();

        
        List<AuditTask> groupTasks = persistenceContext.queryWithParametersInTransaction("getAllAuditTasksByStatus",
                persistenceContext.addParametersToMap("owner", userId,"statuses", filter.getParams().get("statuses"),"firstResult", filter.getOffset(),
                        "maxResults", filter.getCount()),
                ClassUtil.<List<AuditTask>>castClass(List.class));
   
        return groupTasks;
    }

}
