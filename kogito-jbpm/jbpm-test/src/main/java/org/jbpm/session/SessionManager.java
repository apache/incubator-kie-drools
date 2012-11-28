package org.jbpm.session;

import org.jbpm.task.TaskService;
import org.kie.runtime.StatefulKnowledgeSession;

public interface SessionManager {
	
	StatefulKnowledgeSession getKnowledgeSession();
	
	TaskService getTaskService();
	
	// TODO this shouldn't throw an Exception
	void dispose() throws Exception;

}
