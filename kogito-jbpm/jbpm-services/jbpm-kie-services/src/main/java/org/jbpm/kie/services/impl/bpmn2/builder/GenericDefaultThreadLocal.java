package org.jbpm.kie.services.impl.bpmn2.builder;


/**
 * This is a generic extension of the {@link ThreadLocal} class that makes sure that an intial value 
 * is available for each call of {@link ThreadLocal#get()}. 
 * 
 * @see {@link ThreadLocal}
 * @param <T> The initial value class
 */
public class GenericDefaultThreadLocal<T> extends ThreadLocal<T> {

    private final T defaultInstance;
    
    public GenericDefaultThreadLocal(T defaultInstance) { 
       this.defaultInstance = defaultInstance; 
    }

    @Override
    protected T initialValue() {
        return defaultInstance;
    }
    
}
