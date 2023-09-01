package org.drools.core.reteoo;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;

public class EvalNodeLeftTuple extends LeftTuple {

    private static final long serialVersionUID = 540l;

    private RightTuple blocker;

    private LeftTuple blockedPrevious;

    private LeftTuple blockedNext;

    public EvalNodeLeftTuple() {
        // constructor needed for serialisation
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public EvalNodeLeftTuple(final InternalFactHandle factHandle,
                             final Sink sink,
                             final boolean leftTupleMemoryEnabled) {
        super(factHandle,
              sink,
              leftTupleMemoryEnabled);
    }

    public EvalNodeLeftTuple(final InternalFactHandle factHandle,
                             final LeftTuple leftTuple,
                             final Sink sink) {
        super( factHandle, leftTuple, sink );
    }

    public EvalNodeLeftTuple(final LeftTuple leftTuple,
                             final Sink sink,
                             final PropagationContext pctx,
                             final boolean leftTupleMemoryEnabled) {
        super(leftTuple,
              sink,
              pctx,
              leftTupleMemoryEnabled);
    }

    public EvalNodeLeftTuple(final LeftTuple leftTuple,
                             RightTuple rightTuple,
                             Sink sink) {
        super(leftTuple,
              rightTuple,
              sink);
    }

    public EvalNodeLeftTuple(final LeftTuple leftTuple,
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

    public EvalNodeLeftTuple(final LeftTuple leftTuple,
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

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#unlinkFromLeftParent()
     */
    public void unlinkFromLeftParent() {
        super.unlinkFromLeftParent();
        this.blocker = null;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#unlinkFromRightParent()
     */
    public void unlinkFromRightParent() {
        super.unlinkFromRightParent();
        this.blocker = null;
    }

    public void clearBlocker() {
        this.blockedPrevious = null;
        this.blockedNext = null;
        this.blocker = null;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setBlocker(org.kie.reteoo.RightTuple)
     */
    public void setBlocker(RightTuple blocker) {
        this.blocker = blocker;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getBlocker()
     */
    public RightTuple getBlocker() {
        return this.blocker;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getBlockedPrevious()
     */
    public LeftTuple getBlockedPrevious() {
        return this.blockedPrevious;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setBlockedPrevious(org.kie.reteoo.LeftTuple)
     */
    public void setBlockedPrevious(LeftTuple blockerPrevious) {
        this.blockedPrevious = blockerPrevious;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getBlockedNext()
     */
    public LeftTuple getBlockedNext() {
        return this.blockedNext;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setBlockedNext(org.kie.reteoo.LeftTuple)
     */
    public void setBlockedNext(LeftTuple blockerNext) {
        this.blockedNext = blockerNext;
    }
}
