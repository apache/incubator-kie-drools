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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;


@XmlRootElement(name="remove-group-command")
@XmlAccessorType(XmlAccessType.NONE)
public class RemoveGroupCommand extends TaskCommand<Void> {

	private static final long serialVersionUID = 7393379209067431866L;

	@XmlElement
    @XmlSchemaType(name="string")
	private String groupId;
    
    public RemoveGroupCommand() {
    }

    public RemoveGroupCommand(String groupId) {
    	this.groupId = groupId;
    }


    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
                
        context.getTaskIdentityService().removeGroup(groupId);;
        return null;
    	 
    }

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

    
}
