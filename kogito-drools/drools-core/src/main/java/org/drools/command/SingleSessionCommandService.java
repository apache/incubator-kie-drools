package org.drools.command;

public interface SingleSessionCommandService extends CommandService {
	int getSessionId();
	void dispose();
}
