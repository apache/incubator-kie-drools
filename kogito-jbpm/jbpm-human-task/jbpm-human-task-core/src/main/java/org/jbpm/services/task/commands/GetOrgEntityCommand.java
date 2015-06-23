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

import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-org-entity-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetOrgEntityCommand extends TaskCommand<OrganizationalEntity> {

	private static final long serialVersionUID = -836520791223188840L;

    @XmlElement
	private String id;
	
	public GetOrgEntityCommand() {
	}
	
	public GetOrgEntityCommand(String id) {
		this.id = id;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OrganizationalEntity execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        return context.getTaskIdentityService().getOrganizationalEntityById(id);
    }

}
