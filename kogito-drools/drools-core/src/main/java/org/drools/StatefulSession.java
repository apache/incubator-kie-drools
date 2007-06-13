package org.drools;

import java.util.List;

import org.drools.concurrent.Future;
import org.drools.spi.AgendaFilter;

public interface StatefulSession extends WorkingMemory {
    
    /**
     * Forces the workingMemory to be derefenced from
     * 
     */
    void dispose();    
    
    Future asyncInsert(Object object);    
    Future asyncRetract(FactHandle factHandle);   
    Future asyncUpdate(FactHandle factHandle, Object object);

    Future asyncInsert(Object[] list);
    Future asyncInsert(List list);
    
    Future asyncFireAllRules();
    Future asyncFireAllRules(AgendaFilter agendaFilter); 

}
