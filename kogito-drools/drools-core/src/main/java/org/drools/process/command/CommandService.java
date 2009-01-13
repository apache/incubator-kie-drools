package org.drools.process.command;

import org.drools.StatefulSession;

public interface CommandService {
	
	<T> T execute(Command<T> command);

	StatefulSession getSession();
	
	void dispose();
	
}
