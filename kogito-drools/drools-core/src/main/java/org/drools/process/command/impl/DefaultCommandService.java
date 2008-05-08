package org.drools.process.command.impl;

import org.drools.WorkingMemory;
import org.drools.process.command.Command;
import org.drools.process.command.CommandService;

public class DefaultCommandService implements CommandService {

	private WorkingMemory workingMemory;
	
	public DefaultCommandService(WorkingMemory workingMemory) {
		this.workingMemory = workingMemory;
	}
	
	public Object execute(Command command) {
		return command.execute(workingMemory);
	}

}
