package org.drools.rule;

import org.drools.FactHandle;
import org.drools.spi.Constraint;
import org.drools.spi.BaseEvaluator;
import org.drools.spi.Evaluator;
import org.drools.spi.Field;
import org.drools.spi.FieldExtractor;
import org.drools.spi.LiteralExpressionConstraint;
import org.drools.spi.Tuple;

public class LiteralConstraint
    implements
    Constraint {

    private final Field                field;

    private final FieldExtractor       extractor;

    private final Evaluator            evaluator;

    private static final Declaration[] requiredDeclarations = new Declaration[]{};

    public LiteralConstraint(Field field,
                             FieldExtractor extractor,
                             Evaluator evaluator) {
        this.field = field;
        this.extractor = extractor;
        this.evaluator = evaluator;
    }

    public Evaluator getEvaluator() {
        return this.evaluator;
    }

    public Object getField() {
        return this.field;
    }

    /**
     * Not needed but implemented so we can implement the Constraint interface
     * Just returns an empty static Declaration[]
     * 
     */
    public Declaration[] getRequiredDeclarations() {
        return LiteralConstraint.requiredDeclarations;
    }

    public boolean isAllowed(Object object) {
        return evaluator.evaluate( this.field.getValue(),
                                   this.extractor.getValue( object ) );
    }
};
