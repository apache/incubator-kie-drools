package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.BooleanValueHandler;

public class SwitchCaseFunction extends BaseFunction
    implements
    Function {
    private static final String name = "switch";

    public SwitchCaseFunction() {

    }

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        ValueHandler value = args[0].getValue( context );
        
        ValueHandler result = null;
        
        //ValueHandler
        
        for (int j = 2, length = args.length; j < length; j++ ) {
            
            
            // iterate for each action
            //result = args[j].getValue( context );
            
        }   
        
        return result;
        
        /*
        if ( result ) {
            return args[2].getValue( context );
        } else if ( args[3] != null && args[4] != null ) {
            return args[4].getValue( context );
        } else {
            return new BooleanValueHandler( result );
        }
        */
    }

    public String getName() {
        return name;
    }
}
