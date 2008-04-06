package org.drools.bpel.core;

import org.drools.process.core.context.exception.ExceptionHandler;

/**
 * A BPEL FaultHandler.
 * A catchAll faultHandler has a null faultName.
 *  
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELFaultHandler implements ExceptionHandler {
    
    private String faultName;
    private String faultVariable;
    private BPELActivity activity;
    
    public String getFaultName() {
        return faultName;
    }
    
    public void setFaultName(String faultName) {
        this.faultName = faultName;
    }
    
    public String getFaultVariable() {
        return faultVariable;
    }
    
    public void setFaultVariable(String faultVariable) {
        this.faultVariable = faultVariable;
    }
    
    public BPELActivity getActivity() {
        return activity;
    }
    
    public void setActivity(BPELActivity activity) {
        this.activity = activity;
    }

}
