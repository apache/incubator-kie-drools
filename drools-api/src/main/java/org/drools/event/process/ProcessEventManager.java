package org.drools.event.process;

import java.util.Collection;

public interface ProcessEventManager {
	
    /**
     * Add an event listener.
     * 
     * @param listener
     *            The listener to add.
     */
    public void addEventListener(ProcessEventListener listener);

    /**
     * Remove an event listener.
     * 
     * @param listener
     *            The listener to remove.
     */
    public void removeEventListener(ProcessEventListener listener);

    /**
     * Returns all event listeners.
     * 
     * @return listeners The listeners.
     */
    public Collection<ProcessEventListener> getProcessEventListeners();
    
}
