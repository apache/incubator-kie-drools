package org.drools.commands.impl;

import org.drools.commands.fluent.Batch;
import org.drools.commands.fluent.InternalExecutable;
import org.drools.commands.runtime.process.AbortWorkItemCommand;
import org.drools.commands.runtime.process.CompleteWorkItemCommand;
import org.drools.commands.runtime.process.SignalEventCommand;
import org.drools.commands.runtime.process.StartProcessCommand;
import org.drools.commands.runtime.process.StartProcessInstanceCommand;
import org.drools.commands.runtime.rule.FireAllRulesCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.RequestContext;

public class FireAllRulesInterceptor extends AbstractInterceptor {

	public RequestContext execute( Executable executable, RequestContext ctx ) {
		executeNext(executable, ctx);
		if (requiresFireAllRules( ( (InternalExecutable) executable ))) {
			new FireAllRulesCommand().execute( ctx );
		}
		return ctx;
	}
	
	protected boolean requiresFireAllRules(InternalExecutable executable ) {
		for (Batch batch : executable.getBatches()) {
			for (Command command : batch.getCommands()) {
				if (requiresFireAllRules( command )) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean requiresFireAllRules(Command command) {
		return command instanceof AbortWorkItemCommand
			|| command instanceof CompleteWorkItemCommand
			|| command instanceof SignalEventCommand
			|| command instanceof StartProcessCommand
			|| command instanceof StartProcessInstanceCommand;
	}
	
}
