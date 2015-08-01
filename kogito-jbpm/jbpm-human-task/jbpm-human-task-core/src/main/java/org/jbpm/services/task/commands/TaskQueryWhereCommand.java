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

import org.jbpm.query.jpa.data.QueryWhere;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

@XmlRootElement(name="task-query-where-command")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskQueryWhereCommand extends TaskCommand<List<TaskSummary>> {

    /** generated serial version UID */
    private static final long serialVersionUID = -6879337395030142688L;

    @XmlElement
    private QueryWhere queryWhere;
    
    public TaskQueryWhereCommand() { 
        // JAXB constructor
    }
    
    public TaskQueryWhereCommand(QueryWhere queryWhere) { 
        this.queryWhere = queryWhere;
    }
    
    public QueryWhere getQueryWhere() {
        return queryWhere;
    }

    public void setQueryWhere( QueryWhere queryWhere ) {
        this.queryWhere = queryWhere;
    }

    @Override
    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        return context.getTaskQueryService().query(userId, queryWhere);
    }
   
}
