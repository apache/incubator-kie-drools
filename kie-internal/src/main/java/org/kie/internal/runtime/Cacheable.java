package org.kie.internal.runtime;

/**
 * Allows various components (e.g. work item handlers, event listeners) to be cached and reused
 * within a cache owner (e.g. RuntimeManager) and closed whenever the owner is closed/disposed.
 *
 */
public interface Cacheable {


    /**
     * Closes underlying resources when cache is closed/disposed.
     */
    void close();
}
