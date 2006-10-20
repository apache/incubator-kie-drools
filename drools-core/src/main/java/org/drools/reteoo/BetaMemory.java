package org.drools.reteoo;

import org.drools.util.ObjectHashMap;
import org.drools.util.TupleHashTable;

public class BetaMemory {
    private TupleHashTable  tupleMemory;
    private ObjectHashTable objectMemory;
    private ObjectHashMap   createdHandles;

    public BetaMemory(final TupleHashTable tupleMemory,
                      final ObjectHashTable objectMemory) {
        this.tupleMemory = tupleMemory;
        this.objectMemory = objectMemory;
    }

    public ObjectHashTable getObjectMemory() {
        return this.objectMemory;
    }

    public TupleHashTable getTupleMemory() {
        return this.tupleMemory;
    }
    
    public ObjectHashMap getCreatedHandles() {
        if(createdHandles == null) {
            createdHandles = new ObjectHashMap();
        }
        return createdHandles;
    }
}
