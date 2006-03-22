package org.drools.compiler;

import org.drools.lang.descr.FunctionDescr;

public class FunctionError extends DroolsError {
    private FunctionDescr functionDescr;
    private Object       object;
    private String       message;
    
    public FunctionError(FunctionDescr functionDescr,
                     Object object,
                     String message) {
        super();
        this.functionDescr = functionDescr;
        this.object = object;
        this.message = message;
    }    

    public FunctionDescr getFunctionDescr() {
        return this.functionDescr;
    }
       
    public Object getObject() {
        return this.object;
    }

    public String getMessage() {
        return this.message;
    }
            
}
