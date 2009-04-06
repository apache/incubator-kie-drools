package org.drools.runtime.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.ExecutionResults;

public class BatchExecutionResultImpl implements ExecutionResults {
    Map<String, Object> results = new HashMap<String, Object>();
    
    /* (non-Javadoc)
     * @see org.drools.batchexecution.BatchExecutionResult#getIdentifiers()
     */
    public Collection<String> getIdentifiers() {
        return this.results.keySet();
    }
    
    public Object getValue(String identifier) {
        return this.results.get( identifier );
    }

    /* (non-Javadoc)
     * @see org.drools.batchexecution.BatchExecutionResult#getResults()
     */
    public Map<String, Object> getResults() {
        return this.results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }
}
