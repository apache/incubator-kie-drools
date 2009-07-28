package org.drools.runtime.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.ExecutionResults;

public class ExecutionResultImpl implements ExecutionResults {
    Map<String, Object> results = new HashMap<String, Object>();    
    Map<String, Object> facts = new HashMap<String, Object>();
    
    /* (non-Javadoc)
     * @see org.drools.batchexecution.BatchExecutionResult#getIdentifiers()
     */
    public Collection<String> getIdentifiers() {
        return this.results.keySet();
    }
    
    public Object getValue(String identifier) {
        return this.results.get( identifier );
    }
    
    public Object getFactHandle(String identifier) {
        return this.facts.get( identifier );
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
    
    public Map<String, Object> getFactHandles() {
        return this.facts;
    }    
    
    public void setFactHandles(Map<String, Object> facts) {
        this.facts = facts;
    }
}
