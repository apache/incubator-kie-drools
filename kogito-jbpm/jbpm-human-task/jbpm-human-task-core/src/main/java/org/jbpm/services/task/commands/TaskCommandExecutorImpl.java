/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.command.CommandService;
import org.drools.core.command.Interceptor;
import org.drools.core.command.impl.GenericCommand;
import org.jbpm.services.task.events.TaskEventSupport;
import org.kie.api.command.Command;
import org.kie.api.runtime.Environment;
import org.kie.internal.command.Context;



public class TaskCommandExecutorImpl implements CommandService {
	
	private Environment environment;
	private TaskEventSupport taskEventSupport;
	private CommandService commandService = new SelfExecutionCommandService(this);
	
	public TaskCommandExecutorImpl(Environment environment, TaskEventSupport taskEventSupport) {
		this.environment = environment;
		this.taskEventSupport = taskEventSupport;
	}
    
	public <T> T execute(Command<T> command) {
    	return this.commandService.execute(command);
    }
	
	public void addInterceptor(Interceptor interceptor) {
        interceptor.setNext( this.commandService );
        this.commandService = interceptor;
    }

	@Override
	public Context getContext() {
		if (this.commandService instanceof SelfExecutionCommandService) {
			return new TaskContext();
		}
		return new TaskContext(commandService.getContext(), environment, taskEventSupport);
	}
	
	private class SelfExecutionCommandService implements CommandService {
		private TaskCommandExecutorImpl owner;
		
		SelfExecutionCommandService(TaskCommandExecutorImpl owner) {
			this.owner = owner;
		}
		@Override
		public <T> T execute(Command<T> command) {
			if (command instanceof TaskCommand) {
	    		return (T)((GenericCommand<T>) command).execute(getContext());
	    	} else {
	    		throw new IllegalArgumentException("Task service can only execute task commands");
	    	}
		}

		@Override
		public Context getContext() {
			return owner.getContext();
		}
		
	}
    
}
