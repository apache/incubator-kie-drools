package org.drools.reteoo;

import org.drools.util.TupleHashTable;

public class BetaMemory {
    private TupleHashTable tupleMemory;
    private ObjectHashTable objectMemory;
    
    public BetaMemory(TupleHashTable tupleMemory, ObjectHashTable objectMemory) {
        this.tupleMemory = tupleMemory;   
        this.objectMemory = objectMemory;
    }

    public ObjectHashTable getObjectMemory() {
        return objectMemory;
    }

    public TupleHashTable getTupleMemory() {
        return tupleMemory;
    }           
}
