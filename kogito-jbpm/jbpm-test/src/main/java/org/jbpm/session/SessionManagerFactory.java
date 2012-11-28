package org.jbpm.session;

public interface SessionManagerFactory {
	
	SessionManager getSessionManager();
	
	// TODO: this should not throw an Exception
	void dispose() throws Exception;

}
