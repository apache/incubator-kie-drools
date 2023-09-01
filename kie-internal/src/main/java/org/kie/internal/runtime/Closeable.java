package org.kie.internal.runtime;

/**
 * Allows various components (e.g. work item handlers, event listeners) to be closed when
 * owning component (ksession) is being closed/disposed.
 * This interface marks an component that is lightweight and it's safe and wise (from performance
 * point of view) to be frequently recreated.
 *
 * @see Cacheable is an alternative that allows to keep single instnace te be cached and reused to avoid recreation
 */
public interface Closeable {

    /**
     * Closes the underlying resources
     */
    void close();
}
