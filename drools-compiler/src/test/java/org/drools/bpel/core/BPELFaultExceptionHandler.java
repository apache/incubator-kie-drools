package org.drools.bpel.core;

import org.drools.process.core.context.exception.ExceptionHandler;

public class BPELFaultExceptionHandler implements ExceptionHandler {

    private String faultName;
    
    public String getFaultName() {
        return faultName;
    }

    public void setFaultName(String faultName) {
        this.faultName = faultName;
    }

    public void handleException(String exception, Object params) {
        
    }

}
