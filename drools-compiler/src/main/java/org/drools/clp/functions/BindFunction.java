package org.drools.clp.functions;

import org.drools.clp.ExecutionBuildContext;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.FunctionCaller;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.TempTokenVariable;
import org.drools.clp.ValueHandler;
import org.drools.clp.VariableValueHandler;

public class BindFunction extends BaseFunction implements Function {
    private static final String name = "bind";

    public BindFunction() {
        
    }

    public ValueHandler addParameterCallback(int index, ValueHandler valueHandler, ExecutionBuildContext context ) {
        // The first index in the 'bind' function is the variable
        // register the variable, if it doesn't already exist, will be a TempTokenVariable if it does not already exist
        if ( index == 0 && ( valueHandler instanceof TempTokenVariable ) ) {
            TempTokenVariable temp = ( TempTokenVariable ) valueHandler;
            valueHandler= context.createLocalVariable( temp.getIdentifier() );
        } 
        
        return valueHandler;
    }
    
    public ValueHandler execute(ValueHandler[] args, ExecutionContext context) {
        args[0].setValue( context,  args[1].getValue( context ));
//        Object object = args[1].getValue( context );
//        if ( object instanceof FunctionCaller  || object instanceof VariableValueHandler) {
//            // this is if the paramter is a variable or a function, so we must resolve further
//            args[0].setValue( context,  ( ( ValueHandler ) object).getValue( context ));
//        } else {
//            // thi sis if the parameter 
//            args[0].setValue( context,  object);
//        }
        return args[0];    
    }
    
    public String getName() {
        return name;
    }

}
