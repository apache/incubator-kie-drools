package org.drools.clp;

import java.util.Map;

public interface Function { //extends ValueHandler {
    public Object execute(ValueHandler[] args, ExecutionContext context);    
    
    public String getName();    
    
    //public void addParameter(ValueHandler valueHandler);
    
    //public ValueHandler[] getParameters();                
}
