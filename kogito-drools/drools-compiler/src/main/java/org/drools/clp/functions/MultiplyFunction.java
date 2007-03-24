package org.drools.clp.functions;

import java.math.BigDecimal;

import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispForm;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;

public class MultiplyFunction implements Function {
    private static final String name = "*";

    public MultiplyFunction() {
        
    }
    
    public Object execute(ValueHandler[] args, ExecutionContext context) {
        BigDecimal bdval = new BigDecimal(0);        
        for ( int i = 0, length = args.length; i < length; i++ ) {
            bdval = bdval.multiply( args[i].getBigDecimalValue( context ) );
        }                
        return bdval;        
    }
    
    
    public String getName() {
        return name;
    }
    
    public LispList createList(int index) {
        return new LispForm();
    }    
    
    public String toString() {
        return "[Function '" + getName() + "']";
    }

}
