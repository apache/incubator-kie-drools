package org.drools.process.command.impl;

import org.drools.command.impl.GenericCommand;

public class AsynchronousInterceptor extends AbstractInterceptor {

	public <T> T execute(final GenericCommand<T> command) {
		new Thread(new Runnable() {
			public void run() {
				executeNext(command);
			}
		}).start();
		return null;
	}

}
