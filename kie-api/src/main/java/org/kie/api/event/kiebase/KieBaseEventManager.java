package org.kie.api.event.kiebase;

import java.util.Collection;

public interface KieBaseEventManager {
    /**
     * Add an event listener.
     *
     * @param listener
     *            The listener to add.
     */
    void addEventListener(KieBaseEventListener listener);

    /**
     * Remove an event listener.
     *
     * @param listener
     *            The listener to remove.
     */
    void removeEventListener(KieBaseEventListener listener);

    /**
     * Returns all event listeners.
     *
     * @return listeners The listeners.
     */
    Collection<KieBaseEventListener> getKieBaseEventListeners();
}
