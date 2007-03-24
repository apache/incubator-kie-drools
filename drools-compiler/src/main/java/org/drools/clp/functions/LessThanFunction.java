package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;

public class LessThanFunction extends BaseFunction  implements Function {
    private static final String name = "<";

    public LessThanFunction() {
        
    }   

    public Object execute(ValueHandler[] args, ExecutionContext context) {
        return new Boolean( args[0].getBigDecimalValue( context ).compareTo( args[1].getBigDecimalValue( context ) ) < 0 );    
    }
    
    public String getName() {
        return name;
    }
    
}
