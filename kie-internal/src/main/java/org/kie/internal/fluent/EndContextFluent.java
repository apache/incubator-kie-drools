package org.kie.internal.fluent;

/**
 * These methods are responsible for the logic associated with ending a context.
 * 
 */
public interface EndContextFluent<T> {
    
    T end(String context, String name);
    T end(String name);
    T end();
}
