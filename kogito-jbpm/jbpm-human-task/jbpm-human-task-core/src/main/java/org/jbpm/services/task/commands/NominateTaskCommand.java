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

import javax.enterprise.util.AnnotationLiteral;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.events.AfterTaskNominatedEvent;
import org.jbpm.services.task.events.BeforeTaskNominatedEvent;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.impl.model.xml.adapter.OrganizationalEntityXmlAdapter;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTaskData;

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
@Transactional
@XmlAccessorType(XmlAccessType.FIELD)
public class NominateTaskCommand extends TaskCommand<Void> {

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
        if (context.getTaskService() != null) {
        	context.getTaskService().nominate(taskId, userId, potentialOwners);
        	return null;
        }
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        User user = context.getTaskIdentityService().getUserById(userId);
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskNominatedEvent>() {
        }).fire(task);
        
        if (CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getBusinessAdministrators())) {


            ((InternalTaskData) task.getTaskData()).assignOwnerAndStatus(potentialOwners);
            if (task.getTaskData().getStatus() == Status.Ready) {
                ((InternalPeopleAssignments) task.getPeopleAssignments()).setPotentialOwners(potentialOwners);
            }

        } else {
            throw new PermissionDeniedException("User " + userId + " is not allowed to perform Nominate on Task " + taskId);
        }

        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskNominatedEvent>() {
        }).fire(task);

        return null;
    }

    public List<OrganizationalEntity> getPotentialOwners() {
        return potentialOwners;
    }
    
    
}
