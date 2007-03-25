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

        ValueHandler doHandler = args[args.length - 1];

        while ( args[0].getBooleanValue( context ) ) {
            result = doHandler.getValue( context );
        }

        return result;
    }

    public String getName() {
        return name;
    }

}
