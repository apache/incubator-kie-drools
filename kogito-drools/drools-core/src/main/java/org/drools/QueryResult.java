package org.drools;

import java.util.Map;

import org.drools.rule.Declaration;
import org.drools.spi.Tuple;

public class QueryResult {
    
    private Tuple tuple;
    private WorkingMemory workingMemory;
    private QueryResults queryResults;
    
    public QueryResult( Tuple tuple,
                        WorkingMemory workingMemory, 
                        QueryResults queryResults) {
        this.tuple = tuple;
        this.workingMemory = workingMemory;
        this.queryResults = queryResults;
    }
    
    public Map getDeclarations() {
        return this.queryResults.getDeclarations();
    }
    
    public Object get(int i) {
        return this.workingMemory.getObject( tuple.get( i ) );
    }

    public Object get(String declaration) {
        return get( ( Declaration ) this.queryResults.getDeclarations().get( declaration ) );
    }
    
    public Object get(Declaration declaration) {
        return declaration.getValue( this.workingMemory.getObject( this.tuple.get( declaration ) ) );
    }
    
    public FactHandle[] getFactHandles() {
        return this.tuple.getFactHandles();
    }
    
    public int size() {
        return tuple.getFactHandles().length;
    }
}
