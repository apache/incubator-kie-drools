package org.drools.process.command;

public interface CommandService {
	
	<T> T execute(Command<T> command);

}
