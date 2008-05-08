package org.drools.process.command;

import org.drools.WorkingMemory;

public interface Command {
	
	Object execute(WorkingMemory workingMemory);
	
}
