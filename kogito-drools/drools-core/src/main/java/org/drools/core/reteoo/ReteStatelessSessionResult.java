package org.drools.core.reteoo;

import org.drools.core.QueryResults;
import org.drools.core.StatelessSessionResult;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ObjectStore;
import org.drools.core.spi.GlobalResolver;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

public class ReteStatelessSessionResult    implements
                                           StatelessSessionResult,
                                           Externalizable {
    private transient InternalWorkingMemory workingMemory;
    // @TODO ObjectStore is currently too heavy for serialisation, but done to fix for now
    private ObjectStore objectStore;
    private GlobalResolver globalResolver;

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

    public Iterator iterateObjects(org.kie.api.runtime.ObjectFilter filter) {
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
