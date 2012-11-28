package org.jbpm.session;

import org.jbpm.task.TaskService;
import org.kie.runtime.StatefulKnowledgeSession;

public class NewSessionSessionManager extends AbstractSessionManager {

	private StatefulKnowledgeSessionFactory factory;
	
	public NewSessionSessionManager(StatefulKnowledgeSessionFactory factory) {
		this.factory = factory;
	}
	
	// TODO: this should make sure the session is disposed after usage
	// it should also support a transaction callback to be used to dispose
	// the ksession (can this be automatic?)

	public StatefulKnowledgeSession getKnowledgeSession() {
		if (sessionEnvironment == null) {
			initSessionEnvironment();
		}
		return super.getKnowledgeSession();
	}
	
	public TaskService getTaskService() {
		if (sessionEnvironment == null) {
			initSessionEnvironment();
		}
		return super.getTaskService();
	}
	
	private void initSessionEnvironment() {
		sessionEnvironment = factory.createStatefulKnowledgeSession();
	}
	
	// TODO this shouldn't throw an Exception
	public void dispose() throws Exception {
		sessionEnvironment.dispose();
	}
	
}
