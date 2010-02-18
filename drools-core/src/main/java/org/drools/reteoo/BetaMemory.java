package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.util.ObjectHashMap;
import org.drools.rule.ContextEntry;

public class BetaMemory
    implements
    Externalizable {

    private static final long serialVersionUID = 400L;

    private LeftTupleMemory   leftTupleMemory;
    private RightTupleMemory  rightTupleMemory;
    private ObjectHashMap     createdHandles;
    private ContextEntry[]    context;
    private Object            behaviorContext;

    public BetaMemory() {
    }

    public BetaMemory(final LeftTupleMemory tupleMemory,
                      final RightTupleMemory objectMemory,
                      final ContextEntry[] context) {
        this.leftTupleMemory = tupleMemory;
        this.rightTupleMemory = objectMemory;
        this.context = context;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        leftTupleMemory = (LeftTupleMemory) in.readObject();
        rightTupleMemory = (RightTupleMemory) in.readObject();
        createdHandles = (ObjectHashMap) in.readObject();
        context = (ContextEntry[]) in.readObject();
        behaviorContext = (Object) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( leftTupleMemory );
        out.writeObject( rightTupleMemory );
        out.writeObject( createdHandles );
        out.writeObject( context );
        out.writeObject( behaviorContext );
    }

    public RightTupleMemory getRightTupleMemory() {
        return this.rightTupleMemory;
    }

    public LeftTupleMemory getLeftTupleMemory() {
        return this.leftTupleMemory;
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

    public Object getBehaviorContext() {
        return behaviorContext;
    }

    public void setBehaviorContext(Object behaviorContext) {
        this.behaviorContext = behaviorContext;
    }
}
