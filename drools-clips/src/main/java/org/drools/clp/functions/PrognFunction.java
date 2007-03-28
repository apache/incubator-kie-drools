package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.ValueHandler;

public class PrognFunction extends BaseFunction
    implements
    Function {
    private static final String name = "progn";

    public PrognFunction() {

    }

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {

        ValueHandler result = null;
        for ( int i = 0, length = args.length; i < length; i++ ) {
            result = args[i].getValue( context );
        }

        return result;
    }

    public String getName() {
        return name;
    }
}
