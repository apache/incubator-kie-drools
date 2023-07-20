/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;

import java.util.concurrent.atomic.AtomicInteger;

public class SubnetworkTuple extends AbstractLeftTuple implements RightTuple {

    private AbstractLeftTuple blocked;
    private AbstractLeftTuple tempBlocked;

    private RightTuple tempNextRightTuple;

    private static final AtomicInteger idGenerator = new AtomicInteger( 0 );
    private InternalFactHandle factHandleForEvaluation = new DefaultFactHandle(idGenerator.decrementAndGet(), this);

    private boolean stagedOnRight;
    private short stagedTypeOnRight;
    private Tuple stagedNextOnRight;
    private Tuple stagedPreviousOnRight;


    public SubnetworkTuple() {
        // constructor needed for serialisation
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public SubnetworkTuple(final InternalFactHandle factHandle,
                           final Sink sink,
                           final boolean leftTupleMemoryEnabled) {
        super(factHandle, sink, leftTupleMemoryEnabled);
    }

    public SubnetworkTuple(final InternalFactHandle factHandle,
                           final AbstractLeftTuple leftTuple,
                           final Sink sink) {
        super( factHandle, leftTuple, sink );
    }

    public SubnetworkTuple(final AbstractLeftTuple leftTuple,
                           final Sink sink,
                           final PropagationContext pctx,
                           final boolean leftTupleMemoryEnabled) {
        super(leftTuple, sink, pctx, leftTupleMemoryEnabled);
    }

    public SubnetworkTuple(final AbstractLeftTuple leftTuple,
                           final RightTuple rightTuple,
                           final Sink sink) {
        super(leftTuple, rightTuple, sink);
    }

    public SubnetworkTuple(final AbstractLeftTuple leftTuple,
                           final RightTuple rightTuple,
                           final AbstractLeftTuple currentLeftChild,
                           final AbstractLeftTuple currentRightChild,
                           final Sink sink,
                           final boolean leftTupleMemoryEnabled) {
        super(leftTuple,
              rightTuple,
              currentLeftChild,
              currentRightChild,
              sink,
              leftTupleMemoryEnabled);
    }

    public InternalFactHandle getFactHandleForEvaluation() {
        return factHandleForEvaluation;
    }

    public AbstractLeftTuple getBlocked() {
        return this.blocked;
    }

    public void setBlocked(AbstractLeftTuple leftTuple) {
        this.blocked = leftTuple;
    }

    public void addBlocked(AbstractLeftTuple leftTuple) {
        if ( this.blocked != null && leftTuple != null ) {
            leftTuple.setBlockedNext( this.blocked );
            this.blocked.setBlockedPrevious( leftTuple );
        }
        this.blocked = leftTuple;
    }

    public void removeBlocked(AbstractLeftTuple leftTuple) {
        AbstractLeftTuple previous =  leftTuple.getBlockedPrevious();
        AbstractLeftTuple next =  leftTuple.getBlockedNext();
        if ( previous != null && next != null ) {
            //remove  from middle
            previous.setBlockedNext( next );
            next.setBlockedPrevious( previous );
        } else if ( next != null ) {
            //remove from first
            this.blocked = next;
            next.setBlockedPrevious( null );
        } else if ( previous != null ) {
            //remove from end
            previous.setBlockedNext( null );
        } else {
            this.blocked = null;
        }
        leftTuple.clearBlocker();
    }

    public AbstractLeftTuple getTempBlocked() {
        return tempBlocked;
    }

    public void setTempBlocked(AbstractLeftTuple tempBlocked) {
        this.tempBlocked = tempBlocked;
    }

    public RightTuple getTempNextRightTuple() {
        return tempNextRightTuple;
    }

    public void setTempNextRightTuple(RightTuple tempNextRightTuple) {
        this.tempNextRightTuple = tempNextRightTuple;
    }

    public boolean isStagedOnRight() {
        return stagedOnRight;
    }

    public void setStagedOnRight() {
        this.stagedOnRight = true;
    }

    public void prepareStagingOnRight() {
        super.clearStaged();
        setStagedOnRight();
        stagedTypeOnRight = Tuple.NONE;
    }

    @Override
    public void clearStaged() {
        super.clearStaged();
        stagedOnRight = false;
    }

    @Override
    public void retractTuple( PropagationContext context, ReteEvaluator reteEvaluator ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setExpired( ReteEvaluator reteEvaluator, PropagationContext pctx ) {
        super.setExpired();
    }

    public void moveStagingFromRightToLeft() {
        stagedTypeOnRight = getStagedType();
        stagedPreviousOnRight = getStagedPrevious();
        stagedNextOnRight = getStagedNext();
        clearStaged();
    }

    public SubnetworkTuple moveStagingFromLeftToRight() {
        stagedPrevious = stagedPreviousOnRight;
        stagedPreviousOnRight = null;
        stagedNext = stagedNextOnRight;
        stagedNextOnRight = null;
        return this;
    }

    public short getStagedTypeOnRight() {
        return stagedTypeOnRight;
    }
}
