/*
 * Copyright 2005 JBoss Inc
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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.PropagationContext;
import org.drools.core.time.impl.Timer;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.index.TupleList;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

public class TimerNode extends LeftTupleSource
        implements
        LeftTupleSinkNode,
        MemoryFactory<TimerNode.TimerNodeMemory> {

    private static final long serialVersionUID = 510l;
    private Timer             timer;
    private String[]          calendarNames;
    private boolean           tupleMemoryEnabled;
    private Declaration[][]   declarations;
    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public TimerNode() {

    }

    public TimerNode(final int id,
                     final LeftTupleSource tupleSource,
                     final Timer timer,
                     final String[] calendarNames,
                     final Declaration[][]   declarations,
                     final BuildContext context) {
        super(id, context);
        setLeftTupleSource(tupleSource);
        this.timer = timer;
        this.calendarNames = calendarNames;
        this.declarations = declarations;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();

        initMasks(context, tupleSource);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        timer = (Timer) in.readObject();
        calendarNames = (String[]) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        declarations = ( Declaration[][] ) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(timer);
        out.writeObject( calendarNames );
        out.writeBoolean(tupleMemoryEnabled);
        out.writeObject( declarations );
    }

    public void attach(BuildContext context) {
        this.leftInput.addTupleSink(this, context);
        if (context == null || context.getKnowledgeBase().getConfiguration().isPhreakEnabled()) {
            return;
        }

        throw new UnsupportedOperationException("Phreak Only Node");
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.leftInput.networkUpdated(updateContext);
    }

    public Timer getTimer() {
        return this.timer;
    }

    public String[] getCalendarNames() {
        return this.calendarNames;
    }

    public Declaration[][] getDeclarations() {
        return this.declarations;
    }

    public LeftTupleSource getLeftTupleSource() {
        return this.leftInput;
    }

    /**
     * Produce a debug string.
     *
     * @return The debug string.
     */
    public String toString() {
        return "[TimerNode(" + this.id + "): cond=" + this.timer + " calendars=" + ((calendarNames == null) ? "null" : Arrays.asList(calendarNames)) + "]";
    }

    public int hashCode() {
        int hash = this.leftInput.hashCode() ^ this.timer.hashCode();
        if (calendarNames != null) {
            for ( String calendarName : calendarNames ) {
                hash = hash ^ calendarName.hashCode();
            }
        }
        return hash;
    }

    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || object.getClass() != TimerNode.class) {
            return false;
        }

        final TimerNode other = (TimerNode) object;

        if (calendarNames != null) {
            if (other.getCalendarNames() == null || other.getCalendarNames().length != calendarNames.length) {
                return false;
            }

            for (int i = 0; i < calendarNames.length; i++) {
                if (!other.getCalendarNames()[i].equals(calendarNames[i])) {
                    return false;
                }
            }
        }

        return Arrays.deepEquals(declarations, other.declarations) &&
               this.timer.equals(other.timer) && this.leftInput.equals(other.leftInput);
    }

    public TimerNodeMemory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        return new TimerNodeMemory();
    }

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        EvalNodeLeftTuple peer = new EvalNodeLeftTuple();
        peer.initPeer((BaseLeftTuple) original, this);
        original.setPeer(peer);
        return peer;
    }

    protected boolean doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        if (!this.isInUse()) {
            getLeftTupleSource().removeTupleSink(this);
            return true;
        }
        return false;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * Returns the next node
     *
     * @return The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     *
     * @param next The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     *
     * @return The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     *
     * @param previous The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode(final LeftTupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    public short getType() {
        return NodeTypeEnums.TimerConditionNode;
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new EvalNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled);
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return new EvalNodeLeftTuple(factHandle, leftTuple, sink);
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return new EvalNodeLeftTuple(leftTuple, sink, pctx, leftTupleMemoryEnabled);
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new EvalNodeLeftTuple(leftTuple, rightTuple, sink);
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new EvalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled);
    }

    protected ObjectTypeNode getObjectTypeNode() {
        return leftInput.getObjectTypeNode();
    }

    public static class TimerNodeMemory extends AbstractBaseLinkedListNode<Memory>
            implements
            Memory {

        private static final long serialVersionUID = 510l;
        private TupleList insertOrUpdateLeftTuples;
        private TupleList deleteLeftTuples;
        private SegmentMemory memory;
        private long          nodePosMaskBit;


        public TimerNodeMemory() {
            this.insertOrUpdateLeftTuples = new TupleList();
            this.deleteLeftTuples = new TupleList();
        }

        public TupleList getInsertOrUpdateLeftTuples() {
            return this.insertOrUpdateLeftTuples;
        }

        public TupleList getDeleteLeftTuples() {
            return this.deleteLeftTuples;
        }

        public short getNodeType() {
            return NodeTypeEnums.TimerConditionNode;
        }

        public SegmentMemory getSegmentMemory() {
            return this.memory;
        }

        public void setSegmentMemory(SegmentMemory smem) {
            this.memory = smem;
        }

        public long getNodePosMaskBit() {
            return nodePosMaskBit;
        }

        public void setNodePosMaskBit(long segmentPos) {
            this.nodePosMaskBit = segmentPos;
        }

        public void setNodeDirtyWithoutNotify() {
            memory.updateDirtyNodeMask( nodePosMaskBit );
        }

        public void setNodeCleanWithoutNotify() {
            memory.updateCleanNodeMask( nodePosMaskBit );
        }

        public void reset() {
            insertOrUpdateLeftTuples.clear();
            deleteLeftTuples.clear();
        }
    }

    @Override
    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyLeftTuple(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

}
