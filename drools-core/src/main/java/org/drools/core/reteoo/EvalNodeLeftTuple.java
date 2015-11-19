/*
 * Copyright 2010 JBoss Inc
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

import org.drools.core.common.InternalFactHandle;
import org.drools.core.spi.PropagationContext;

import java.util.Arrays;

public class EvalNodeLeftTuple extends BaseLeftTuple {

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

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#toString()
     */
    public String toString() {
        final StringBuilder buffer = new StringBuilder();

        LeftTuple entry = this;
        while (entry != null) {
            //buffer.append( entry.handle );
            buffer.append(entry.getFactHandle()).append("\n");
            entry = entry.getParent();
        }
        return buffer.toString();
    }

    protected String toExternalString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%08X",
                                     System.identityHashCode(this))).append(":");
        int[] ids = new int[getIndex() + 1];
        LeftTuple entry = this;
        while (entry != null) {
            ids[entry.getIndex()] = entry.getFactHandle().getId();
            entry = entry.getParent();
        }
        builder.append(Arrays.toString(ids))
               .append(" activation=")
               .append( getContextObject() != null ? getContextObject() : "null")
               .append(" sink=")
               .append( getTupleSink().getClass().getSimpleName())
               .append("(").append( getTupleSink().getId()).append(")");
        return builder.toString();
    }

}
