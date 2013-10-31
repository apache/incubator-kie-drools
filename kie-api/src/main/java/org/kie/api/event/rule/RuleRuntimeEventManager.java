package org.kie.api.event.rule;

import java.util.Collection;

public interface RuleRuntimeEventManager extends WorkingMemoryEventManager {
    /**
     * Add an event listener.
     *
     * @param listener
     *            The listener to add.
     */
    void addEventListener(RuleRuntimeEventListener listener);

    /**
     * Remove an event listener.
     *
     * @param listener
     *            The listener to remove.
     */
    void removeEventListener(RuleRuntimeEventListener listener);

    /**
     * Returns all event listeners.
     *
     * @return listeners The listeners.
     */
    Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners();

    /**
     * Add an event listener.
     *
     * @param listener
     *            The listener to add.
     */
    void addEventListener(AgendaEventListener listener);

    /**
     * Remove an event listener.
     *
     * @param listener
     *            The listener to remove.
     */
    void removeEventListener(AgendaEventListener listener);

    /**
     * Returns all event listeners.
     *
     * @return listeners The listeners.
     */
    Collection<AgendaEventListener> getAgendaEventListeners();
}
