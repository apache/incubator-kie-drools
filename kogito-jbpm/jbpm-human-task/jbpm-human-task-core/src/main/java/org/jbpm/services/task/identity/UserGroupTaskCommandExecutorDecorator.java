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
package org.jbpm.services.task.identity;

import java.util.List;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.jbpm.services.task.annotations.CommandBased;
import org.jbpm.services.task.commands.AddTaskCommand;
import org.jbpm.services.task.commands.NominateTaskCommand;
import org.jbpm.services.task.commands.TaskCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;

/**
 *
 */
@Decorator
public class UserGroupTaskCommandExecutorDecorator extends AbstractUserGroupCallbackDecorator implements CommandExecutor {

    @Inject
    @Delegate
    @CommandBased
    private CommandExecutor executor;

    public <T> T execute(Command<T> cmd) {
        TaskCommand<T> command = (TaskCommand<T>) cmd;
        if (command instanceof AddTaskCommand) {
            Task task = ((AddTaskCommand) command).getTask();
            doCallbackOperationForPeopleAssignments((InternalPeopleAssignments) task.getPeopleAssignments());
            doCallbackOperationForTaskData((InternalTaskData) task.getTaskData());
            doCallbackOperationForTaskDeadlines(((InternalTask) task).getDeadlines());
        }
        if(command instanceof NominateTaskCommand){
            List<OrganizationalEntity> potentialOwners = ((NominateTaskCommand)command).getPotentialOwners();
            doCallbackOperationForPotentialOwners(potentialOwners);
        }
        command.setGroupsIds(doUserGroupCallbackOperation(command.getUserId(), command.getGroupsIds()));
        doCallbackUserOperation(command.getTargetEntityId());
        return (T) executor.execute(command);
    }


}
