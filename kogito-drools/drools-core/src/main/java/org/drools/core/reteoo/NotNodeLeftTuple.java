/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.FastIterator;

public class NotNodeLeftTuple extends BaseLeftTuple {
    private static final long serialVersionUID = 540l;

    private RightTuple blocker;
    private LeftTuple  blockedPrevious;
    private LeftTuple  blockedNext;

    public NotNodeLeftTuple() {
        // constructor needed for serialisation
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public NotNodeLeftTuple(final InternalFactHandle factHandle,
                            Sink sink,
                            boolean leftTupleMemoryEnabled) {
        super(factHandle,
              sink,
              leftTupleMemoryEnabled);
    }

    public NotNodeLeftTuple(final InternalFactHandle factHandle,
                            final LeftTuple leftTuple,
                            final Sink sink) {
        super( factHandle, leftTuple, sink );
    }

    public NotNodeLeftTuple(final LeftTuple leftTuple,
                            final Sink sink,
                            final PropagationContext pctx,
                            final boolean leftTupleMemoryEnabled) {
        super(leftTuple,
              sink,
              pctx,
              leftTupleMemoryEnabled);
    }

    public NotNodeLeftTuple(final LeftTuple leftTuple,
                            RightTuple rightTuple,
                            Sink sink) {
        super(leftTuple,
              rightTuple,
              sink);
    }

    public NotNodeLeftTuple(final LeftTuple leftTuple,
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

    public NotNodeLeftTuple(final LeftTuple leftTuple,
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
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#unlinkFromRightParent()
     */
    public void unlinkFromRightParent() {
        super.unlinkFromRightParent();
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

    @Override
    public Collection<Object> getAccumulatedObjects() {
        if (NodeTypeEnums.ExistsNode != getTupleSink().getType()) {
            return Collections.emptyList();
        }

        BetaNode betaNode = ( (BetaNode) getTupleSink() );
        BetaConstraints constraints = betaNode.getRawConstraints();
        InternalWorkingMemory wm = getFactHandle().getEntryPoint().getInternalWorkingMemory();
        BetaMemory bm = (BetaMemory) wm.getNodeMemory( (MemoryFactory) getTupleSink() );
        TupleMemory rtm = bm.getRightTupleMemory();
        FastIterator it = betaNode.getRightIterator( rtm );

        ContextEntry[] contextEntry = bm.getContext();
        constraints.updateFromTuple( contextEntry, wm, this );

        Collection<Object> result = new ArrayList<>();
        for (RightTuple rightTuple = betaNode.getFirstRightTuple(this, rtm, null, it); rightTuple != null; ) {
            RightTuple nextRight = (RightTuple) it.next(rightTuple);
            InternalFactHandle fh = rightTuple.getFactHandleForEvaluation();
            if (constraints.isAllowedCachedLeft(contextEntry, fh)) {
                result.add(fh.getObject());
            }
            rightTuple = nextRight;
        }
        return result;
    }
}
