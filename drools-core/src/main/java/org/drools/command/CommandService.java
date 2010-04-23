package org.drools.command;

import org.drools.command.impl.GenericCommand;

public interface CommandService {
	
	<T> T execute(GenericCommand<T> command);

	Context getContext();	
}
