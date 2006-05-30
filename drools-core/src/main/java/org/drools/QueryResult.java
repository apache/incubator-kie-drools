package org.drools;

import java.util.Map;

import org.drools.rule.Declaration;
import org.drools.spi.Tuple;

public class QueryResult {

    protected Tuple       tuple;
    private WorkingMemory workingMemory;
    private QueryResults  queryResults;

    public QueryResult(final Tuple tuple,
                       final WorkingMemory workingMemory,
                       final QueryResults queryResults) {
        this.tuple = tuple;
        this.workingMemory = workingMemory;
        this.queryResults = queryResults;
    }

    public Map getDeclarations() {
        return this.queryResults.getDeclarations();
    }

    public Object get(final int i) {
        //adjust for the DroolsQuery object
        return this.tuple.get( i + 1 ).getObject();
    }

    public Object get(final String declaration) {
        return get( (Declaration) this.queryResults.getDeclarations().get( declaration ) );
    }

    public Object get(final Declaration declaration) {
        return declaration.getValue( this.tuple.get( declaration ).getObject() );
    }

    public FactHandle[] getFactHandles() {
        // Strip the DroolsQuery fact
        final FactHandle[] src = this.tuple.getFactHandles();
        final FactHandle[] dst = new FactHandle[src.length - 1];
        System.arraycopy( src,
                          1,
                          dst,
                          0,
                          dst.length );
        return dst;
    }

    public int size() {
        // Adjust for the DroolsQuery object
        return this.tuple.getFactHandles().length - 1;
    }
}
