package org.drools.runtime.rule;

/**
 * A row of data from the QueryResults container.
 *
 */
public interface QueryResultsRow {
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
