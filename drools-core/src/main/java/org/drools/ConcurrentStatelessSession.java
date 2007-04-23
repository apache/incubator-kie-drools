package org.drools;

import java.util.List;

import org.drools.event.AgendaEventListener;
import org.drools.spi.AgendaFilter;
import org.drools.spi.GlobalResolver;

public interface ConcurrentStatelessSession {    
    
    void asyncExecute(Object object);
    void asyncExecute(Object[] list);
    void asyncExecute(List list);
}
