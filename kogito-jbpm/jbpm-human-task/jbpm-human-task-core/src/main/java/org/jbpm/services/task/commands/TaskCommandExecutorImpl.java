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

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.fluent.impl.Batch;
import org.drools.core.fluent.impl.InternalExecutable;
import org.drools.core.runtime.ChainableRunner;
import org.drools.core.runtime.InternalLocalRunner;
import org.jbpm.services.task.events.TaskEventSupport;
import org.kie.api.command.Command;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.RequestContext;
import org.kie.internal.identity.IdentityProvider;


public class TaskCommandExecutorImpl implements InternalLocalRunner {
	
	private Environment environment;
	private TaskEventSupport taskEventSupport;
	private InternalLocalRunner commandService = new SelfExecutionCommandService(this);
	
	public TaskCommandExecutorImpl(Environment environment, TaskEventSupport taskEventSupport) {
		this.environment = environment;
		this.taskEventSupport = taskEventSupport;
	}

	@Override
	public RequestContext execute( Executable executable, RequestContext ctx ) {
		return this.commandService.execute(executable, ctx);
	}

	public <T> T execute(Command<T> command) {
    	return this.commandService.execute(command);
    }
	
	public void addInterceptor(ChainableRunner interceptor) {
        interceptor.setNext( this.commandService );
        this.commandService = interceptor;
    }

	@Override
	public RequestContext createContext() {
		if (this.commandService instanceof SelfExecutionCommandService) {
			return new TaskContext();
		}
		return new TaskContext(commandService.createContext(), environment, taskEventSupport);
	}

	private class SelfExecutionCommandService implements InternalLocalRunner {
		private TaskCommandExecutorImpl owner;
		
		SelfExecutionCommandService(TaskCommandExecutorImpl owner) {
			this.owner = owner;
		}

		@Override
		public RequestContext execute(Executable executable, RequestContext context) {

			for (Batch batch : ( (InternalExecutable) executable ).getBatches()) {
				for (Command command : batch.getCommands()) {
					if (command instanceof TaskCommand) {
					    TaskContext ctx = new TaskContext(context, environment, taskEventSupport);
					    addUserIdToContext((TaskCommand<?>) command, ctx);
					    Object result = ((ExecutableCommand) command).execute( ctx );
					    context.set("Result", result);
					} else {
						throw new IllegalArgumentException("Task service can only execute task commands");
					}
				}
			}
			return context;
		}

		@Override
		public RequestContext createContext() {
			return owner.createContext();
		}
	}
    
	protected void addUserIdToContext(TaskCommand<?> command, RequestContext context) {
	    
	    if (context instanceof org.kie.internal.task.api.TaskContext) {
	        org.kie.internal.task.api.TaskContext taskContext = (org.kie.internal.task.api.TaskContext) context;
	        IdentityProvider identityProvider = (IdentityProvider) taskContext.get(EnvironmentName.IDENTITY_PROVIDER);
	        
	        String userId = command.getUserId();
	        if (command instanceof CompositeCommand) {
	            userId = ((CompositeCommand<?>) command).getMainCommand().getUserId();
	        }
	        if (userId == null && identityProvider != null) {
	            userId = identityProvider.getName();
	            command.setUserId(userId);
	        }
	        taskContext.setUserId(userId);
	    }
	    
	}
}
