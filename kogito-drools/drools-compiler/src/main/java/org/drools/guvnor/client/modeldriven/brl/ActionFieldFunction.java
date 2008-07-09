package org.drools.guvnor.client.modeldriven.brl;

/**
 * This node indicates that the user wants to execute a method on some
 * fact in case the LHR matches.
 * 
 * @author isabel
 * */
public class ActionFieldFunction extends ActionFieldValue {
    
    volatile String method = "";
    
    public ActionFieldFunction() {
    }
    
    public ActionFieldFunction(final String field, final String value, final String type) {
        super(field, value, type);
    }
    
    public void setMethod(final String methodName) {
        this.method = methodName;
    }

    public String getMethod() {
        return this.method;
    }
}
