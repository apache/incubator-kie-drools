package org.drools.core.command;

public interface ShadowContext extends Context {
    
    ShadowWorld getContextManager();
    
    void set(String identifier,
             Object value,
             boolean shadow);
}
