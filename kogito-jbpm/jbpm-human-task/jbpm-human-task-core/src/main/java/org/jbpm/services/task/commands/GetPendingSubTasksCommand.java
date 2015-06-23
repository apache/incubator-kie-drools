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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.internal.command.Context;

@XmlRootElement(name="get-pending-sub-tasks-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetPendingSubTasksCommand extends TaskCommand<Integer> {

	private static final long serialVersionUID = 5077599352603072633L;

    @XmlElement
    @XmlSchemaType(name="long")
	private Long parentId;
	
	public GetPendingSubTasksCommand() {
	}
	
	public GetPendingSubTasksCommand(Long parentId) {
		this.parentId = parentId;
	}
		
    public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
    	return context.getTaskQueryService().getPendingSubTasksByParent(parentId);
        
    	

    }

}
