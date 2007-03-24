package org.drools.clp.functions;

import org.drools.clp.ExecutionBuildContext;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
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
    
    public Object execute(ValueHandler[] args, ExecutionContext context) {
        args[0].setValue( context, args[1].getValue( context ) );        
        return args[0];    
    }
    
    public String getName() {
        return name;
    }

}
