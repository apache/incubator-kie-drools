package org.drools.process.command.impl;

import org.drools.command.Command;
import org.drools.command.impl.GenericCommand;


public class LoggingInterceptor extends AbstractInterceptor {

	public <T> T execute(GenericCommand<T> command) {
		System.out.println("Executing --> " + command);
		T result = executeNext(command);
		System.out.println("Done executing --> " + command);
		return result;
	}

}
