package org.kie.kogito.process;


public interface ProcessError {

    String failedNodeId();
    
    String errorMessage();
    
    void retrigger();
    
    void skip();
}
