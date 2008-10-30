package org.drools.runtime.rule;

import java.util.Collection;

import org.drools.event.rule.WorkingMemoryEventManager;
import org.drools.runtime.ObjectFilter;
import org.drools.time.SessionClock;

public interface WorkingMemory extends WorkingMemoryEventManager, WorkingMemoryEntryPoint {
    FactHandle insert(Object object);
    
    void retract(FactHandle factHandle);
    
    void update(FactHandle factHandle);
    
    void update(FactHandle factHandle, Object object);
    
    void halt();
    /**
     * Returns the session clock instance associated with this session
     * @return
     */
    public SessionClock getSessionClock();    
                        
    FactHandle getFactHandle(Object object);
    
    Collection<?> getObjects();

    Collection<?> getObjects(ObjectFilter filter);

    Collection<? extends FactHandle> getFactHandles();

    Collection<? extends FactHandle> getFactHandles(ObjectFilter filter);   
}
