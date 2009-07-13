package org.drools.command;

public interface Context {
    
    ContextManager getContextManager();
    
    String getName();
    
    Object get(String identifier);

    void set(String identifier,
             Object value);    
}
