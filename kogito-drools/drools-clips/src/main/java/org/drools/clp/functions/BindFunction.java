package org.drools.clp.functions;

import org.drools.clp.BuildContext;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.FunctionCaller;
import org.drools.clp.valuehandlers.TempTokenVariable;

public class BindFunction extends BaseFunction
    implements
    Function {
    private static final String name = "bind";

    public BindFunction() {

    }

    public ValueHandler addParameterCallback(int index,
                                             FunctionCaller caller,
                                             ValueHandler valueHandler,
                                             BuildContext context) {
        // The first index in the 'bind' function is the variable
        // register the variable, if it doesn't already exist, will be a TempTokenVariable if it does not already exist
        if ( index == 0 && (valueHandler instanceof TempTokenVariable) ) {
            TempTokenVariable temp = (TempTokenVariable) valueHandler;
            valueHandler = context.createLocalVariable( temp.getIdentifier() );
        }

        caller.addParameter( valueHandler );
        
        return valueHandler;
    }

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        ValueHandler nested = args[1].getValue( context );
        ValueHandler value = (nested != null ) ? nested : args[1];
    
        args[0].setValue( context,
                          value );
        return args[0];
    }

    public String getName() {
        return name;
    }

}
