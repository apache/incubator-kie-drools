package org.drools.rule;

import org.drools.FactHandle;
import org.drools.spi.BetaNodeConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.PredicateEvaluator;
import org.drools.spi.Tuple;

public class PredicateConstraint
    implements
    BetaNodeConstraint {    
    private final PredicateEvaluator            evaluator;
    
    private final Declaration                   declaration;

    private final Declaration[]                 requiredDeclarations;

    private static final Declaration[]          EMPTY_DECLARATIONS = new Declaration[0];

    public PredicateConstraint(PredicateEvaluator evaluator,
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

    public boolean isAllowed(Object object,
                             FactHandle handle,
                             Tuple tuple) {

        return evaluator.evaluate( tuple,
                                   object,
                                   handle,
                                   this.declaration,
                                   this.requiredDeclarations );

    }

};
