package org.drools.process.command.impl;

import org.drools.process.command.Command;

public class AsynchronousInterceptor extends AbstractInterceptor {

	public <T> T execute(final Command<T> command) {
		new Thread(new Runnable() {
			public void run() {
				executeNext(command);
			}
		}).start();
		return null;
	}

}
