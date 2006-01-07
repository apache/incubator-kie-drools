package org.drools.rule;

import org.drools.FactHandle;
import org.drools.spi.BetaNodeConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.ReturnValueEvaluator;
import org.drools.spi.Tuple;

public class BoundVariableConstraint
    implements
    BetaNodeConstraint {

    private final FieldExtractor       fieldExtractor;

    private final Declaration          declaration;
    
    private final Declaration[]        requiredDeclarations;

    private final Evaluator            evaluator;

    public BoundVariableConstraint(FieldExtractor fieldExtractor,
                                  Declaration declaration,
                                  Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;
        this.declaration = declaration;
        this.requiredDeclarations = new Declaration[] { declaration };
        this.evaluator = evaluator;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public boolean isAllowed(Object object,
                             FactHandle handle,
                             Tuple tuple) {
        return evaluator.evaluate( this.fieldExtractor.getValue( object ),
                                   tuple.get( this.declaration ) );
    }
}
