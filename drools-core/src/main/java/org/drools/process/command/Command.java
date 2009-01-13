package org.drools.process.command;

import org.drools.StatefulSession;

public interface Command<T> {
	
	T execute(StatefulSession session);
	
}
