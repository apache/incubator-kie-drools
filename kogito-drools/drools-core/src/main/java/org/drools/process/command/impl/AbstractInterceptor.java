package org.drools.process.command.impl;

import org.drools.StatefulSession;
import org.drools.process.command.Command;
import org.drools.process.command.CommandService;
import org.drools.process.command.Interceptor;

public abstract class AbstractInterceptor implements Interceptor {

	private CommandService next;
	
	public StatefulSession getSession() {
		return next.getSession();
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
	
	public void dispose() {
		next.dispose();
	}

}
