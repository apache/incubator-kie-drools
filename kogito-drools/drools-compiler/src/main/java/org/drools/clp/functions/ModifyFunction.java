package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.FunctionCaller;
import org.drools.clp.SlotNameValuePair;
import org.drools.clp.ValueHandler;
import org.mvel.PropertyAccessor;

public class ModifyFunction  implements Function {
    private static final String name = "modify";    
    
    public ModifyFunction() {
    }


    public Object execute(ValueHandler[] args, ExecutionContext context) {
        Object object = args[0].getValue( context );        
            for ( int i = 1, length = args.length; i < length; i++ ) {
                FunctionCaller pair = ( FunctionCaller ) args[i];                
                PropertyAccessor.set(object, pair.getName(), pair.getParameters()[0].getValue( context ) );
            }                
        return null;
    }
    
    public String getName() {
        return name;
    }

}
