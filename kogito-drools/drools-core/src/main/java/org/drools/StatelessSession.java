package org.drools;

import java.util.List;

import org.drools.event.AgendaEventListener;
import org.drools.spi.AgendaFilter;
import org.drools.spi.GlobalResolver;

public interface StatelessSession {
    /**
     * Returns all event listeners.
     * 
     * @return listeners The listeners.
     */
    public List getWorkingMemoryEventListeners();

    /**
     * Add an event listener.
     * 
     * @param listener
     *            The listener to add.
     */
    public void addEventListener(AgendaEventListener listener);

    /**
     * Remove an event listener.
     * 
     * @param listener
     *            The listener to remove.
     */
    public void removeEventListener(AgendaEventListener listener);    
    
    
    void setAgendaFilter(AgendaFilter agendaFilter);
    
    /**
     * Delegate used to resolve any global names not found in the global map.
     * @param globalResolver
     */
    void setGlobalResolver(GlobalResolver globalResolver);
    
    void setGlobal(String identifer, Object value);        
    
    
    void execute(Object object);
   
    void execute(Object[] list);
    
    void execute(List list);    
    
    void asyncExecute(Object object);
    
    void asyncExecute(Object[] list);
    
    void asyncExecute(List list);      
    
    StatelessSessionResult executeWithResults(Object object);
   
    StatelessSessionResult executeWithResults(Object[] list);
    
    StatelessSessionResult executeWithResults(List list);
}
