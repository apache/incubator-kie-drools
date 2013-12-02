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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jbpm.services.task.impl.model.xml.adapter.OrganizationalEntityXmlAdapter;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.command.Context;

/**
 * Operation.Skip : [ new OperationCommand().{ status = [ Status.Created ],
 * allowed = [ Allowed.Initiator, Allowed.BusinessAdministrator ], newStatus =
 * Status.Obsolete, skipable = true }, new OperationCommand().{ status = [
 * Status.Ready ], allowed = [ Allowed.PotentialOwner,
 * Allowed.BusinessAdministrator ], newStatus = Status.Obsolete, skipable = true
 * }, new OperationCommand().{ status = [ Status.Reserved, Status.InProgress ],
 * allowed = [ Allowed.Owner, Allowed.BusinessAdministrator ], newStatus =
 * Status.Obsolete, skipable = true } ],
 */
@XmlRootElement(name="nominate-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class NominateTaskCommand extends UserGroupCallbackTaskCommand<Void> {

	private static final long serialVersionUID = 1874781422343631410L;

	@XmlElement(name="potential-owner")
    @XmlJavaTypeAdapter(value=OrganizationalEntityXmlAdapter.class)
    private List<OrganizationalEntity> potentialOwners;
    
    public NominateTaskCommand() {
    }
    
    public NominateTaskCommand(long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
        this.taskId = taskId;
        this.userId = userId;
        this.potentialOwners = potentialOwners;
    }

    public void setPotentialOwners(List<OrganizationalEntity> potentialOwners) {
		this.potentialOwners = potentialOwners;
	}

	public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        doCallbackOperationForPotentialOwners(potentialOwners, context);
        doUserGroupCallbackOperation(userId, null, context);
    	context.getTaskInstanceService().nominate(taskId, userId, potentialOwners);
    	return null;
       
    }

    public List<OrganizationalEntity> getPotentialOwners() {
        return potentialOwners;
    }
    
    
}
