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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="remove-all-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class RemoveAllTasksCommand extends TaskCommand<Integer> {
	
	private static final long serialVersionUID = -5174631969130504959L;

	public RemoveAllTasksCommand() {
	}


	public Integer execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
    	return context.getTaskAdminService().removeAllTasks();
       
    }
}
