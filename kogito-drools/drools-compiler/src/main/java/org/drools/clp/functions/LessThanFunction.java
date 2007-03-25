package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.BooleanValueHandler;

public class LessThanFunction extends BaseFunction
    implements
    Function {
    private static final String name = "<";

    public LessThanFunction() {

    }

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        return new BooleanValueHandler( args[0].getBigDecimalValue( context ).compareTo( args[1].getBigDecimalValue( context ) ) < 0 );
    }

    public String getName() {
        return name;
    }

}
