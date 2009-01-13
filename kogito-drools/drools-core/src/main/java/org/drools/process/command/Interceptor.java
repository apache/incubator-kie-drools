package org.drools.process.command;


public interface Interceptor extends CommandService {

	void setNext(CommandService commandService);
	
	CommandService getNext();
	
}
