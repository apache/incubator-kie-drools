package org.drools.runtime.rule.impl;

import java.util.List;
import java.util.Map;

import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResultsRow;

public class FlatQueryResultRow
    implements
    QueryResultsRow {
    Map<String, Integer> identifiers;
    private List result;
    private List<FactHandle> factHandles;
    private int size;

    public FlatQueryResultRow(Map<String, Integer> identifiers, List result, List<FactHandle> factHandles, int size) {
        this.identifiers = identifiers;
        this.result = result;
        this.factHandles = factHandles;
    }

    public Object get(String identifier) {
        return this.result.get( identifiers.get( identifier ) );
    }

    public Object get(int i) {
        return this.result.get( i );
    }
    

    public FactHandle getFactHandle(String identifier) {
        return this.factHandles.get( identifiers.get( identifier ) );
    }    

    public FactHandle getFactHandle(int i) {
        return this.factHandles.get( i );
    }

    public int size() {
        return this.size;
    }

    

}
