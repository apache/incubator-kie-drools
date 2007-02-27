package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.base.DroolsMVELFactory;
import org.drools.rule.Declaration;
import org.drools.spi.EvalExpression;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

public class MVELPredicateExpression
    implements
    PredicateExpression {
    
    private final Serializable expr;
    private final DroolsMVELFactory factory;
    
    public MVELPredicateExpression(final Serializable expr,
                              final DroolsMVELFactory factory) {
        this.expr = expr;
        this.factory = factory;
    }

    public boolean evaluate(Object object, 
                            Tuple tuple,
                            Declaration[] previousDeclarations,
                            Declaration[] requiredDeclarations,
                            WorkingMemory workingMemory) throws Exception {                
        factory.setContext( tuple, workingMemory );   
        Boolean result = ( Boolean ) MVEL.executeExpression(this.expr, factory);        
        return result.booleanValue(); 
    }

}
