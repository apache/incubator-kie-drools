package org.drools.runtime;

import java.util.Collection;

/**
 * <p>
 * Contains the results for the BatchExecution Command. If the identifier is reference the results of a query, you'll need to cast the vlaue to
 * QueryResults.
 * </p>
 * 
 *
 */
public interface BatchExecutionResults {

    Collection<String> getIdentifiers();

    Object getValue(String identifier);

}