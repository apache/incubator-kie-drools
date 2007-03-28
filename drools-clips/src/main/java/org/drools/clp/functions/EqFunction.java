package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.BooleanValueHandler;

public class EqFunction extends BaseFunction
    implements
    Function {
    private static final String name = "eq";

    public EqFunction() {

    }

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {

        return new BooleanValueHandler( args[0].equals( args[1], context ) );
    }

    public String getName() {
        return name;
    }
}
