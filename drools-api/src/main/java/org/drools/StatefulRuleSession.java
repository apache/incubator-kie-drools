package org.drools;

import org.drools.time.SessionClock;

public interface StatefulRuleSession {
    FactHandle insertObject(Object object);
    void retractObject(FactHandle factHandle);
    void updateObject(FactHandle factHandle);
    void updateObject(FactHandle factHandle, Object object);
    
    void fireAllRules();
    
    void halt();
    
    /**
     * Returns the session clock instance associated with this session
     * @return
     */
    public SessionClock getSessionClock();    
}
