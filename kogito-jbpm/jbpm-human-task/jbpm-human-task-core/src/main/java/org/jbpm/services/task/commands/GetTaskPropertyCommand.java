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
import org.kie.internal.task.api.TaskInstanceService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name="get-task-property-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskPropertyCommand extends UserGroupCallbackTaskCommand<Object> {

	private static final long serialVersionUID = -836520791223188840L;

	@XmlElement
	@XmlSchemaType(name="integer")
	private Integer property;

	
	public GetTaskPropertyCommand() {
	}
	
	public GetTaskPropertyCommand(long taskId, String userId, Integer property) {
		this.taskId = taskId;
		this.userId = userId;
		this.property = property;
    }

    public Integer getProperty() {
		return property;
	}

	public void setProperty(Integer name) {
		this.property = name;
	}

	public Object execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        TaskInstanceService service = context.getTaskInstanceService();
        Object result = null;
        switch (property) {
		case SetTaskPropertyCommand.PRIORITY_PROPERTY:
			result = service.getPriority(taskId);
			break;
		case SetTaskPropertyCommand.EXPIRATION_DATE_PROPERTY:
			result = service.getExpirationDate(taskId);
			break;
		case SetTaskPropertyCommand.DESCRIPTION_PROPERTY:
			result = service.getDescriptions(taskId);
			break;
		case SetTaskPropertyCommand.SKIPPABLE_PROPERTY:
			result = service.isSkipable(taskId);
			break;
		case SetTaskPropertyCommand.SUB_TASK_STRATEGY_PROPERTY:
			result = service.getSubTaskStrategy(taskId);
			break;
		default:
			break;
		}
        return result;
    }

}
