package org.drools;

import java.util.Iterator;
import java.util.List;

public interface StatelessSessionResult {        
    
    Iterator iterateObjects();    
    
    Iterator iterateObjects(ObjectFilter filter);   
    
    /**
     * Retrieve the QueryResults of the specified query.
     *
     * @param query
     *            The name of the query.
     *
     * @return The QueryResults of the specified query.
     *         If no results match the query it is empty.
     *         
     * @throws IllegalArgumentException 
     *         if no query named "query" is found in the rulebase         
     */
    public QueryResults getQueryResults(String query);
       
}
