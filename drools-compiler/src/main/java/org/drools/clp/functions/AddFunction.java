package org.drools.clp.functions;

import java.math.BigDecimal;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ObjectValueHandler;
import org.drools.clp.ValueHandler;

public class AddFunction extends BaseFunction implements Function {
    private static final String name = "+";

    public AddFunction() {
        
    }
    
    public ValueHandler execute(ValueHandler[] args, ExecutionContext context) {
        BigDecimal bdval = new BigDecimal(0);        
        for ( int i = 0, length = args.length; i < length; i++ ) {
            bdval = bdval.add( args[i].getBigDecimalValue( context ) );
        }                
        return new ObjectValueHandler( new BigDecimal( bdval.intValue() ) );     
    }    
    
    
    public String getName() {
        return name;
    }    
}
