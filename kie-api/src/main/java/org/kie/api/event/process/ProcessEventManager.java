package org.kie.api.event.process;

import java.util.Collection;

/**
 * A manager for process related events.
 */
public interface ProcessEventManager {

    /**
     * Add a process event listener.
     *
     * @param listener the listener to add.
     */
    public void addEventListener(ProcessEventListener listener);

    /**
     * Remove a process event listener.
     *
     * @param listener the listener to remove
     */
    public void removeEventListener(ProcessEventListener listener);

    /**
     * Returns all event listeners.
     *
     * @return listeners the listeners
     */
    public Collection<ProcessEventListener> getProcessEventListeners();

}
