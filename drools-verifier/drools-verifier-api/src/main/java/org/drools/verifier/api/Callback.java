package org.drools.verifier.api;

/**
 * Generic callback
 */
public interface Callback<T> {

    void callback(final T result);
}
