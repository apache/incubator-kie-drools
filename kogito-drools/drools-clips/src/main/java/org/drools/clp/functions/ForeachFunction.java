package org.drools.clp.functions;

import org.drools.clp.ExecutionBuildContext;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.BaseValueHandler;
import org.drools.clp.valuehandlers.ListValueHandler;
import org.drools.clp.valuehandlers.TempTokenVariable;

public class ForeachFunction extends BaseFunction
    implements
    Function {
    private static final String name = "foreach";

    public ForeachFunction() {

    }
    
    public ValueHandler addParameterCallback(int index,
                                             ValueHandler valueHandler,
                                             ExecutionBuildContext context) {
        // The first index in the 'foreach' function is the variable
        // register the variable, if it doesn't already exist, will be a TempTokenVariable if it does not already exist
        if ( index == 0 && (valueHandler instanceof TempTokenVariable) ) {
            TempTokenVariable temp = (TempTokenVariable) valueHandler;
            valueHandler = context.createLocalVariable( temp.getIdentifier() );
        }

        return valueHandler;
    }    

    public ValueHandler execute(ValueHandler[] args,
                                ExecutionContext context) {
        ValueHandler result = null;
        
        BindFunction bind = new BindFunction();
        ValueHandler[] bindArgs = new ValueHandler[2];
        bindArgs[0] = args[0];
  
        // Check if the arg is a List or a variable resolving to a list
        ValueHandler value = args[1];
        if ( !(value instanceof ListValueHandler ) ) {
           value = value.getValue( context ); 
        }
        ValueHandler[] list  = (( ListValueHandler ) value).getList();
      
        for (int i = 0, length1 = list.length; i < length1; i++ ) {
            bindArgs[1] = list[i];
            bind.execute( bindArgs, context );
            for (int j = 2, length2 = args.length; j < length2; j++ ) {
                // iterate for each action
                result = args[j].getValue( context );
                if ( result == BaseValueHandler.BREAK ) {
                    break;
                }
            }
            if ( result == BaseValueHandler.BREAK ) {
                // need to do this twice as its a nested loop here, a single loop in the lisp
                break;
            }            
        }

        return result;
    }

    public String getName() {
        return name;
    }

}
