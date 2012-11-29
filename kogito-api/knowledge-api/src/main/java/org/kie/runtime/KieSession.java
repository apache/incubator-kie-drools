package org.kie.runtime;

public interface KieSession extends KieRuleSession {
    
    int getId();
    
    /**
     * Releases all the current session resources, setting up the session for garbage collection.
     * This method <b>must</b> always be called after finishing using the session, or the engine
     * will not free the memory used by the session.
     */
    void dispose();
}
