package org.drools.reteoo;

import java.util.Iterator;

import org.drools.ObjectFilter;
import org.drools.QueryResults;
import org.drools.StatelessSessionResult;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.GlobalResolver;
import org.drools.util.JavaIteratorAdapter;
import org.drools.util.ObjectHashMap;
import org.drools.util.AbstractHashTable.HashTableIterator;

public class ReteStatelessSessionResult implements StatelessSessionResult {
    private transient InternalWorkingMemory workingMemory;
    private ObjectHashMap assertMap;
    private GlobalResolver globalResolver;
    
    public ReteStatelessSessionResult(InternalWorkingMemory workingMemory, GlobalResolver globalResolver) {
        this.workingMemory = workingMemory;
        this.assertMap = workingMemory.getAssertMap();
        this.globalResolver = globalResolver;
    }

    public QueryResults getQueryResults(String query) {
        return this.workingMemory.getQueryResults( query );
    }
    
    public QueryResults getQueryResults(final String query, final Object[] arguments) {
        return this.workingMemory.getQueryResults( query, 
                                                   arguments );
    }

    public Iterator iterateObjects() {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator,
                                        JavaIteratorAdapter.OBJECT );
    }

    public Iterator iterateObjects(ObjectFilter filter) {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator,
                                        JavaIteratorAdapter.OBJECT,
                                        filter );
    }
    
    public Object getGlobal(String identifier) {
        if ( this.globalResolver == null ) {
            return null;
        }
        return this.globalResolver.resolveGlobal( identifier );
    }
    
    public GlobalResolver getGlobalResolver() {
        return this.globalResolver;
    }
}
