package org.drools.reteoo;

import java.io.Serializable;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.rule.ContextEntry;
import org.drools.util.ObjectHashMap;

public class BetaMemory
    implements
    Externalizable {

    private static final long serialVersionUID = 400L;

    private TupleMemory       tupleMemory;
    private FactHandleMemory  factHandleMemory;
    private ObjectHashMap     createdHandles;
    private ContextEntry[]    context;

    public BetaMemory() {
    }

    public BetaMemory(final TupleMemory tupleMemory,
                      final FactHandleMemory objectMemory,
                      final ContextEntry[] context ) {
        this.tupleMemory = tupleMemory;
        this.factHandleMemory = objectMemory;
        this.context = context;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tupleMemory         = (TupleMemory)in.readObject();
        factHandleMemory    = (FactHandleMemory)in.readObject();
        createdHandles      = (ObjectHashMap)in.readObject();
        context             = (ContextEntry[])in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(tupleMemory);
        out.writeObject(factHandleMemory);
        out.writeObject(createdHandles);
        out.writeObject(context);
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
