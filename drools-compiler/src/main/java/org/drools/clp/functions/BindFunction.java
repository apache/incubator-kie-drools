package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;

public class BindFunction implements Function {
    private static final String name = "bind";

    public BindFunction() {
        
    }
    public Object execute(ValueHandler[] args, ExecutionContext context) {
        args[0].setValue( context, args[1].getValue( context ) );        
        return args[0];    
    }
    
    public LispList createList(int index) {
        return new LispForm();
    }    
    
    public String getName() {
        return name;
    }

    public String toString() {
        return "[Function '" + getName() + "']";
    }
}
