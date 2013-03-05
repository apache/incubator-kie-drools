package org.jbpm.shared.services.api;

import org.kie.runtime.KieSession;

public interface SessionManager {

    /**
     * Returns active session looked up by sessionId key managed by this manager<br/>
     * NOTE: Not every implementation must support this method as it is intended for multi session managers only
     * @param sessionId internal key that identifies session
     * @return active session if found by given sessionId key, otherwise null
     */
    KieSession getKsessionById(int sessionId);
    
    /**
     * Returns KieSession id for given process instance id. That is usually session id that the process instance
     * was started by.
     * @param processInstanceId process instance id that session should be found for
     * @return session id if found otherwise -1
     */
    int getSessionForProcessInstanceId(Long processInstanceId);
    
    /**
     * Dispose given session and remove it from manager context
     * @param session session to be disposed
     */
//    void disposeSession(KieSession session);
}
