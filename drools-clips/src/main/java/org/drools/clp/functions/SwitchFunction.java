package org.drools.clp.functions;

import org.drools.clp.BuildContext;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm2;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.BaseValueHandler;
import org.drools.clp.valuehandlers.BooleanValueHandler;
import org.drools.clp.valuehandlers.FunctionCaller;
import org.drools.clp.valuehandlers.IndexedLocalVariableValue;
import org.drools.clp.valuehandlers.TempTokenVariable;

public class SwitchFunction extends BaseFunction
    implements
    Function {
    private static final String name = "switch";

    public SwitchFunction() {

    }
    
    public ValueHandler addParameterCallback(int index,
                                             FunctionCaller caller,
                                             ValueHandler valueHandler,
                                             BuildContext context) {
        if ( index == 0 ) {
            if ( !(valueHandler instanceof IndexedLocalVariableValue ) ) {
                // this should already be bound as a local variable
                throw new RuntimeException( "The variable must already have been declared to use it in a switch statement" );
            } else {
                context.setProperty( "switch-variable", valueHandler );
            }
        }
        
        caller.addParameter( valueHandler );

        return valueHandler;
    }     

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {        
        ValueHandler result = null;
        
        // binds the variable for the case function to use
        args[0].getValue( context );
        
        // now its bound we can execute each case statement in turn
        for (int i = 1, length = args.length; i < length; i++ ) {
            result = args[i].getValue( context );   
            if ( result == BaseValueHandler.BREAK ) {
                break;
            }
        }   
        
        return result;
    }

    public String getName() {
        return name;
    }
}
