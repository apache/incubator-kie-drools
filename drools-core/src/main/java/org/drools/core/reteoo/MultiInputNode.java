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
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.common.TupleSetsImpl;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.MultiInputNode.MultiInputNodeMemory;
import org.drools.core.reteoo.sequencing.ConditionalSignalCounter;
import org.drools.core.reteoo.sequencing.SignalProcessor;
import org.drools.core.reteoo.sequencing.LogicGate;
import org.drools.core.reteoo.sequencing.SignalStatus;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.sequencing.Sequencer;
import org.drools.core.reteoo.sequencing.Sequencer.SequencerMemory;
import org.drools.core.util.AbstractLinkedListNode;
import org.drools.core.util.LinkedList;
import org.drools.core.util.index.TupleList;
import org.drools.util.bitmask.BitMask;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class MultiInputNode extends LeftTupleSource
    implements
    LeftTupleSinkNode,
    MemoryFactory<MultiInputNodeMemory> {

    private static final long      serialVersionUID = 510l;

    protected boolean              tupleMemoryEnabled;

    private Sequencer sequencer;

    //private SequenceInputAdapter[] sequenceInputAdapters;

    private AlphaAdapter[] alphaAdapters;

    private DynamicFilterProto[]   dynamicFilters;

    private LogicGate[]                gates;

    private LeftTupleSinkNode      previousTupleSinkNode;

    private LeftTupleSinkNode      nextTupleSinkNode;

    public MultiInputNode() {

    }

    public MultiInputNode(final int id,
                          final LeftTupleSource tupleSource,
                          final BuildContext context) {
        super(id, context);
        setLeftTupleSource(tupleSource);
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();

        initMasks(context); // Is this still relevant? (mdp for multi input)

        hashcode = calculateHashCode();
        gates = new LogicGate[0];
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

//    public SequenceInputAdapter[] getSequenceInputAdapters() {
//        return sequenceInputAdapters;
//    }
//
//    public void setSequenceInputAdapters(SequenceInputAdapter[] sequenceInputAdapters) {
//        this.sequenceInputAdapters = sequenceInputAdapters;
//    }

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
        return "[MultiInputNode(" + this.id + ")]]";
    }

    private int calculateHashCode() {
        return this.leftInput.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if ( object == null || !(object instanceof MultiInputNode) || this.hashCode() != object.hashCode() ) {
            return false;
        }

        MultiInputNode other = (MultiInputNode)object;
        return this.leftInput.getId() == other.leftInput.getId();
    }

    public MultiInputNodeMemory createMemory(final RuleBaseConfiguration config, ReteEvaluator reteEvaluator) {
        LinkedList<DynamicFilter>[] filters = new LinkedList[alphaAdapters.length];
        MultiInputNodeMemory memory = new MultiInputNodeMemory(this, filters, new DynamicFilter[filters.length]);
        return memory;
    }

    public SequencerMemory createSequencerMemory(MultiInputNodeMemory nodeMemory) {
        int signalAdapters = 0;
        int counters = 0;
        for (LogicGate gate : gates) {
            counters = counters + gate.getInputSignalCounters().length;
            if ( gate.getOutput() instanceof ConditionalSignalCounter) {
                ++counters;
            }
            signalAdapters = signalAdapters + gate.getSignalAdapterIndexes().length;
        }

        long[] gateMemory = new long[gates.length];
        long[] counterMemory = new long[counters];

        SequencerMemory circuitMemory = new SequencerMemory(this, nodeMemory, gateMemory, counterMemory,
                                                            new SignalAdapter[signalAdapters], new SignalAdapter[signalAdapters]);
        return circuitMemory;
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
        return NodeTypeEnums.MultiInputNode;
    }

    public void setLogicGates(LogicGate[] logicGates) {
        this.gates = logicGates;
    }

    public void fail(SequencerMemory memory) {
        System.out.println("fail");
    }

    public static class MultiInputNodeMemory extends AbstractLinkedListNode<Memory>
        implements
        Externalizable,
        Memory {

        private static final long serialVersionUID = 510l;

        private TupleMemory       leftTupleMemory;

        private TupleSets         stagedRightTuples;
        
        private SegmentMemory     memory;

        private LinkedList<DynamicFilter>[] activeFilters;

        private DynamicFilter[] filters;

        private MultiInputNode node;

        public MultiInputNodeMemory(MultiInputNode node, LinkedList<DynamicFilter>[] activeFilters, DynamicFilter[] filters) {
            this.node = node;
            stagedRightTuples = new TupleSetsImpl();
            leftTupleMemory = new TupleList();
            this.activeFilters = activeFilters;
            this.filters = filters;
        }

        public void addActiveFilter(DynamicFilter filter) {
            if (this.activeFilters[filter.activeFilterIndex] == null) {
                this.activeFilters[filter.activeFilterIndex] = new LinkedList<>();
            }
            this.activeFilters[filter.activeFilterIndex].add(filter);
        }

        public void removeActiveFilter(DynamicFilter filter) {
            this.activeFilters[filter.activeFilterIndex].remove(filter);
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

        public TupleSets getStagedRightTuples() {
            return stagedRightTuples;
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
            return NodeTypeEnums.MultiInputNode;
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

    }

    @Override
    public ObjectTypeNode getObjectTypeNode() {
        return leftInput.getObjectTypeNode();
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder) {
        return true;
    }

    public static class MaskValue {
        long mask = 0;
    }

    public static class SignalAdapter extends AbstractLinkedListNode<SignalAdapter>  {
        private SignalProcessor output;
        private int             signalBitIndex;
        private SequencerMemory memory;

        public SignalAdapter(SignalProcessor output, int signalBitIndex, SequencerMemory memory) {
            this.output = output;
            this.signalBitIndex = signalBitIndex;
            this.memory         = memory;
        }

        public void receive() {
            output.receive(signalBitIndex, SignalStatus.MATCHED, memory);
        }
    }

    public static class DynamicFilterProto {
        private AlphaNodeFieldConstraint  constraint;
        private int filterIndex;

        public DynamicFilterProto(AlphaNodeFieldConstraint constraint, int filterIndex) {
            this.constraint  = constraint;
            this.filterIndex = filterIndex;
        }

        public AlphaNodeFieldConstraint getConstraint() {
            return constraint;
        }

        public int getFilterIndex() {
            return filterIndex;
        }
    }

    public static class DynamicFilter extends AbstractLinkedListNode<DynamicFilter>  {
        private AlphaNodeFieldConstraint  constraint;
        private LinkedList<SignalAdapter> signalAdapters;
        private int                       activeFilterIndex;

        public DynamicFilter(DynamicFilterProto  proto) {
            this.constraint        = proto.constraint;
            this.activeFilterIndex = proto.filterIndex;
            this.signalAdapters    = new LinkedList<>();
        }

        public AlphaNodeFieldConstraint getConstraint() {
            return constraint;
        }

        public int getActiveFilterIndex() {
            return activeFilterIndex;
        }

        public void addSignalAdapter(SignalAdapter signalAdapter) {
            signalAdapters.add(signalAdapter);
        }

        public void removeSignalAdapter(SignalAdapter signalAdapter) {
            signalAdapters.remove(signalAdapter);
        }

        public LinkedList<SignalAdapter> getSignalAdapters() {
            return signalAdapters;
        }

        public void assertObject(final InternalFactHandle factHandle,
                                 final PropagationContext pctx,
                                 final ReteEvaluator reteEvaluator,
                                 final MultiInputNodeMemory memory) {
            System.out.println("true : " + factHandle.getObject());

            if (constraint.isAllowed(factHandle, reteEvaluator)) {
                for (SignalAdapter signal = signalAdapters.getFirst(); signal != null; signal = signal.getNext()) {
                    signal.receive();
                }
            }
        }
    }


    public static class AlphaAdapter extends ObjectSource
            implements
            ObjectSinkNode,
            RightTupleSink {
        private int          adapterIndex;

        private ObjectTypeNodeId otnId;

        private MultiInputNode node;


        public AlphaAdapter(int id, ObjectSource source, RuleBasePartitionId partitionId, MultiInputNode node, int adapterIndex) {
            super(id, source, partitionId);
            this.adapterIndex = adapterIndex;
            this.node = node;
        }

        public void assertObject(final InternalFactHandle factHandle,
                                 final PropagationContext pctx,
                                 final ReteEvaluator reteEvaluator) {
            System.out.println(getClass().getSimpleName() + ":" + adapterIndex + ":" + factHandle.getObject());

            MultiInputNodeMemory memory = reteEvaluator.getNodeMemory(node);

            LinkedList<DynamicFilter> filters       = memory.getActiveFilters()[adapterIndex];
            if (filters != null) {
                for (DynamicFilter f = filters.getFirst(); f != null; f = f.getNext()) {
                    f.assertObject(factHandle, pctx, reteEvaluator, memory);
                }
            }
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
        public BitMask calculateDeclaredMask(ObjectType modifiedType, List<String> settableProperties) {
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
            return NodeTypeEnums.MultiInputNode; // need to update enums for multi input (mdp)
        }

        public void doAttach(BuildContext context) {
            super.doAttach(context);
            this.source.addObjectSink(this);
        }
    }


//    /**
//     * Used with the updateSink method, so that the parent ObjectSource
//     * can  update the  TupleSink
//     */
//    public static class SequenceInputAdapter extends ObjectSource
//            implements
//            ObjectSinkNode,
//            RightTupleSink {
//        private int            sequenceIndex;
//        private MultiInputNode multiNode;
//
//        public SequenceInputAdapter(int id, ObjectSource source, RuleBasePartitionId partitionId,
//                                    int sequenceIndex, MultiInputNode multiNode) {
//            super(id, source, partitionId);
//            this.sequenceIndex = sequenceIndex;
//            this.multiNode     = multiNode;
//        }
//
//        public void assertObject(final InternalFactHandle factHandle,
//                                 final PropagationContext pctx,
//                                 final ReteEvaluator reteEvaluator) {
//            // I don't do it here, but scope of the multiinput sequence is keyed to the LT.
//            // this code should also wire up the reset call, which for current use case is timeout  (don't hard code it to timeout).
//            SequenceIndexedRightTuple tuple = createTuple( factHandle, this, pctx ); // creates the RightTuple, with the index value of this right input adapter
//
//            multiNode.getSequenceProcessor().receive(tuple);
//
//            //MultiInputMemory memory = reteEvaluator.getNodeMemory(multiNode);
//
//        }
//
//        public SequenceIndexedRightTuple createTuple(InternalFactHandle handle,
//                                                     RightTupleSink sink,
//                                                     PropagationContext context) {
//            SequenceIndexedRightTuple tuple = new SequenceIndexedRightTuple(handle, sink, sequenceIndex);
//            tuple.setPropagationContext( context );
//            return tuple;
//        }
//
//        public void modifyObject(InternalFactHandle factHandle,
//                                 ModifyPreviousTuples modifyPreviousTuples,
//                                 PropagationContext context,
//                                 ReteEvaluator reteEvaluator) {
//
//        }
//
//
//        @Override
//        public void retractRightTuple(TupleImpl rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {
//            // for now assuming we don't have any deletes, but we could add this later.
//        }
//
//        @Override
//        public void modifyRightTuple(TupleImpl rightTuple, PropagationContext context, ReteEvaluator reteEvaluator) {
//            // for now assuming we don't have any modifies, but we could add this later.
//            // typically just gets treated as another add, as the processor is not that stateful (for this use case) - to keep it fast.
//        }
//
//        public int getId() {
//            return 0;
//        }
//
//        public RuleBasePartitionId getPartitionId() {
//            return multiNode.getPartitionId();
//        }
//
//        public void byPassModifyToBetaNode(InternalFactHandle factHandle,
//                                           ModifyPreviousTuples modifyPreviousTuples,
//                                           PropagationContext context,
//                                           ReteEvaluator reteEvaluator) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public ObjectSinkNode getNextObjectSinkNode() {
//            return null;
//        }
//
//        @Override
//        public void setNextObjectSinkNode(ObjectSinkNode next) {
//
//        }
//
//        @Override
//        public ObjectSinkNode getPreviousObjectSinkNode() {
//            return null;
//        }
//
//        @Override
//        public void setPreviousObjectSinkNode(ObjectSinkNode previous) {
//
//        }
//
//        @Override
//        public BitMask calculateDeclaredMask(ObjectType modifiedType, List<String> settableProperties) {
//            return null;
//        }
//
//        @Override
//        public void updateSink(ObjectSink sink, PropagationContext context, InternalWorkingMemory workingMemory) {
//
//        }
//
//        @Override
//        public ObjectTypeNodeId getInputOtnId() {
//            return null;
//        }
//
//        public int getType() {
//            return NodeTypeEnums.MultiInputNode; // need to update enums for multi input (mdp)
//        }
//
//        public int getAssociationsSize() {
//            return multiNode.getAssociationsSize();
//        }
//
//
//        @Override public Rule[] getAssociatedRules() {
//            return multiNode.getAssociatedRules();
//        }
//
//        public boolean isAssociatedWith(Rule rule) {
//            return multiNode.isAssociatedWith( rule );
//        }
//    }

//    public static class PhreakMultiInputNode {
//        public void doNode(MultiInputNode multiInputNode,
//                           MultiInputMemory memory,
//                           LeftTupleSink sink,
//                           ReteEvaluator reteEvaluator,
//                           TupleSets srcLeftTuples,
//                           TupleSets trgLeftTuples,
//                           TupleSets stagedLeftTuples) {
//
//            TupleSets srcRightTuples = memory.getStagedRightTuples().takeAll();
//
//            if (srcLeftTuples.getDeleteFirst() != null) {
//                doLeftDeletes(srcLeftTuples, trgLeftTuples, stagedLeftTuples);
//            }
//
//            if (srcLeftTuples.getUpdateFirst() != null) {
//                // the nature of this is it needs to be a treated as a remove + add
//                // TODO
//            }
//
//            if (srcLeftTuples.getInsertFirst() != null) {
//                doLeftInserts(memory, srcLeftTuples);
//            }
//
//            if (srcRightTuples.getInsertFirst() != null) {
//                doRightInserts(multiInputNode, sink, memory, reteEvaluator, srcRightTuples, trgLeftTuples, stagedLeftTuples);
//            }
//
//            srcLeftTuples.resetAll();
//        }
//
//        private void doRightInserts(MultiInputNode multiInputNode, LeftTupleSink sink, MultiInputMemory memory,
//                                    ReteEvaluator reteEvaluator, TupleSets srcRightTuples,
//                                    TupleSets trgLeftTuples, TupleSets stagedLeftTuples) {
//            FastIterator<TupleImpl> it = memory.getLeftTupleMemory().fastIterator();
//            for ( LeftTuple leftTuple = (LeftTuple) memory.getLeftTupleMemory().getFirst(null); leftTuple != null; leftTuple = (LeftTuple) it.next(leftTuple) ) {
//
//                for (TupleImpl rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
//                    TupleImpl next = rightTuple.getStagedNext();
//
//                    //multiInputNode.sequenceProcessor.receive((SequenceIndexedRightTuple) rightTuple);
//
//                    rightTuple.clearStaged();
//                    rightTuple = next;
//                }
//            }
//
//
//        }
//
//        private void doLeftDeletes(TupleSets srcLeftTuples, TupleSets trgLeftTuples, TupleSets stagedLeftTuples) {
//            for (TupleImpl leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
//                TupleImpl next = leftTuple.getStagedNext();
//                leftTuple.getMemory().remove(leftTuple);
//                leftTuple.setContextObject(null);
//                // TODO add code here to propagate deletion of child LT (mdp)
//
//                leftTuple.clearStaged();
//                leftTuple = next;
//            }
//        }
//
//        private void doLeftInserts(MultiInputMemory memory,
//                                   TupleSets srcLeftTuples) {
//
//            // this only needs to add it to the left memory. As everything is driven by the receiving of right inputs.
//            for (TupleImpl leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
//                TupleImpl next = leftTuple.getStagedNext();
//
//                memory.getLeftTupleMemory().add(leftTuple);
//                leftTuple.setContextObject( new MaskValue());
//
//                leftTuple.clearStaged();
//                leftTuple = next;
//            }
//        }
//    }
}
