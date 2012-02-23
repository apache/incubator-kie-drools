package org.drools.command;

import org.drools.runtime.ExecutionResults;

public interface ShadowWorld extends World {
    public ShadowContext createContext(String identifier);
    
    public ShadowContext getContext(String identifier); 
    
    public ExecutionResults getExecutionResults();
}
