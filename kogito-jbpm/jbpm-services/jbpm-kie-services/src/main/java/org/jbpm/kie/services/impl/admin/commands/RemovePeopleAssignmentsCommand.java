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

package org.jbpm.kie.services.impl.admin.commands;

import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import org.jbpm.services.task.events.TaskEventSupport;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.runtime.Context;
import org.kie.api.task.TaskLifeCycleEventListener.AssignmentType;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.InternalPeopleAssignments;

import java.util.Arrays;
import java.util.List;

import static org.jbpm.kie.services.impl.admin.UserTaskAdminServiceImpl.*;


public class RemovePeopleAssignmentsCommand extends UserGroupCallbackTaskCommand<Void> {

    private static final long serialVersionUID = -1856489382099976731L;

    
    private int type;
    private OrganizationalEntity[] entities;

    public RemovePeopleAssignmentsCommand(String userId, long taskId, int type, OrganizationalEntity[] entities) {
        super();
        setUserId(userId);
        setTaskId(taskId);
        this.type = type;
        this.entities = entities;
    }

    @Override
    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        TaskEventSupport taskEventSupport = context.getTaskEventSupport();
        
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);       
        // security check
        if (!isBusinessAdmin(userId, task.getPeopleAssignments().getBusinessAdministrators(), context)) {
            throw new PermissionDeniedException("User " + userId + " is not business admin of task " + taskId);
        }
        List<OrganizationalEntity> entityList = Arrays.asList(entities);
        AssignmentType assignmentType = null;
        
        taskEventSupport.fireBeforeTaskUpdated(task, context);
        switch (type) {
            case POT_OWNER:
                assignmentType = AssignmentType.POT_OWNER;
                taskEventSupport.fireBeforeTaskAssignmentsRemovedEvent(task, context, assignmentType, entityList);
                task.getPeopleAssignments().getPotentialOwners().removeAll(entityList);
                break;
            case EXCL_OWNER:
                assignmentType = AssignmentType.EXCL_OWNER;
                taskEventSupport.fireBeforeTaskAssignmentsRemovedEvent(task, context, assignmentType, entityList);
                ((InternalPeopleAssignments)task.getPeopleAssignments()).getExcludedOwners().removeAll(entityList);    
                break;
            case ADMIN:
                assignmentType = AssignmentType.ADMIN;
                taskEventSupport.fireBeforeTaskAssignmentsRemovedEvent(task, context, assignmentType, entityList);
                task.getPeopleAssignments().getBusinessAdministrators().removeAll(entityList);                
                break;

            default:
                break;
        }
        taskEventSupport.fireAfterTaskAssignmentsRemovedEvent(task, context, assignmentType, entityList);
        return null;
    }

}
