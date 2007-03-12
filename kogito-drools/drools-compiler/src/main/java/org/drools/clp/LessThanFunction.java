package org.drools.clp;

import java.math.BigDecimal;

import org.drools.FactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.Extractor;

public class LessThanFunction extends BaseFunction implements Function {
    private static final String name = "<";

    public LessThanFunction() {
        
    }
    
    public LessThanFunction(ValueHandler[] parameters) {
        super( parameters );
    }

    public Object getValue(ExecutionContext context) {
        ValueHandler[] args = getParameters();                
        return new Boolean( args[1].getBigDecimalValue( context ).compareTo( args[2].getBigDecimalValue( context ) ) < 0 );
    }
    
    public boolean getBoolean(ExecutionContext context) {
        ValueHandler[] args = getParameters();                
        return args[1].getBigDecimalValue( context ).compareTo( args[2].getBigDecimalValue( context ) ) < 0;
    }

    public String getName() {
        return name;
    }

}
