package org.drools.rule;

import org.drools.WorkingMemory;
import org.drools.spi.Constraint;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;

public class EvalCondition {    
    private final EvalExpression           eval;    
   
    private final Declaration[]            requiredDeclarations;

    private static final Declaration[]     EMPTY_DECLARATIONS = new Declaration[0];

    public EvalCondition(EvalExpression eval,
                          Declaration[] requiredDeclarations) {
        
        this.eval = eval;
        

        if ( requiredDeclarations == null ) {
            this.requiredDeclarations = EvalCondition.EMPTY_DECLARATIONS;
        } else {
            this.requiredDeclarations = requiredDeclarations;
        }
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public boolean isAllowed(Tuple tuple,
                             WorkingMemory workingMemory) {

        return eval.evaluate( tuple, 
                              this.requiredDeclarations, 
                             workingMemory);

    }

};
