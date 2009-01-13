package org.drools.process.command.impl;

import org.drools.StatefulSession;
import org.drools.process.command.Command;
import org.drools.process.command.CommandService;

public class DefaultCommandService implements CommandService {

	private StatefulSession session;
	
	public DefaultCommandService(StatefulSession session) {
		this.session = session;
	}
	
	public StatefulSession getSession() {
		return session;
	}
	
	public <T> T execute(Command<T> command) {
		return command.execute(session);
	}
	
	public void dispose() {
		session.dispose();
	}

}
