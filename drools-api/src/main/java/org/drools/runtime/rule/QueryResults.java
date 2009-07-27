package org.drools.runtime.rule;

import java.util.Iterator;

import org.drools.result.GenericResult;

/**
 * <p>
 * Contains the results of a query. The identifiers is a map of the declarations for the query, only patterns or fields that are bound can
 * be accessed in the QueryResultsRow. This class can be marshalled using the drools-transformer-xstream module in combination with the BatchExecutionHelper.
 * See the BatchExecutionHelper for more details.
 * </p>
 * 
 */
public interface QueryResults extends Iterable<QueryResultsRow> {
    String[] getIdentifiers();
    
    Iterator<QueryResultsRow> iterator();
    
    int size();
}
