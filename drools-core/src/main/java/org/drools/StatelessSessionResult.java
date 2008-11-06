package org.drools;

import java.util.Iterator;

import org.drools.spi.GlobalResolver;

/**
 * StatelessSessionResults allow inspection of session after it has executed. Querries can still be executed, unless you serialise at which point the 
 * WorkingMemory and RuleBase it references to execute thosa querries are lost due to being transient.
 * 
 * Globals must be explicitely imported into the StatelessSessionResult to be accessible, otherwise getGlobal and getGlobalResolver return null.
 * @author mproctor
 *
 */
public interface StatelessSessionResult {        
    
    /**
     * Iterator all Objects inserted into the StatelessSession.
     * @return
     *       java.util.Iterators.
     */
    Iterator iterateObjects();    
    
    /**
     * Iterate only those objects that match the provided ObjectFilter.
     * @param filter
     *             The ObjectFilter instance.
     * @return
     *             java.util.Iterators.
     */
    Iterator iterateObjects(org.drools.runtime.ObjectFilter filter);   
    
    /**
     * Retrieve the QueryResults of the specified query.
     *
     * @param query
     *            The name of the query.
     *
     * @return The QueryResults of the specified query.
     *         If no results match the query it is empty..
     *         
     * @throws IllegalArgumentException.
     *         if no query named "query" is found in the rulebase.     
     */
    public QueryResults getQueryResults(String query);
    
    /**
     * Allows for parameters to be passed to a query.
     * @param query
     *            The name of the query.
     * @param arguments
     *            The Object[] of arguments to pass to the query's parameters.
     * @return
     *            java.util.Iterator.
     */
    public QueryResults getQueryResults(final String query, final Object[] arguments);
    
    /**
     * Retrieves the global value for the given identifier, note that globals must be exported from the StatelessSession to be accessible.
     * @param identifier
     *                 The global identifier.
     * @return
     *                 The instance the global identifier points to.
     */
    public Object getGlobal(String identifier);
    
    /**
     * The GlobalResolver returned from the GlobalExporter by the StatelessSessoin
     * @return
     *        The GlobalResolver used for this StatelessSessionResult
     */
    public GlobalResolver getGlobalResolver(); 
       
}
