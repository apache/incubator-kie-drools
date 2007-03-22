package org.drools.clp.functions;

import java.math.BigDecimal;

import org.drools.FactHandle;
import org.drools.clp.BaseFunction;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.ValueHandler;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.Extractor;

public class BindFunction implements Function {
    private static final String name = "bind";

    public BindFunction() {
        
    }
    public Object execute(ValueHandler[] args, ExecutionContext context) {
        args[0].setValue( context, args[1].getValue( context ) );        
        return args[0];    
    }
    
    public String getName() {
        return name;
    }

}
