package org.drools.process.command.impl;

import org.drools.process.command.Command;

public class LoggingInterceptor extends AbstractInterceptor {

	public <T> T execute(Command<T> command) {
		System.out.println("Executing --> " + command);
		T result = executeNext(command);
		System.out.println("Done executing --> " + command);
		return result;
	}

}
