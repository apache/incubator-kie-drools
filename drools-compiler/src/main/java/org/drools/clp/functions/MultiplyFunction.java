package org.drools.clp.functions;

import java.math.BigDecimal;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;
import org.drools.clp.valuehandlers.ObjectValueHandler;

public class MultiplyFunction extends BaseFunction  implements Function {
    private static final String name = "*";

    public MultiplyFunction() {
        
    }
    
    public ValueHandler execute(ValueHandler[] args, ExecutionContext context) {
        BigDecimal bdval = new BigDecimal(0);        
        for ( int i = 0, length = args.length; i < length; i++ ) {
            bdval = bdval.multiply( args[i].getBigDecimalValue( context ) );
        }                
        return new ObjectValueHandler( bdval );        
    }
    
    
    public String getName() {
        return name;
    }    

}
