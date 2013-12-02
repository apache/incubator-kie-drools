/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.kie.api.task.model.User;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;


@XmlRootElement(name="add-user-command")
@XmlAccessorType(XmlAccessType.NONE)
public class AddUserCommand extends TaskCommand<Void> {

	private static final long serialVersionUID = 5800835226386301758L;

	@XmlElement
    @XmlSchemaType(name="string")
	private String userId;
    
    public AddUserCommand() {
    }

    public AddUserCommand(String userId) {
    	this.userId = userId;
    }


    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user).setId(userId);
        
        context.getTaskIdentityService().addUser(user);
        return null;
    	 
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

    
}
