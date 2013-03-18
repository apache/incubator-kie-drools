package org.drools.core.command.impl;

import org.drools.core.command.runtime.process.AbortWorkItemCommand;
import org.drools.core.command.runtime.process.CompleteWorkItemCommand;
import org.drools.core.command.runtime.process.SignalEventCommand;
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.drools.core.command.runtime.process.StartProcessInstanceCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.kie.api.command.Command;

public class FireAllRulesInterceptor extends AbstractInterceptor {

	public <T> T execute(Command<T> command) {
		T result = executeNext(command);
		if (requiresFireAllRules(command)) {
			executeNext(new FireAllRulesCommand());
		}
		return result;
	}
	
	protected <T> boolean requiresFireAllRules(Command<T> command) {
		return command instanceof AbortWorkItemCommand
			|| command instanceof CompleteWorkItemCommand
			|| command instanceof SignalEventCommand
			|| command instanceof StartProcessCommand
			|| command instanceof StartProcessInstanceCommand;
	}
	
}
