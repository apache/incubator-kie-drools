package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;

public interface Command<T> extends org.drools.command.Command {
	
	T execute(ReteooWorkingMemory session);
	
}
