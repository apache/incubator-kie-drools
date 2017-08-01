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
import org.kie.api.task.model.Group;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;


@XmlRootElement(name="add-group-command")
@XmlAccessorType(XmlAccessType.NONE)
public class AddGroupCommand extends TaskCommand<Void> {

	/** generated serial version UID */
    private static final long serialVersionUID = 8723206149543662492L;
    
    @XmlElement
    @XmlSchemaType(name="string")
	private String groupId;
    
    public AddGroupCommand() {
    }

    public AddGroupCommand(String userId) {
    	this.groupId = userId;
    }


    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
        Group group = TaskModelProvider.getFactory().newGroup();
        ((InternalOrganizationalEntity) group).setId(groupId);
        
        context.getTaskIdentityService().addGroup(group);
        return null;
    	 
    }

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

    
}
