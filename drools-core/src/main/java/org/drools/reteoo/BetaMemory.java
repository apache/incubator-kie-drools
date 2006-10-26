package org.drools.reteoo;

import org.drools.util.ObjectHashMap;
import org.drools.util.TupleHashTable;

public class BetaMemory {
    private TupleMemory  tupleMemory;
    private FactHandleMemory factHandleMemory;
    private ObjectHashMap   createdHandles;

    public BetaMemory(final TupleMemory tupleMemory,
                      final FactHandleMemory objectMemory) {
        this.tupleMemory = tupleMemory;
        this.factHandleMemory = objectMemory;
    }

    public FactHandleMemory getFactHandleMemory() {
        return this.factHandleMemory;
    }

    public TupleMemory getTupleMemory() {
        return this.tupleMemory;
    }
    
    public ObjectHashMap getCreatedHandles() {
        if(createdHandles == null) {
            createdHandles = new ObjectHashMap();
        }
        return createdHandles;
    }
}
