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

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.I18NText;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.SubTasksStrategy;

@XmlRootElement(name="set-task-property-command")
@XmlAccessorType(XmlAccessType.NONE)
public class SetTaskPropertyCommand extends UserGroupCallbackTaskCommand<Void> {

	private static final long serialVersionUID = -836520791223188840L;

	@XmlElement
	@XmlSchemaType(name="integer")
	private Integer property;
	
	@XmlElement
	@XmlSchemaType(name="string")
	private Object value;
	
	public SetTaskPropertyCommand() {
	}
	
	public SetTaskPropertyCommand(long taskId, String userId, Integer property, Object value) {
		this.taskId = taskId;
		this.userId = userId;
		this.property = property;
		this.value = value;
    }

    public Integer getProperty() {
		return property;
	}

	public void setProperty(Integer name) {
		this.property = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        TaskInstanceService service = context.getTaskInstanceService();
        
        switch (property) {
		case FAULT_PROPERTY:
			doCallbackUserOperation(userId, context);
			service.setFault(taskId, userId, (FaultData) value);
			break;
		case OUTPUT_PROPERTY:
			doCallbackUserOperation(userId, context);
			service.setOutput(taskId, userId, value);
			break;
		case PRIORITY_PROPERTY:
			service.setPriority(taskId, (Integer) value);
			break;
		case TASK_NAMES_PROPERTY:
			service.setTaskNames(taskId, (List<I18NText>) value);
			break;
		case EXPIRATION_DATE_PROPERTY:
			service.setExpirationDate(taskId, (Date) value);
			break;
		case DESCRIPTION_PROPERTY:
			service.setDescriptions(taskId, (List<I18NText>) value);
			break;
		case SKIPPABLE_PROPERTY:
			service.setSkipable(taskId, (Boolean) value);
			break;
		case SUB_TASK_STRATEGY_PROPERTY:
			service.setSubTaskStrategy(taskId, (SubTasksStrategy) value);
			break;
		default:
			break;
		}
        return null;
    }

}
