package org.jbpm.session;

public interface SessionManagerFactory {
	
	SessionManager getSessionManager();
	
	SessionManager getSessionManager(String context);
	
	// TODO: this should not throw an Exception
	void dispose() throws Exception;

}
