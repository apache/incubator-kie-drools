package org.drools.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.base.MapGlobalResolver;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleFlowEventSupport;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.process.instance.ProcessInstanceFactory;
import org.drools.process.instance.WorkItemManager;
import org.drools.rule.TimeMachine;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.GlobalResolver;
import org.drools.temporal.SessionClock;

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
