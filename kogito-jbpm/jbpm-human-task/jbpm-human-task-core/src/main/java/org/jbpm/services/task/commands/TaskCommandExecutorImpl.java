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

import javax.inject.Inject;

import org.drools.core.command.impl.GenericCommand;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.annotations.CommandBased;
import org.kie.api.command.Command;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.command.Context;


/**
 *
 */
@CommandBased @Transactional
public class TaskCommandExecutorImpl implements CommandExecutor {
    @Inject
    private TaskContext context;

    public <T> T execute(Command<T> command) {
    	if (command instanceof TaskCommand) {
    		return (T)((GenericCommand) command).execute((Context)context);
    	} else {
    		throw new IllegalArgumentException("Task service can only execute task commands");
    	}
    }
    
}
