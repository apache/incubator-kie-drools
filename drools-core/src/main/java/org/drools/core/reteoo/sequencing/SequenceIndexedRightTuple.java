package org.drools.core.reteoo.sequencing;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.SuperCacheFixer;
import org.drools.core.reteoo.ObjectTypeNodeId;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.TupleImpl;

public class SequenceIndexedRightTuple extends TupleImpl {
    private int sequenceIndex;

    public SequenceIndexedRightTuple(InternalFactHandle factHandle, Sink sink, int sequenceIndex) {
        super(factHandle, sink, false);
        this.sequenceIndex = sequenceIndex;
    }

    public int getSequenceIndex() {
        return sequenceIndex;
    }

    @Override
    public void reAdd() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectTypeNodeId getInputOtnId() {
        return SuperCacheFixer.getRightInputOtnId(this);
    }

    @Override
    public boolean isLeftTuple() {
        return false;
    }
}
