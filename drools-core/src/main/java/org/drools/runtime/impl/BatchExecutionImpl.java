package org.drools.runtime.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;

@XmlRootElement
public class BatchExecutionImpl implements GenericCommand<Void> {

	private static final long serialVersionUID = 1L;

	private List<GenericCommand> commands;
	
	private String lookup;
	
	public BatchExecutionImpl() {
	}

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
