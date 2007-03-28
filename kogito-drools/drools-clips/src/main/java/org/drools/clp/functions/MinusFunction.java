package org.drools.clp.functions;

import java.math.BigDecimal;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.ObjectValueHandler;

public class MinusFunction extends BaseFunction
    implements
    Function {
    private static final String name = "-";

    public MinusFunction() {

    }

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        BigDecimal bdval = args[0].getBigDecimalValue( context );
        for ( int i = 1, length = args.length; i < length; i++ ) {
            bdval = bdval.subtract( args[i].getBigDecimalValue( context ) );
        }

        return new ObjectValueHandler( bdval );
    }

    public String getName() {
        return name;
    }
}
