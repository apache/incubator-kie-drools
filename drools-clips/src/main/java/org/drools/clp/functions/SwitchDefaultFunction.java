package org.drools.clp.functions;

import org.drools.clp.ExecutionBuildContext;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.BooleanValueHandler;
import org.drools.clp.valuehandlers.FunctionCaller;
import org.drools.clp.valuehandlers.TempTokenVariable;

public class SwitchDefaultFunction extends BaseFunction
    implements
    Function {
    private static final String name = "default";

    public SwitchDefaultFunction() {

    }

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        return args[0].getValue( context );
    }

    public String getName() {
        return name;
    }
}
