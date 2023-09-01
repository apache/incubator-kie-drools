package org.kie.dmn.api.core.event;

import org.kie.dmn.api.core.DMNRuntime;

import java.util.Set;

/**
 * A runtime event manager interface for DMN
 */
public interface DMNRuntimeEventManager {

    /**
     * Registers a runtime listener
     *
     * @param listener listener to register
     */
    void addListener(DMNRuntimeEventListener listener);

    /**
     * Removes a runtime listener
     *
     * @param listener listener to remove
     */
    void removeListener(DMNRuntimeEventListener listener);

    /**
     * Returns the set of all registered listeners
     *
     * @return set of all registered listeners
     */
    Set<DMNRuntimeEventListener> getListeners();

    /**
     * Returns true if there are registered listeners, false otherwise
     *
     * @return
     */
    boolean hasListeners();

    DMNRuntime getRuntime();

}
