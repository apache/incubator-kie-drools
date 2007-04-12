package org.drools.reteoo;

import java.io.Serializable;

import org.drools.util.ObjectHashMap;

public class BetaMemory
    implements
    Serializable {

    private static final long serialVersionUID = -4648029105678562600L;

    private TupleMemory       tupleMemory;
    private FactHandleMemory  factHandleMemory;
    private ObjectHashMap     createdHandles;

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
        if ( this.createdHandles == null ) {
            this.createdHandles = new ObjectHashMap();
        }
        return this.createdHandles;
    }
}
