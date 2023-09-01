package org.kie.api.runtime;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * Contains the results for the BatchExecution Command. If the identifier is reference the results of a query, you'll need to cast the value to
 * QueryResults.
 * </p>
 */
public interface ExecutionResults {

    Collection<String> getIdentifiers();

    Object getValue(String identifier);

    Object getFactHandle(String identifier);

    Map<String, Object> getResults();

    void setResult(String identifier, Object result);

}
