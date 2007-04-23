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
    
    Future asyncAssertObject(Object object);    
    Future asyncRetractObject(FactHandle factHandle);   
    Future asyncModifyObject(FactHandle factHandle, Object object);

    Future asyncAssertObjects(Object[] list);
    Future asyncAssertObjects(List list);
    
    Future asyncFireAllRules();
    Future asyncFireAllRules(AgendaFilter agendaFilter); 

}
