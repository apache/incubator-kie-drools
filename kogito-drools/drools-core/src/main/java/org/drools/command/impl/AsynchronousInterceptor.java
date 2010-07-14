package org.drools.command.impl;

import org.drools.command.Command;


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
