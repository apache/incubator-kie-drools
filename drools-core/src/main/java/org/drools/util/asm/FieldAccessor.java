package org.drools.util.asm;

/**
 * This provides "field" access to getters on a given class.
 * Implementations are generated into byte code (using a switchtable) 
 * when a new class is encountered.
 * 
 * @author Michael Neale
 */
public interface FieldAccessor {

    /**
     * Returns the "field" corresponding to the order in which it is in the object (class).
     * 
     * @param obj The object for the field to be extracted from.
     * @param idx The index of the "field".
     * @return Appropriate return type. Primitives are boxed to the corresponding type.
     */
    public Object getFieldByIndex(Object obj, int idx);
    
}
