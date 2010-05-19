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
     * Return the Object for the specified position
     * 
     * @param i
     * @return
     */
    public Object get(final int i);    
    
    /**
     * Return the FactHandle associated with the given identifier
     * @param identifier
     * @return
     */
    public FactHandle getFactHandle(String identifier);

    /**
     * Return the FactHandle for the specified position
     * 
     * @param i
     * @return
     */
    public FactHandle getFactHandle(final int i);  
    
    /**
     * Number of objects in the row
     * 
     * @return
     */
    public int size();
}
