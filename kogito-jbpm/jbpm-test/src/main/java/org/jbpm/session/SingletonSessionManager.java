package org.jbpm.session;

public class SingletonSessionManager extends AbstractSessionManager {

	public SingletonSessionManager(SessionEnvironment sessionEnvironment) {
		this.sessionEnvironment = sessionEnvironment;
	}
	
	public void dispose() {
		// Do nothing, sessionEnvironment is reused across session managers
	}
	
	// TODO: when using the same task service across multiple threads, we must synchronize access to it, same
	// way as we do with the ksession (using the ksession and synchronization lock to avoid deadlock)
	
}
