package org.drools.rule;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.Evaluator;
import org.drools.spi.Field;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Tuple;

public class LiteralConstraint
    implements
    FieldConstraint {

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

    public Field getField() {
        return this.field;
    }

    /**
     * Literal constraints cannot have required declarations, so always return an empty array.
     * @return
     *      Return an empty <code>Declaration[]</code>
     */
    public Declaration[] getRequiredDeclarations() {
        return LiteralConstraint.requiredDeclarations;
    }

    public boolean isAllowed(FactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory) {
        return evaluator.evaluate( this.field.getValue(),
                                   this.extractor.getValue( workingMemory.getObject( handle ) ) );
    }
};
