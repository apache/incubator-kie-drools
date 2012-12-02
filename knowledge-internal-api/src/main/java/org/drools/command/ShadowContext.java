package org.drools.command;

public interface ShadowContext extends Context {
    
    ShadowWorld getContextManager();
    
    void set(String identifier,
             Object value,
             boolean shadow);
}
