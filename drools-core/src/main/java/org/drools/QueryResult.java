package org.drools;

import java.lang.reflect.Array;
import java.util.Map;

import org.drools.rule.Declaration;
import org.drools.spi.Tuple;

public class QueryResult {
    
    protected Tuple tuple;
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
        //adjust for the DroolsQuery object
        return this.workingMemory.getObject( tuple.get( i+1 ) );
    }

    public Object get(String declaration) {
        return get( ( Declaration ) this.queryResults.getDeclarations().get( declaration ) );
    }
    
    public Object get(Declaration declaration) {
        return declaration.getValue( this.workingMemory.getObject( this.tuple.get( declaration ) ) );
    }
    
    public FactHandle[] getFactHandles() {
        // Strip the DroolsQuery fact
        FactHandle[] src = this.tuple.getFactHandles();
        FactHandle[] dst = new FactHandle[src.length-1];
        System.arraycopy( src, 1, dst, 0, dst.length );
        return dst;
    }
    
    public int size() {
        // Adjust for the DroolsQuery object
        return tuple.getFactHandles().length -1;
    }
}
