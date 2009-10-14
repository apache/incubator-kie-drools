package org.drools.command;


public interface ContextManager {
    public Context getContext(String identifier);
    
    public Context getDefaultContext();
}
