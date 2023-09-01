package org.drools.base.base;

import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.accessor.WriteAccessor;


/**
 * An interface for Accessor classes that joins both reader and writer accessor methods 
 */
public interface FieldAccessor extends ReadAccessor, WriteAccessor {
    
}
