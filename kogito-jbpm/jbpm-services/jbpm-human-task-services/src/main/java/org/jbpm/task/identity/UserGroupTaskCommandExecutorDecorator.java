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
package org.jbpm.task.identity;

import java.util.List;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Task;
import org.jbpm.task.annotations.CommandBased;
import org.jbpm.task.api.TaskCommandExecutor;
import org.jbpm.task.commands.AddTaskCommand;
import org.jbpm.task.commands.NominateTaskCommand;
import org.jbpm.task.commands.TaskCommand;

/**
 *
 */
@Decorator
public class UserGroupTaskCommandExecutorDecorator extends AbstractUserGroupCallbackDecorator implements TaskCommandExecutor {

    @Inject
    @Delegate
    @CommandBased
    private TaskCommandExecutor executor;
    

    public <T> T executeTaskCommand(TaskCommand<T> command) {
        if (command instanceof AddTaskCommand) {
            Task task = ((AddTaskCommand) command).getTask();
            doCallbackOperationForPeopleAssignments(task.getPeopleAssignments());
            doCallbackOperationForTaskData(task.getTaskData());
            doCallbackOperationForTaskDeadlines(task.getDeadlines());
        }
        if(command instanceof NominateTaskCommand){
            List<OrganizationalEntity> potentialOwners = ((NominateTaskCommand)command).getPotentialOwners();
            doCallbackOperationForPotentialOwners(potentialOwners);
        }
        command.setGroupsIds(doUserGroupCallbackOperation(command.getUserId(), command.getGroupsIds()));
        doCallbackUserOperation(command.getTargetEntityId());
        return executor.executeTaskCommand(command);
    }

    
}
