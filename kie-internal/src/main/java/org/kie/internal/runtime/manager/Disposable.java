package org.kie.internal.runtime.manager;

/**
 * Marker interface that indicates that given class is disposable - meaning shall be
 * manually or automatically disposed on close events.
 *
 */
public interface Disposable {

    /**
     * Actual logic that shall be executed on dispose.
     */
    void dispose();

    /**
     * Allows to register listeners to be notified whenever instance is disposed so dependent
     * instances can take proper action on that occasion.
     * @param listener callback listener instance
     */
    void addDisposeListener(DisposeListener listener);
}
