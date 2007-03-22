package org.drools.clp.functions;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.SlotNameValuePair;
import org.drools.clp.ValueHandler;
import org.mvel.PropertyAccessor;

public class ModifyFunction  implements Function {
    private static final String name = "modify"; 
    
    //private static final String modifyExpr
    
    public ModifyFunction() {
    }


    public Object execute(ValueHandler[] args, ExecutionContext context) {
        Object object = args[0].getValue( context );        
            for ( int i = 1, length = args.length; i < length; i++ ) {
                SlotNameValuePair pair = ( SlotNameValuePair ) args[i];                
                PropertyAccessor.set(object, pair.getName(), pair.getValueHandler().getValue( context ) );
            }                
        return null;
    }
    
    public String getName() {
        return name;
    }

}
