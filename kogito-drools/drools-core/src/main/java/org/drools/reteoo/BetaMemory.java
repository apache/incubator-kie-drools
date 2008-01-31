package org.drools.reteoo;

import java.io.Serializable;

import org.drools.rule.ContextEntry;
import org.drools.util.ObjectHashMap;

public class BetaMemory
    implements
    Serializable {

    private static final long serialVersionUID = 400L;

    private TupleMemory       tupleMemory;
    private FactHandleMemory  factHandleMemory;
    private ObjectHashMap     createdHandles;
    private ContextEntry[]    context;

    public BetaMemory(final TupleMemory tupleMemory,
                      final FactHandleMemory objectMemory,
                      final ContextEntry[] context ) {
        this.tupleMemory = tupleMemory;
        this.factHandleMemory = objectMemory;
        this.context = context;
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

    /**
     * @return the context
     */
    public ContextEntry[] getContext() {
        return context;
    }
}
