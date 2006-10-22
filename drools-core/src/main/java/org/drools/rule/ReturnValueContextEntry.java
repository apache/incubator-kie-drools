package org.drools.rule;

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.FieldExtractor;

public interface ReturnValueContextEntry extends ContextEntry {

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
     * Returns the required declarations for the given restriction
     */    
    public Declaration[] getRequiredDeclarations();

    /**
     * Returns the current working memory for the context
     */
    public InternalWorkingMemory getWorkingMemory();

}