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
package org.jbpm.task.commands;

import org.jbpm.task.annotations.CommandBased;
import javax.inject.Inject;
import org.kie.command.Context;
import org.jboss.seam.transaction.Transactional;

import org.jbpm.task.api.TaskCommandExecutor;
import org.jbpm.task.commands.TaskCommand;
import org.jbpm.task.commands.TaskContext;

/**
 *
 */
@CommandBased @Transactional
public class TaskCommandExecutorImpl implements TaskCommandExecutor{
    @Inject
    private TaskContext context;

    public <T> T executeTaskCommand(TaskCommand<T> command) {
        return (T)command.execute((Context)context);
    }
    
}
