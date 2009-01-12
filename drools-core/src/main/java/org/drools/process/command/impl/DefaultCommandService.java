package org.drools.process.command.impl;

import org.drools.process.command.Command;
import org.drools.process.command.CommandService;
import org.drools.WorkingMemory;

public class DefaultCommandService implements CommandService {

	private WorkingMemory workingMemory;
	
	public DefaultCommandService(WorkingMemory workingMemory) {
		this.workingMemory = workingMemory;
	}
	
	public <T> T execute(Command<T> command) {
		return command.execute(workingMemory);
	}

}
