package org.drools.clp.functions;

import org.drools.clp.ExecutionBuildContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;

public abstract class BaseFunction implements Function {  
    
    public void initCallback(ExecutionBuildContext context) {
        
    }
    
    public ValueHandler addParameterCallback(int index, ValueHandler valueHandler, ExecutionBuildContext context ) {
        return valueHandler;
    }
    
    public LispList createList(int index) {
        return new LispForm();
    }
    
    public String toString() {
        return "[Function '" + getName() + "']";
    }
}
