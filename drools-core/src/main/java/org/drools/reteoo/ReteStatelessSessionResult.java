package org.drools.reteoo;

import java.util.Iterator;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

import org.drools.QueryResults;
import org.drools.StatelessSessionResult;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.ObjectStore;
import org.drools.runtime.ObjectFilter;
import org.drools.spi.GlobalResolver;
import org.drools.util.JavaIteratorAdapter;
import org.drools.util.ObjectHashMap;
import org.drools.util.AbstractHashTable.HashTableIterator;

public class ReteStatelessSessionResult
    implements
    StatelessSessionResult,
    Externalizable {
    private transient InternalWorkingMemory workingMemory;
    // @TODO ObjectStore is currently too heavy for serialisation, but done to fix for now
    private ObjectStore                     objectStore;
    private GlobalResolver                  globalResolver;

    public ReteStatelessSessionResult() {
    }

    public ReteStatelessSessionResult(InternalWorkingMemory workingMemory,
                                      GlobalResolver globalResolver) {
        this.workingMemory = workingMemory;
        this.objectStore = workingMemory.getObjectStore();
        this.globalResolver = globalResolver;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        workingMemory = (InternalWorkingMemory) in.readObject();
        objectStore = (ObjectStore) in.readObject();
        globalResolver = (GlobalResolver) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( workingMemory );
        out.writeObject( objectStore );
        out.writeObject( globalResolver );
    }

    public QueryResults getQueryResults(String query) {
        return this.workingMemory.getQueryResults( query );
    }

    public QueryResults getQueryResults(final String query,
                                        final Object[] arguments) {
        return this.workingMemory.getQueryResults( query,
                                                   arguments );
    }

    public Iterator iterateObjects() {
        return this.objectStore.iterateObjects();
    }

    public Iterator iterateObjects(ObjectFilter filter) {
        return this.objectStore.iterateObjects( filter );
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
