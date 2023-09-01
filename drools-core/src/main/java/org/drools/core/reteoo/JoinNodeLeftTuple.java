package org.drools.core.reteoo;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;

public class JoinNodeLeftTuple extends LeftTuple {

    private static final long serialVersionUID = 540l;

    public JoinNodeLeftTuple() {
        // constructor needed for serialisation
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public JoinNodeLeftTuple(final InternalFactHandle factHandle,
                             final Sink sink,
                             final boolean leftTupleMemoryEnabled) {
        super(factHandle, sink, leftTupleMemoryEnabled);
    }

    public JoinNodeLeftTuple(final InternalFactHandle factHandle,
                             final LeftTuple leftTuple,
                             final Sink sink) {
        super( factHandle, leftTuple, sink );
    }

    public JoinNodeLeftTuple(final LeftTuple leftTuple,
                             final Sink sink,
                             final PropagationContext pctx,
                             final boolean leftTupleMemoryEnabled) {
        super(leftTuple, sink, pctx, leftTupleMemoryEnabled);
    }

    public JoinNodeLeftTuple(final LeftTuple leftTuple,
                             final RightTuple rightTuple,
                             final Sink sink) {
        super(leftTuple, rightTuple, sink);
    }

    public JoinNodeLeftTuple(final LeftTuple leftTuple,
                             final RightTuple rightTuple,
                             final Sink sink,
                             final boolean leftTupleMemoryEnabled) {
        this(leftTuple,
             rightTuple,
             null,
             null,
             sink,
             leftTupleMemoryEnabled);
    }

    public JoinNodeLeftTuple(final LeftTuple leftTuple,
                             final RightTuple rightTuple,
                             final LeftTuple currentLeftChild,
                             final LeftTuple currentRightChild,
                             final Sink sink,
                             final boolean leftTupleMemoryEnabled) {
        super(leftTuple,
              rightTuple,
              currentLeftChild,
              currentRightChild,
              sink,
              leftTupleMemoryEnabled);
    }
}
