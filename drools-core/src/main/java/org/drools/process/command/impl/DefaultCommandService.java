package org.drools.process.command.impl;

import org.drools.StatefulSession;
import org.drools.process.command.Command;
import org.drools.process.command.CommandService;
import org.drools.reteoo.ReteooWorkingMemory;

public class DefaultCommandService implements CommandService {

	private ReteooWorkingMemory session;
	
	public DefaultCommandService(StatefulSession session) {
		this.session = ( ReteooWorkingMemory ) session;
	}
	
	public StatefulSession getSession() {
		return ( StatefulSession ) session;
	}
	
	public <T> T execute(Command<T> command) {
		return command.execute(session);
	}
	
	public void dispose() {
		session.dispose();
	}

}
