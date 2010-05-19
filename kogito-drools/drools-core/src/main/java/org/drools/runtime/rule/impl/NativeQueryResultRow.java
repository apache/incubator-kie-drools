package org.drools.runtime.rule.impl;

import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResultsRow;

public class NativeQueryResultRow
    implements
    QueryResultsRow {
    private org.drools.QueryResult result;

    public NativeQueryResultRow(org.drools.QueryResult result) {
        this.result = result;
    }

    public Object get(String identifier) {
        return this.result.get( identifier );
    }    

    public Object get(int i) {
        return this.result.get( i );
    }
    
    public FactHandle getFactHandle(String identifier) {
        return this.result.getFactHandle( identifier );
    }    

    public FactHandle getFactHandle(int i) {
        return this.result.getFactHandle( i );
    }

    public int size() {
        return result.size();
    }

}
