package org.drools.lang.descr;

public class AccessorDescr extends DeclarativeInvokerDescr { 
    private String variableName;
    private DeclarativeInvokerDescr[] invokers;
    public AccessorDescr(String methodName,
                         DeclarativeInvokerDescr[] accessors) {
        super();
        this.variableName = methodName;
        this.invokers = accessors;
    }
    
    public DeclarativeInvokerDescr[] getInvokers() {
        return this.invokers;
    }
    
    public void setInvokers(DeclarativeInvokerDescr[] accessors) {
        this.invokers = accessors;
    }
    
    public String getVariableName() {
        return this.variableName;
    }
    
    public void setVariableName(String methodName) {
        this.variableName = methodName;
    }
    
    

}
