package org.drools.clp.functions;

import org.drools.clp.BuildContext;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.BaseValueHandler;
import org.drools.clp.valuehandlers.BooleanValueHandler;
import org.drools.clp.valuehandlers.FunctionCaller;
import org.drools.clp.valuehandlers.TempTokenVariable;

public class SwitchCaseFunction extends BaseFunction
    implements
    Function {
    private static final String name = "case";

    public SwitchCaseFunction() {

    }

    public ValueHandler addParameterCallback(int index,
                                             FunctionCaller caller,
                                             ValueHandler valueHandler,
                                             BuildContext context) {
        if ( index == 0 ) {
            // swap the element for an equality check        
            FunctionCaller switchCaller  = new FunctionCaller( context.getFunctionRegistry().getFunction( "eq" ) );
            switchCaller.addParameter( (ValueHandler) context.getProperty( "switch-variable"  ) );
            switchCaller.addParameter( valueHandler );
            valueHandler = switchCaller;
        }
        
        caller.addParameter( valueHandler );
        
        return valueHandler;
    }      
    
    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        ValueHandler result = null;
        
        if ( args[0].getBooleanValue( context ) ) {
            for (int i = 2, length = args.length; i < length; i++ ) {
                result = args[i].getValue( context );   
                if ( result == BaseValueHandler.BREAK ) {
                    break;
                }
            }
        } else {
            result = new BooleanValueHandler( false );
        }
        
        return result;
    }

    public String getName() {
        return name;
    }
}
