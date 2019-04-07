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
import org.kie.internal.task.api.model.TaskDef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;



@XmlRootElement(name="deploy-task-def-command")
@XmlAccessorType(XmlAccessType.NONE)
public class DeployTaskDefCommand extends TaskCommand<Void> {


	private static final long serialVersionUID = 5656199063761548979L;

	@XmlTransient
	private TaskDef taskDef;
	
	public DeployTaskDefCommand() {
	}
	
    public DeployTaskDefCommand(TaskDef taskDef) {
        this.taskDef = taskDef;
    }

    public Void execute(Context cntxt ) {
        TaskContext context = (TaskContext) cntxt;
        context.getTaskDefService().deployTaskDef(taskDef);
    	return null;
           
    }
}
