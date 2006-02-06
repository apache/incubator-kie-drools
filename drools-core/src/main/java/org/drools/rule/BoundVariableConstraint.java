package org.drools.rule;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.FieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Tuple;

public class BoundVariableConstraint
    implements
    FieldConstraint {

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

    public boolean isAllowed(FactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory) {
        return evaluator.evaluate( this.fieldExtractor.getValue( workingMemory.getObject( handle ) ),
                                   declaration.getValue( workingMemory.getObject( tuple.get( this.declaration ) ) ) );
    }
}
