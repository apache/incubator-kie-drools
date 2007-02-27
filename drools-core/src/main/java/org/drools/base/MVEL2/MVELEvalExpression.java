package org.drools.base.MVEL2;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.base.DroolsMVELFactory;
import org.drools.rule.Declaration;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

public class MVELEvalExpression
    implements
    EvalExpression {
    
    private final Serializable expr;
    private final DroolsMVELFactory factory;
    
    public MVELEvalExpression(final Serializable expr,
                              final DroolsMVELFactory factory) {
        this.expr = expr;
        this.factory = factory;
    }

    public boolean evaluate(Tuple tuple,
                            Declaration[] requiredDeclarations,
                            WorkingMemory workingMemory) throws Exception {                
        factory.setContext( tuple, workingMemory );   
        Boolean result = ( Boolean ) MVEL.executeExpression(this.expr, factory);        
        return result.booleanValue(); 
    }

}
