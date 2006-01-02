package org.drools.rule;

import org.drools.FactHandle;
import org.drools.spi.BetaNodeConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.ReturnValueEvaluator;
import org.drools.spi.Tuple;

public class ReturnValueConstraint
    implements
    BetaNodeConstraint {

    private final FieldExtractor       fieldExtractor;

    private final ReturnValueEvaluator returnValueEvaluator;

    private final Declaration[]        requiredDeclarations;

    private final Evaluator            evaluator;

    private static final Declaration[] noRequiredDeclarations = new Declaration[]{};

    public ReturnValueConstraint(FieldExtractor fieldExtractor,
                                 ReturnValueEvaluator returnValueEvaluator,
                                 Declaration[] declarations,
                                 Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;

        this.returnValueEvaluator = returnValueEvaluator;

        if ( declarations != null ) {
            this.requiredDeclarations = declarations;
        } else {
            this.requiredDeclarations = ReturnValueConstraint.noRequiredDeclarations;
        }

        this.evaluator = evaluator;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public boolean isAllowed(Object object,
                             FactHandle handle,
                             Tuple tuple) {
        return evaluator.evaluate( this.fieldExtractor.getValue( object ),
                                   this.returnValueEvaluator.evaluate( tuple,
                                                                       this.requiredDeclarations ) );
    }
}
