package org.drools.runtime.impl;

import java.util.Collection;
import java.util.List;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.vsm.ServiceManager;


public class BatchExecutionImpl implements GenericCommand<Void> {
	private List<GenericCommand> commands;
	
	private String lookup;

	public BatchExecutionImpl(List<GenericCommand> commands) {
        this.commands = commands;
    }
	
    public BatchExecutionImpl(List<GenericCommand> commands, String lookup) {
        this.commands = commands;
        this.lookup = lookup;
    }	

    public List<GenericCommand> getCommands() {
        return commands;
    }

    public Void execute(Context context) {
        for ( GenericCommand command : commands ) {
            ((GenericCommand)command).execute( context );
        }
        return null;
    }	
    
}
