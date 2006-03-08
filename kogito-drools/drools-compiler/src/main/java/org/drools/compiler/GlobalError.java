package org.drools.compiler;

public class GlobalError extends DroolsError {
    private String global;
    
    public GlobalError(String global) {
        this.global = global;
    }
    
    public String getGlobal() {
        return this.global;
    }

}
