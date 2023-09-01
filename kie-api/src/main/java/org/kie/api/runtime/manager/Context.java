package org.kie.api.runtime.manager;

/**
 * Context represents to highest level hierarchy of contextual data that might be used
 * when working with <code>RuntimeManager</code> that is then relying on the context to look up correct
 * instances of <code>RuntimeEngine</code>.
 *
 * @param <T> - represents the actual type of data context implements as an identifier
 */
public interface Context<T> {

    /**
     * See concrete implementation for available types
     *
     * @return the id of context which is specific to the type of context.
     */
    T getContextId();
}
