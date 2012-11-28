package org.jbpm.session;

import org.jbpm.task.TaskService;
import org.kie.runtime.StatefulKnowledgeSession;

/**
 * A session manager holds a reference to a ksession and task service. Each thread should request
 * it's own session manager from the session manager factory.  
 * 
 * When the session manager is no longer needed, it should be disposed.
 * 
 * The session manager will make sure that the same ksession and task service will be reused for multiple
 * invocations within the same transaction, so that subsequent invocations will be able to see the (uncommitted)
 * changes of previous calls.
 * 
 * TODO: do we allow multiple calls to the same session manager 
 *  - when not using user transactions? (yes)
 *  - when using user transactions 
 *    - do we expect the user to always call dispose() once? (or can we use transaction synchronization here, but
 *      how to know which one, do we expect user to init transaction listener with method call? or multiple
 *      version of session manager)
 *    - can we use the transaction synchronization to always dispose the ksession at the end of the transaction
 * 
 * @author kverlaen
 */
public abstract class AbstractSessionManager implements SessionManager {
	
	protected SessionEnvironment sessionEnvironment;
	
	public StatefulKnowledgeSession getKnowledgeSession() {
		return sessionEnvironment.getKnowledgeSession();
	}
	
	public TaskService getTaskService() {
		return sessionEnvironment.getTaskService();
	}
}
