package org.drools.core;

import java.util.Collection;

import org.kie.api.event.kiebase.KieBaseEventManager;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;

/**
 * The EventManager class is implemented by classes wishing to add,remove and get the various Drools EventListeners.
 */
public interface WorkingMemoryEventManager
    extends
    KieBaseEventManager {
    /**
     * Add an event listener.
     * 
     * @param listener
     *            The listener to add.
     */
    public void addEventListener(RuleRuntimeEventListener listener);

    /**
     * Remove an event listener.
     * 
     * @param listener
     *            The listener to remove.
     */
    public void removeEventListener(RuleRuntimeEventListener listener);

    /**
     * Returns all event listeners.
     * 
     * @return listeners The listeners.
     */
    public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners();

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

    /**
     * Returns all event listeners.
     * 
     * @return listeners The listeners.
     */
    public Collection<AgendaEventListener> getAgendaEventListeners();

}
