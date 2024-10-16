package org.drools.core.reteoo.sequencing;

import org.drools.base.reteoo.DynamicFilter;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequencer;
import org.drools.base.reteoo.sequencing.SequencerMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.SequenceNode;
import org.drools.core.reteoo.SequenceNode.SequenceNodeMemory;
import org.drools.base.reteoo.SignalAdapter;
import org.drools.core.reteoo.TupleImpl;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.base.reteoo.sequencing.signalprocessors.ConditionalSignalCounter;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.base.util.CircularArrayList;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;

public class SequencerMemoryImpl implements SequencerMemory {

    private final TupleImpl lt;

    private final CircularArrayList<Object> events;

    private final SequenceMemory[] sequenceMemories;

    private final LeftTupleSink sink;

    private final ArrayList<SequenceMemory> sequenceStack = new ArrayList<>();

    private Sequencer sequencer;

    private SequenceNode node;

    private SequenceNodeMemory nodeMemory;

    public SequencerMemoryImpl(Sequencer sequencer, TupleImpl lt, LeftTupleSink sink, SequenceNode node, SequenceNodeMemory nodeMemory) {
        this.sequencer        = sequencer;
        this.lt               = lt;
        this.events           = new CircularArrayList<>(FactHandle.class, 100);
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
    public CircularArrayList<Object> getEvents() {
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
    public SequenceMemory getCurrentSequence() {
        if (!sequenceStack.isEmpty()) {
            return sequenceStack.get(sequenceStack.size() - 1);
        }

        return null;
    }

    @Override
    public SequenceMemory popSequence() {
        return sequenceStack.remove(sequenceStack.size() - 1);
    }

    @Override
    public void pushSequence(SequenceMemory sequenceMemory) {
        sequenceStack.add(sequenceMemory);
    }

    @Override
    public int getCurrentStep() {
        SequenceMemory seq = getCurrentSequence();
        return seq != null ? seq.getStep() : -1;
    }

    @Override
    public ArrayList<SequenceMemory> getSequenceStack() {
        return sequenceStack;
    }

    @Override
    public SequenceMemory getSequenceMemory(Sequence sequence) {
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

            sequenceMemory                                = new SequenceMemory(this, sequence, new SignalAdapter[signalAdapters], new SignalAdapter[signalAdapters],
                                                                               gateMemory, counterMemory);
            sequenceMemories[sequence.getSequenceIndex()] = sequenceMemory;
        }

        return sequenceMemory;
    }


    @Override
    public void match() {
        nodeMemory.getStagedChildTuples().add(new LeftTuple(lt, sink,
                                                            lt.getPropagationContext(), false));
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
