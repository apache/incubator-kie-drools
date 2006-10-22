package org.drools.rule;

import org.drools.reteoo.ReteTuple;
import org.drools.spi.FieldExtractor;

public interface VariableContextEntry
    extends
    ContextEntry {

    /**
     * Returns the field extractor for the constrained field
     */
    public FieldExtractor getFieldExtractor();
    
    /**
     * Returns the object to extract the field from
     */
    public Object getObject();

    /**
     * Returns the ReteTuple where the variable value is read from
     */
    public ReteTuple getTuple();

    /**
     * Returns the Declaration object that knows how to read the value from the tuple
     */
    public Declaration getVariableDeclaration();
}
