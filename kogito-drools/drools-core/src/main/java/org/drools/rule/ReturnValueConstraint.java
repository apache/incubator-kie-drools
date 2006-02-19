package org.drools.rule;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.FieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;

public class ReturnValueConstraint
    implements
    FieldConstraint {

    private final FieldExtractor       fieldExtractor;

    private ReturnValueExpression      returnValueExpression;

    private final Declaration[]        requiredDeclarations;

    private final Evaluator            evaluator;

    private static final Declaration[] noRequiredDeclarations = new Declaration[]{};

    public ReturnValueConstraint(FieldExtractor fieldExtractor,
                                 Declaration[] declarations,
                                 Evaluator evaluator) {
        this( fieldExtractor,
              null,
              declarations,
              evaluator );
    }

    public ReturnValueConstraint(FieldExtractor fieldExtractor,
                                 ReturnValueExpression returnValueExpression,
                                 Declaration[] declarations,
                                 Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;

        this.returnValueExpression = returnValueExpression;

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

    public void setReturnValueExpression(ReturnValueExpression expression) {
        this.returnValueExpression = expression;
    }

    public boolean isAllowed(FactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory) {
        return evaluator.evaluate( this.fieldExtractor.getValue( workingMemory.getObject( handle ) ),
                                   this.returnValueExpression.evaluate( tuple,
                                                                        this.requiredDeclarations,
                                                                        workingMemory ) );
    }
}
