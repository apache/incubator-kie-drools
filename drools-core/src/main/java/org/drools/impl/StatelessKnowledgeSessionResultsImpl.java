package org.drools.impl;

import java.util.Collection;
import java.util.Map;

import org.drools.runtime.StatelessKnowledgeSessionResults;

public class StatelessKnowledgeSessionResultsImpl implements StatelessKnowledgeSessionResults {

    private Map<String, ?> results;
    
    public StatelessKnowledgeSessionResultsImpl(Map<String, ?> results) {
        this.results = results;
    }
    
    public Collection<String> getIdentifiers() {
        return results.keySet();
    }

    public Object getValue(String identifier) {
        return results.get( identifier );
    }

}
