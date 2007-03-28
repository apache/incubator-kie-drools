package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.BaseValueHandler;

public class WhileFunction extends BaseFunction
    implements
    Function {
    private static final String name = "while";

    public WhileFunction() {

    }

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        ValueHandler result = null;

        ValueHandler doLoop = args[0];
        
        while ( doLoop.getBooleanValue( context ) ) {
            for (int i = 2, length = args.length; i < length; i++ ) {
                // iterate for each action                
                result = args[i].getValue( context );
                if ( result == BaseValueHandler.BREAK ) {
                    break;
                }                  
            }
            if ( result == BaseValueHandler.BREAK ) {
                // need to do this twice as its a nested loop here, a single loop in the lisp
                break;
            }              
        }

        return result;
    }

    public String getName() {
        return name;
    }

}
