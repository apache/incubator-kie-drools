package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.BaseValueHandler;
import org.drools.clp.valuehandlers.BooleanValueHandler;

public class BreakFunction extends BaseFunction
    implements
    Function {
    private static final String name = "break";

    public BreakFunction() {

    }

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        return BaseValueHandler.BREAK;
    }

    public String getName() {
        return name;
    }
}
