package org.drools.core.reteoo.sequencing;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.DynamicFilter;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequencer;
import org.drools.base.reteoo.sequencing.SequencerMemory;
import org.drools.base.reteoo.sequencing.steps.ParallelStep;
import org.drools.base.reteoo.sequencing.steps.Step;
import org.drools.base.reteoo.sequencing.steps.Step.StepType;
import org.drools.base.reteoo.sequencing.steps.SubsequenceStep;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.SequenceNode;
import org.drools.core.reteoo.SequenceNode.SequenceNodeMemory;
import org.drools.base.reteoo.SignalAdapter;
import org.drools.core.reteoo.TupleFactory;
import org.drools.core.reteoo.TupleImpl;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.base.reteoo.sequencing.signalprocessors.ConditionalSignalCounter;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.base.util.CircularArrayList;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.List;

import static org.drools.core.phreak.TupleEvaluationUtil.flushLeftTupleIfNecessary;

public class SequencerMemoryImpl implements SequencerMemory {

    private final TupleImpl lt;

    private final CircularArrayList<Object> events;

    private final SequenceMemory[] sequenceMemories;

    private final LeftTupleSink sink;

    private final ArrayList<SequenceMemory> sequenceStack = new ArrayList<>();

    private Sequencer sequencer;

    private SequenceNode node;

    private SequenceNodeMemory nodeMemory;

    private SequenceMemory childSequenceMemory;

    public SequencerMemoryImpl(Sequencer sequencer, TupleImpl lt, LeftTupleSink sink, SequenceNode node, SequenceNodeMemory nodeMemory) {
        this.sequencer        = sequencer;
        this.lt               = lt;
        this.events           = new CircularArrayList<>(Object.class, 100);
        this.sink             = sink;
        this.sequenceMemories = new SequenceMemory[sequencer.getSequencences().length];
        this.node   = node;
        this.nodeMemory = nodeMemory;
    }

    @Override
    public TupleImpl getLeftTuple() {
        return lt;
    }

    @Override
    public CircularArrayList<Object> getData() {
        return events;
    }

    @Override
    public LeftTupleSink getSink() {
        return sink;
    }

    @Override
    public Sequencer getSequencer() {
        return sequencer;
    }

    @Override
    public SequenceMemory getChildSequenceMemory() {
        return childSequenceMemory;
    }

    @Override
    public void setChildSequenceMemory(SequenceMemory childSequenceMemory) {
        this.childSequenceMemory = childSequenceMemory;
    }

    public SequenceMemory getOrCreateSequenceMemory(SequenceMemory parent, Sequence sequence, CircularArrayList<Object> newData) {
        SequenceMemory sequenceMemory = sequenceMemories[sequence.getSequenceIndex()];
        if (sequenceMemory == null) {

            int signalAdapters = 0;
            int counters       = 0;

            for (LogicGate gate : sequence.getGates()) {
                counters = counters + gate.getInputSignalCounters().length;
                if (gate.getOutput().getClass() == ConditionalSignalCounter.class) {
                    ++counters;
                }
                signalAdapters = signalAdapters + gate.getSignalAdapterIndexes().length;
            }

            long[] gateMemory    = new long[sequence.getGates().length];
            long[] counterMemory = new long[counters];

            CircularArrayList<Object> data = newData == null ? new CircularArrayList<>(1000) : newData;

            sequenceMemory  = new SequenceMemory(this, parent, sequence, data,
                                                  new SignalAdapter[signalAdapters], new SignalAdapter[signalAdapters],
                                                  gateMemory, counterMemory);

            sequenceMemories[sequence.getSequenceIndex()] = sequenceMemory;
        }

        return sequenceMemory;
    }

    @Override
    public SequenceMemory getSequenceMemory(Sequence sequence) {
        SequenceMemory sequenceMemory = sequenceMemories[sequence.getSequenceIndex()];
        return sequenceMemory;
    }


    @Override
    public void match(ValueResolver valueResolver) {
        boolean wasEmpty = nodeMemory.getStagedChildTuples().isEmpty();
        TupleImpl child = TupleFactory.createLeftTuple(lt, sink,  lt.getPropagationContext(), false);
        nodeMemory.getStagedChildTuples().addInsert(child);

        long          nodePosMaskBit = nodeMemory.getNodePosMaskBit();
        SegmentMemory smem           = nodeMemory.getSegmentMemory();
        boolean       shouldFlush    = node.isStreamMode();

        if (wasEmpty) {
            shouldFlush = smem.notifyRuleLinkSegment(valueResolver.as(ReteEvaluator.class), nodePosMaskBit)  | shouldFlush;
        } else {
            shouldFlush = smem.linkSegmentWithoutRuleNotify(nodePosMaskBit) | shouldFlush;
        }

        if (shouldFlush) {
            flushLeftTupleIfNecessary(valueResolver.as(ReteEvaluator.class), smem, node.isStreamMode() );
        }
    }

    @Override
    public DynamicFilter getActiveDynamicFilter(int filterIndex) {
        return nodeMemory.getActiveDynamicFilter(filterIndex);
    }

    @Override
    public void removeActiveFilter(DynamicFilter filter) {
        nodeMemory.removeActiveFilter(filter);
    }
}
