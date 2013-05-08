package org.kie.internal.fluent;


public interface ContextFluent<T>{

    /**
     * The last executed command, if it returns a value, is set to a name in this executing context.
     * @param name
     * @return this
     */
    T set(String name);
    
    /**
     * Indicates that output from the last command should be returned (default is no).
     * </p>
     * A call to this method <i>must</i> follow a call to {@link #set(String)} method in order to 
     * set the name for the result.
     * @return this
     */
    T out();
    
    /**
     * Indicates that the output from the last executed command should be returned and set to the given name in the context
     * @param name
     * @return this
     */
    T out(String name);
} 
