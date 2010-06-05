package org.drools.runtime.rule;

public interface Row {
    /**
     * Get the object that is bound to the given identifier
     * @param identifier
     *     The identifier of the bound object
     * @return
     */
    public Object get(String identifier);
      
    
    /**
     * Return the FactHandle associated with the given identifier
     * @param identifier
     * @return
     */
    public FactHandle getFactHandle(String identifier);

}
