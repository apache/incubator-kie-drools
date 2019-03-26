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

import java.util.HashMap;

import org.jbpm.services.task.commands.TaskCommand;
import org.kie.api.runtime.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="delete-bam-task-summaries-for-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class DeleteBAMTaskSummariesCommand extends TaskCommand<Void> {

    private static final long serialVersionUID = -7929370526623674312L;

    public DeleteBAMTaskSummariesCommand() { 
        // default, delete all
    }
    
    public DeleteBAMTaskSummariesCommand(long taskId) {
        this.taskId = taskId;
    }

    @Override
    public Void execute(Context context) {
        TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
        if( this.taskId != null ) { 
        	HashMap<String, Object> params = new HashMap<String, Object>();
        	params.put("taskId", this.taskId);
        	persistenceContext.executeUpdate("deleteBAMTaskSummariesForTask", params);
        } else {
        	persistenceContext.executeUpdate("deleteAllBAMTaskSummaries", null);
        }
        return null;
    }

}
