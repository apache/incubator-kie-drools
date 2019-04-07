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

import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskIdentityService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;


@XmlRootElement(name="add-users-groups-command")
@XmlAccessorType(XmlAccessType.NONE)
public class AddUsersGroupsCommand extends TaskCommand<Void> {

	private static final long serialVersionUID = 5800835226386301758L;

	@XmlJavaTypeAdapter(JaxbMapAdapter.class)
	@XmlElement
	private Map<String, User> users;
	
	@XmlJavaTypeAdapter(JaxbMapAdapter.class)
	@XmlElement
	private Map<String, Group> groups;
    
    public AddUsersGroupsCommand() {
    }

    public AddUsersGroupsCommand(Map<String, User> users, Map<String, Group> groups) {
    	this.users = users;
    	this.groups = groups;
    }


    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
        TaskIdentityService identityService = context.getTaskIdentityService();
        for (User user : users.values()) {
        	identityService.addUser(user);
        }

        for (Group group : groups.values()) {
        	identityService.addGroup(group);
        }
        return null;
    	 
    }

}
