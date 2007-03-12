package org.drools.clp;

import java.math.BigDecimal;

import org.drools.FactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.Extractor;

public class AddFunction extends BaseFunction implements Function {
    private static final String name = "+";

    public AddFunction() {
        
    }
    
    public AddFunction(ValueHandler[] parameters) {
        super( parameters );
    }

    public Object getValue(ExecutionContext context) {
        BigDecimal bdval = new BigDecimal(0);        
        ValueHandler[] args = getParameters();
        for ( int i = 0, length = args.length; i < length; i++ ) {
            bdval = bdval.add( args[i].getBigDecimalValue( context ) );
        }
                
        return bdval;
    }        

    public String getName() {
        return name;
    }

}
