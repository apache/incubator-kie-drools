package org.drools.runtime.impl;

import java.util.List;

import org.drools.process.command.Command;
import org.drools.reteoo.ReteooWorkingMemory;


public class BatchExecutionImpl implements Command<Void> {
	private List<Command> commands;

	public BatchExecutionImpl(List<Command> commands) {
        super();
        this.commands = commands;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public Void execute(ReteooWorkingMemory session) {
        for ( Command command : commands ) {
            ((org.drools.process.command.Command)command).execute( session );
        }
        return null;
    }	
    
}
