package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.BooleanValueHandler;

public class IfFunction extends BaseFunction
    implements
    Function {
    private static final String name = "if";

    public IfFunction() {

    }

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        boolean result = args[0].getBooleanValue( context );
        if ( result ) {
            return args[2].getValue( context );
        } else if ( args[3] != null && args[4] != null ) {
            return args[4].getValue( context );
        } else {
            return new BooleanValueHandler( result );
        }
    }

    public String getName() {
        return name;
    }

    public LispList createList(int index) {
        return new LispForm();
    }

    public String toString() {
        return "[Function '" + getName() + "']";
    }
}
