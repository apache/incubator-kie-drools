package org.drools.reteoo;

import java.util.HashMap;
import java.util.Map;

import org.drools.util.TupleHashTable;

public class BetaMemory {
    private TupleHashTable  tupleMemory;
    private ObjectHashTable objectMemory;
    private Map         createdHandles;

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
    
    public Map getCreatedHandles() {
        if(createdHandles == null) {
            createdHandles = new HashMap();
        }
        return createdHandles;
    }
}
