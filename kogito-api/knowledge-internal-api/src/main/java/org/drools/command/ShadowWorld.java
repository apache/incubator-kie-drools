package org.drools.core.command;

import org.drools.core.runtime.ExecutionResults;

public interface ShadowWorld extends World {
    //public ShadowContext createContext(String identifier);
    
    public ShadowContext getContext(String identifier); 
    
    //public ExecutionResults getExecutionResults();
}
