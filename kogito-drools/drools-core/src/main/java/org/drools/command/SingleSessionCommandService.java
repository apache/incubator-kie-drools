package org.drools.command;

import org.drools.runtime.CommandExecutor;

public interface SingleSessionCommandService extends CommandService {
	int getSessionId();
	void dispose();
}
