package org.drools.rule;

import org.drools.spi.FieldExtractor;

/**
 * A context for literal constraints
 * 
 * @author etirelli
 */
public interface LiteralContextEntry
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

}
