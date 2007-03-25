package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.ValueHandler;

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
            }
        }

        return result;
    }

    public String getName() {
        return name;
    }

}
