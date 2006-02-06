package org.drools.rule;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.FieldConstraint;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;

public class PredicateConstraint
    implements
    FieldConstraint {    
    private final PredicateExpression           evaluator;    

    private final Declaration                   declaration;
    
    private final Declaration[]                 requiredDeclarations;

    private static final Declaration[]          EMPTY_DECLARATIONS = new Declaration[0];

    public PredicateConstraint(PredicateExpression evaluator,
                               Declaration declaration,
                               Declaration[] requiredDeclarations) {
        
        this.evaluator = evaluator;
        
        this.declaration = declaration;

        if ( requiredDeclarations == null ) {
            this.requiredDeclarations = PredicateConstraint.EMPTY_DECLARATIONS;
        } else {
            this.requiredDeclarations = requiredDeclarations;
        }
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public boolean isAllowed(FactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory) {

        return evaluator.evaluate( tuple,
                                   handle,
                                   this.declaration, 
                                   this.requiredDeclarations, 
                                   workingMemory);

    }

};
