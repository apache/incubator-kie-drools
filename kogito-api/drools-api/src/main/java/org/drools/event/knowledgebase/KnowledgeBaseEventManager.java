package org.drools.event.knowledgebase;

import java.util.Collection;
import java.util.List;

public interface KnowledgeBaseEventManager {
    /**
     * Add an event listener.
     * 
     * @param listener
     *            The listener to add.
     */
    public void addEventListener(KnowledgeBaseEventListener listener);

    /**
     * Remove an event listener.
     * 
     * @param listener
     *            The listener to remove.
     */
    public void removeEventListener(KnowledgeBaseEventListener listener);

    /**
     * Returns all event listeners.
     * 
     * @return listeners The listeners.
     */
    public Collection<KnowledgeBaseEventListener> getKnowledgeBaseEventListeners();
}
