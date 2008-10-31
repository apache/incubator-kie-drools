package org.drools.common;


import org.drools.spi.FactHandleFactory;
import org.drools.time.SessionClock;

public class SharedTemporalWorkingMemoryContext<T extends SessionClock>  extends SharedWorkingMemoryContext {    
    protected T                                   sessionClock;
    
    public SharedTemporalWorkingMemoryContext(FactHandleFactory handleFactory, T sessionClock) {
        super( handleFactory );        
        this.sessionClock = sessionClock;
    }

    public T getSessionClock() {
        return sessionClock;
    }
    
            
    
}
