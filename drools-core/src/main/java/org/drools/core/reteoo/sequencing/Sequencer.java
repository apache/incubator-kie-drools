package org.drools.core.reteoo.sequencing;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.MultiInputNode;
import org.drools.core.reteoo.MultiInputNode.MultiInputNodeMemory;
import org.drools.core.reteoo.MultiInputNode.SignalAdapter;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.core.reteoo.sequencing.Step.SequenceStep;
import org.drools.core.util.CircularArrayList;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.List;

public class Sequencer {

    private MultiInputNode node;
    private Sequence       sequence;

    private Sequence[]     sequencences;

    public Sequencer(MultiInputNode node, Sequence sequence) {
        this.node = node;
        this.sequence = sequence;
        this.sequencences = populateSequences(sequence, new ArrayList<>()).stream().toArray(Sequence[]::new);
    }

    public static List<Sequence> populateSequences(Sequence sequence, List<Sequence> list) {
        list.add(sequence);
        for (Step step  : sequence.getSteps()) {
            if (step instanceof SequenceStep) {
                populateSequences(((SequenceStep)step).getSequence(), list);
            }
        }

        return list;
    }

    public Sequence[] getSequencences() {
        return sequencences;
    }

    public void start(SequencerMemory memory, ReteEvaluator reteEvaluator) {
        sequence.start(memory, reteEvaluator);
    }

    public void stop(SequencerMemory memory, ReteEvaluator reteEvaluator) {
        // deactive each active sequence on the stack.
        ArrayList<SequenceMemory>  stack = memory.getSequenceStack();
        for (int i = stack.size()-1; i >= 0; i--) {
            SequenceMemory sequenceMemory = stack.get(i);
            sequenceMemory.getSequence().getSteps()[sequenceMemory.getStep()].deactivate(sequenceMemory, reteEvaluator);
        }
        stack.clear();
    }

    public void next(SequencerMemory sequencerMemory, ReteEvaluator reteEvaluator) {
        SequenceMemory sequenceMemory = sequencerMemory.getCurrentSequence();
        if (sequenceMemory != null) {
            sequenceMemory.getSequence().next(sequenceMemory, reteEvaluator);
        } else {
            // the root sequence has completed
            TupleImpl lt = sequencerMemory.getLeftTuple();
            sequencerMemory.getNodeMemory().getStagedChildTuples().add(new LeftTuple(lt, sequencerMemory.sink,
                                                                                     lt.getPropagationContext(), false));
            // notify?

        }
    }

    public void fail(SequenceMemory memory) {
        //node.fail(memory);
        // TODO reset all memory
    }

    public Sequence getSequence() {
        return sequence;
    }

    public static class SequencerMemory {

        private TupleImpl lt;

        private CircularArrayList<Object> events;

        private MultiInputNode node;

        private SequenceMemory[] sequenceMemories;

        private final LeftTupleSink sink;

        private MultiInputNodeMemory nodeMemory;

        private ArrayList<SequenceMemory> sequenceStack = new ArrayList<>();

        public SequencerMemory(TupleImpl lt, LeftTupleSink sink, MultiInputNode node, MultiInputNodeMemory nodeMemory) {
            this.lt               = lt;
            this.events           = new CircularArrayList<>(FactHandle.class, 100);
            this.node             = node;
            this.sink             = sink;
            this.nodeMemory       = nodeMemory;
            this.sequenceMemories = new SequenceMemory[node.getSequencer().getSequencences().length];

        }

        public TupleImpl getLeftTuple() {
            return lt;
        }

        public CircularArrayList<Object> getEvents() {
            return events;
        }

        public LeftTupleSink getSink() {
            return sink;
        }

        public MultiInputNode getNode() {
            return node;
        }

        public MultiInputNodeMemory getNodeMemory() {
            return nodeMemory;
        }

        public SequenceMemory getCurrentSequence() {
            if (!sequenceStack.isEmpty()) {
                return sequenceStack.get(sequenceStack.size() - 1);
            }

            return null;
        }

        public SequenceMemory popSequence() {
            return sequenceStack.remove(sequenceStack.size()-1);
        }

        public void pushSequence(SequenceMemory sequenceMemory) {
            sequenceStack.add(sequenceMemory);
        }

        public int getCurrentStep() {
            SequenceMemory seq = getCurrentSequence();
            return seq != null ? seq.getStep() : -1;
        }

        public ArrayList<SequenceMemory> getSequenceStack() {
            return sequenceStack;
        }

        public SequenceMemory getSequenceMemory(Sequence sequence) {
            SequenceMemory sequenceMemory = sequenceMemories[sequence.getSequenceIndex()];
            if (sequenceMemory == null) {

                int signalAdapters = 0;
                int counters       = 0;

                for (LogicGate gate : sequence.getGates()) {
                    counters = counters + gate.getInputSignalCounters().length;
                    if ( gate.getOutput().getClass() == ConditionalSignalCounter.class) {
                        ++counters;
                    }
                    signalAdapters = signalAdapters + gate.getSignalAdapterIndexes().length;
                }

                long[] gateMemory    = new long[sequence.getGates().length];
                long[] counterMemory = new long[counters];

                sequenceMemory = new SequenceMemory(this, sequence, new SignalAdapter[signalAdapters], new SignalAdapter[signalAdapters],
                                                    gateMemory, counterMemory, nodeMemory);
                sequenceMemories[sequence.getSequenceIndex()] = sequenceMemory;
            }

            return sequenceMemory;
        }
    }

}
