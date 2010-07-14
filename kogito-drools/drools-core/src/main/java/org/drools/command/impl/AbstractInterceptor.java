package org.drools.command.impl;

import org.drools.StatefulSession;
import org.drools.command.Command;
import org.drools.command.CommandService;
import org.drools.command.Context;
import org.drools.command.Interceptor;

public abstract class AbstractInterceptor implements Interceptor {

	private CommandService next;
	
	public Context getContext() {
		return next.getContext();
	}
	
	public void setNext(CommandService commandService) {
		this.next = commandService;
	}
	
	public CommandService getNext() {
		return next;
	}

	protected <T> T executeNext(Command<T> command) {
		return next.execute(command);
	}

}
