package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ObjectValueHandler;
import org.drools.clp.ValueHandler;

public class WhileFunction extends BaseFunction implements Function {
    private static final String name = "while";

    public WhileFunction() {
        
    }
    
    public ValueHandler execute(ValueHandler[] args, ExecutionContext context) {
        Object result = null;
        
        ValueHandler doHandler = args[ args.length - 1 ];
        
        while( args[0].getBooleanValue( context ) ) {
            result = doHandler.getValue( context );
        }

        return new ObjectValueHandler( result ); 
    }    
    
    
    public String getName() {
        return name;
    }    

}
