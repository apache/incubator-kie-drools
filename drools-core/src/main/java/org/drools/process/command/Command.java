package org.drools.process.command;

import org.drools.WorkingMemory;

public interface Command<T> {
	
	T execute(WorkingMemory workingMemory);
	
}
