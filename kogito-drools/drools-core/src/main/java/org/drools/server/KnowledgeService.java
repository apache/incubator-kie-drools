package org.drools.server;

import org.drools.CheckedDroolsException;

public interface KnowledgeService {
	
	public String executeCommand(String cmd) throws CheckedDroolsException;

}
