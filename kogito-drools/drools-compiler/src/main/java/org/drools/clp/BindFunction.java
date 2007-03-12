package org.drools.clp;

import java.math.BigDecimal;

import org.drools.FactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.Extractor;

public class BindFunction extends BaseFunction implements Function {
    private static final String name = "bind";

    public BindFunction() {
        
    }
    
    public BindFunction(ValueHandler[] parameters) {
        super( parameters );
    }

    public Object getValue(ExecutionContext context) {
        ValueHandler args[] = getParameters();
        
        args[0].setValue( context, args[1].getValue( context ) );
        
        return args[0];
    }        

    public String getName() {
        return name;
    }

}
