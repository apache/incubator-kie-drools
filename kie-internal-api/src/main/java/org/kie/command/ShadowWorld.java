package org.kie.command;


public interface ShadowWorld extends World {
    //public ShadowContext createContext(String identifier);
    
    public ShadowContext getContext(String identifier); 
    
    //public ExecutionResults getExecutionResults();
}
