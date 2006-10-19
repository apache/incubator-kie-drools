package org.drools.reteoo;

import org.drools.util.TupleHashTable;

public class BetaMemory {
    private TupleHashTable  tupleMemory;
    private ObjectHashTable objectMemory;

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
}
