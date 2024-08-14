/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import org.drools.base.base.ObjectType;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.reteoo.DynamicFilter;
import org.drools.base.reteoo.DynamicFilterProto;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.reteoo.ObjectTypeNodeId;
import org.drools.base.rule.Pattern;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.SequenceNode.SequenceNodeMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.reteoo.sequencing.Sequencer;
import org.drools.core.reteoo.sequencing.SequencerMemoryImpl;
import org.drools.base.reteoo.sequencing.SequencerMemory;
import org.drools.base.util.AbstractLinkedListNode;
import org.drools.base.util.LinkedList;
import org.drools.core.util.index.TupleList;
import org.drools.util.bitmask.BitMask;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class SequenceNode extends LeftTupleSource
    implements
    LeftTupleNode,
    LeftTupleSinkNode,
    MemoryFactory<SequenceNodeMemory> {

    private static final long      serialVersionUID = 510l;

    protected boolean              tupleMemoryEnabled;

    private Sequencer sequencer;

    private AlphaAdapter[] alphaAdapters;

    private DynamicFilterProto[] dynamicFilters;

    private LeftTupleSinkNode      previousTupleSinkNode;

    private LeftTupleSinkNode      nextTupleSinkNode;

    public SequenceNode() {

    }

    public SequenceNode(final int id,
                        final LeftTupleSource tupleSource,
                        final BuildContext context) {
        super(id, context);
        setLeftTupleSource(tupleSource);
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();

        initMasks(context); // Is this still relevant? (mdp for multi input)

        hashcode = calculateHashCode();
        //this.processor = new AnyNotAllInputProcessor(this); // hard coded for now. Inject the processor here, conditional on the behaviour you want.
    }

    public AlphaAdapter[] getAlphaAdapters() {
        return alphaAdapters;
    }

    public void setAlphaAdapters(AlphaAdapter[] adapters) {
        this.alphaAdapters = adapters;
    }

    public DynamicFilterProto[] getDynamicFilters() {
        return dynamicFilters;
    }

    public void setDynamicFilters(DynamicFilterProto[] dynamicFilters) {
        this.dynamicFilters = dynamicFilters;
    }

    public void doAttach(BuildContext context) {
        super.doAttach(context);
        this.leftInput.addTupleSink( this, context );
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.leftInput.networkUpdated(updateContext);
    }

    
    /**
     * Produce a debug string.
     *
     * @return The debug string.
     */
    public String toString() {
        return "[SequenceNode(" + this.id + ")]]";
    }

    private int calculateHashCode() {
        return this.leftInput.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if ( object == null || !(object instanceof SequenceNode) || this.hashCode() != object.hashCode() ) {
            return false;
        }

        SequenceNode other = (SequenceNode)object;
        return this.leftInput.getId() == other.leftInput.getId();
    }

    public SequenceNodeMemory createMemory(final RuleBaseConfiguration config, ReteEvaluator reteEvaluator) {
        LinkedList<DynamicFilter>[] filters = new LinkedList[alphaAdapters.length];
        SequenceNodeMemory          memory  = new SequenceNodeMemory(this, filters, new DynamicFilter[filters.length]);
        return memory;
    }

    public SequencerMemory createSequencerMemory(TupleImpl lt, LeftTupleSink sink, SequenceNodeMemory nodeMemory) {
        SequencerMemory sequencerMemory = new SequencerMemoryImpl(sequencer, lt, sink, this, nodeMemory);

        return sequencerMemory;
    }

    public Sequencer getSequencer() {
        return sequencer;
    }

    public void setSequencer(Sequencer sequencer) {
        this.sequencer = sequencer;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode(final LeftTupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    public int getType() {
        return NodeTypeEnums.SequenceNode;
    }

    public static class SequenceNodeMemory extends AbstractLinkedListNode<Memory>
        implements
        Externalizable,
        Memory {

        private static final long serialVersionUID = 510l;

        private TupleMemory       leftTupleMemory;

        private TupleList         stagedChildTuples;
        
        private SegmentMemory     memory;

        private LinkedList<DynamicFilter>[] activeFilters;

        private DynamicFilter[] filters;

        private SequenceNode node;

        private long          nodePosMaskBit;

        public SequenceNodeMemory(SequenceNode node, LinkedList<DynamicFilter>[] activeFilters, DynamicFilter[] filters) {
            this.node = node;
            stagedChildTuples = new TupleList();
            leftTupleMemory = new TupleList();
            this.activeFilters = activeFilters;
            this.filters = filters;
        }

        public SequenceNode getNode() {
            return node;
        }

        public void addActiveFilter(DynamicFilter filter) {
            if (this.activeFilters[filter.getActiveFilterIndex()] == null) {
                this.activeFilters[filter.getActiveFilterIndex()] = new LinkedList<>();
            }
            this.activeFilters[filter.getActiveFilterIndex()].add(filter);
        }

        public void removeActiveFilter(DynamicFilter filter) {
            this.activeFilters[filter.getActiveFilterIndex()].remove(filter);
        }

        public LinkedList<DynamicFilter>[] getActiveFilters() {
            return this.activeFilters;
        }

        public DynamicFilter[] getFilters() {
            return filters;
        }

        public void setFilters(DynamicFilter[] filters) {
            this.filters = filters;
        }

        public TupleMemory getLeftTupleMemory() {
            return leftTupleMemory;
        }

        public TupleList getStagedChildTuples() {
            return stagedChildTuples;
        }

        public SegmentMemory getMemory() {
            return memory;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput out) throws IOException {

        }

        public int getNodeType() {
            return NodeTypeEnums.SequenceNode;
        }

        public void setSegmentMemory(SegmentMemory smem) {
            this.memory = smem;
        }
        
        public SegmentMemory getSegmentMemory() {
            return this.memory;
        }

        public void reset() { }

        public DynamicFilter getActiveDynamicFilter(int filterIndex) {
            DynamicFilter filter = filters[filterIndex];

            if (filter == null) {
                DynamicFilterProto proto = node.getDynamicFilters()[filterIndex];
                filter = new DynamicFilter(proto);
                filters[filterIndex] = filter;
                addActiveFilter(filter);
            } else if (filter.getSignalAdapters().isEmpty()) {
                // when it's empty, it's  removed from the list of active filters, it must be readded.
                addActiveFilter(filter);
            }

            return filter;
        }

        public void setNodePosMaskBit(long nodePosMaskBit) {
            this.nodePosMaskBit = nodePosMaskBit;
        }

        public long getNodePosMaskBit() {
            return nodePosMaskBit;
        }
    }

    @Override
    public ObjectTypeNode getObjectTypeNode() {
        return leftInput.getObjectTypeNode();
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder) {
        return true;
    }


    public static class AlphaAdapter extends ObjectSource
            implements
            ObjectSinkNode,
            RightTupleSink {
        private int          adapterIndex;

        private ObjectTypeNodeId otnId;

        private SequenceNode node;


        public AlphaAdapter(int id, ObjectSource source, RuleBasePartitionId partitionId, SequenceNode node, int adapterIndex) {
            super(id, source, partitionId);
            this.adapterIndex = adapterIndex;
            this.node = node;
        }

        public void assertObject(final InternalFactHandle factHandle,
                                 final PropagationContext pctx,
                                 final ReteEvaluator reteEvaluator) {
            System.out.println(getClass().getSimpleName() + ":" + adapterIndex + ":" + factHandle.getObject());

            SequenceNodeMemory memory = reteEvaluator.getNodeMemory(node);

            LinkedList<DynamicFilter> filters       = memory.getActiveFilters()[adapterIndex];
            if (filters != null) {
                for (DynamicFilter f = filters.getFirst(); f != null; f = f.getNext()) {
                    f.assertObject(factHandle, reteEvaluator);
                }
            }
        }

        public void propagate() {

        }

        public void modifyObject(InternalFactHandle factHandle,
                                 ModifyPreviousTuples modifyPreviousTuples,
                                 PropagationContext context,
                                 ReteEvaluator reteEvaluator) {

        }

        @Override
        public void retractRightTuple(TupleImpl rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {
            // for now assuming we don't have any deletes, but we could add this later.
        }

        @Override
        public void modifyRightTuple(TupleImpl rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {
            // for now assuming we don't have any modifies, but we could add this later.
            // typically just gets treated as another add, as the processor is not that stateful (for this use case) - to keep it fast.
        }

        public int getId() {
            return 0;
        }

        public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                           ModifyPreviousTuples modifyPreviousTuples,
                                           PropagationContext context,
                                           ReteEvaluator reteEvaluator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSinkNode getNextObjectSinkNode() {
            return null;
        }

        @Override
        public void setNextObjectSinkNode(ObjectSinkNode next) {

        }

        @Override
        public ObjectSinkNode getPreviousObjectSinkNode() {
            return null;
        }

        @Override
        public void setPreviousObjectSinkNode(ObjectSinkNode previous) {

        }

        @Override
        public BitMask calculateDeclaredMask(Pattern pattern, ObjectType modifiedType, List<String> settableProperties) {
            return null;
        }

        @Override
        public void updateSink(ObjectSink sink, PropagationContext context, InternalWorkingMemory workingMemory) {

        }

        @Override
        public ObjectTypeNodeId getInputOtnId() {
            return otnId;
        }

        public int getType() {
            return NodeTypeEnums.SequenceNode; // need to update enums for multi input (mdp)
        }

        public void doAttach(BuildContext context) {
            super.doAttach(context);
            this.source.addObjectSink(this);
        }
    }

    public static class PhreakSequenceNode {
        public void doNode(SequenceNode node,
                           SequenceNodeMemory memory,
                           LeftTupleSink sink,
                           ReteEvaluator reteEvaluator,
                           TupleSets srcLeftTuples,
                           TupleSets trgLeftTuples,
                           TupleSets stagedLeftTuples) {

            if (srcLeftTuples.getDeleteFirst() != null) {
                doLeftDeletes(node, srcLeftTuples, trgLeftTuples, stagedLeftTuples, reteEvaluator);
            }

            if (srcLeftTuples.getUpdateFirst() != null) {
                doLeftUpdates(node, srcLeftTuples, trgLeftTuples, stagedLeftTuples, reteEvaluator);
            }

            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(node, memory, sink, srcLeftTuples, reteEvaluator);
            }
            
            srcLeftTuples.resetAll();
        }

        private void doLeftUpdates(SequenceNode node, TupleSets srcLeftTuples, TupleSets trgLeftTuples, TupleSets stagedLeftTuples, ReteEvaluator reteEvaluator) {}

//        public void doNode(SequenceNode node, SequenceNodeMemory nodeMem, PathMemory pmem, SegmentMemory smem, LeftTupleSinkNode sink, ActivationsManager activationsManager, TupleSets srcTuples, TupleSets trgTuples, TupleSets stagedLeftTuples) {
//
//        }

        private void doLeftDeletes(SequenceNode node,
                                   TupleSets srcLeftTuples, TupleSets trgLeftTuples, TupleSets stagedLeftTuples,
                                   ReteEvaluator evaluator) {
            for (TupleImpl leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                TupleImpl next = leftTuple.getStagedNext();

                SequencerMemory sequencerMemory = (SequencerMemory) leftTuple.getContextObject();
                node.getSequencer().stop(sequencerMemory, evaluator);
                leftTuple.getMemory().remove(leftTuple);
                leftTuple.setContextObject(null);
                // TODO add code here to propagate deletion of child LT (mdp)

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

        private void doLeftInserts(SequenceNode node,
                                   SequenceNodeMemory memory,
                                   LeftTupleSink sink,
                                   TupleSets srcLeftTuples,
                                   ReteEvaluator evaluator) {
            for (TupleImpl leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                TupleImpl next = leftTuple.getStagedNext();

                memory.getLeftTupleMemory().add(leftTuple);

                SequencerMemory sequencerMemory = memory.node.createSequencerMemory(leftTuple, sink, memory);
                leftTuple.setContextObject(sequencerMemory);
                node.getSequencer().start(sequencerMemory, evaluator);

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }

    }
}
