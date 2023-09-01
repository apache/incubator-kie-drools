package org.kie.api.runtime;

/**
 * The context of an execution
 */
public interface Context {

    /**
     * The unique name of this context
     */
    String getName();

    /**
     * Return the value associated with the given identifier in this context
     */
    Object get(String identifier);

    /**
     * Set a value on this context with this given identifier
     */
    void set(String identifier,
             Object value);

    /**
     * Remove the value associated with the given identifier in this context
     */
    void remove(String identifier);

    /**
     * Return true if the given identifier has an associated value in this context
     */
    boolean has(String identifier);
}
