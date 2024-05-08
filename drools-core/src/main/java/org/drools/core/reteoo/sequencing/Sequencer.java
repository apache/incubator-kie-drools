package org.drools.core.reteoo.sequencing;

import org.drools.core.reteoo.MultiInputNode;
import org.drools.core.reteoo.MultiInputNode.DynamicFilter;
import org.drools.core.reteoo.MultiInputNode.MultiInputNodeMemory;
import org.drools.core.reteoo.MultiInputNode.SignalAdapter;

import java.util.ArrayList;

public class Sequencer {

    private MultiInputNode node;
    private Sequence       sequence;

    public Sequencer(MultiInputNode node, Sequence sequence) {
        this.node = node;
        this.sequence = sequence;
    }

    public void start(SequencerMemory memory) {
        start(sequence, memory);
    }

    public void start(Sequence sequence, SequencerMemory memory) {
        memory.pushCurrentSequence(new SequenceMemory(sequence));
        sequence.getSteps()[0].activate(memory);
    }

    public void next(SequencerMemory memory) {
        SequenceMemory currentSequence = memory.getCurrentSequence();
        currentSequence.getSequence().getSteps()[currentSequence.getStep()].deactivate(memory);
        int step = currentSequence.incrementStep();

        if (step < currentSequence.getSequence().getSteps().length) {
            currentSequence.getSequence().getSteps()[step].activate(memory);
        } else {
            memory.popCurrentSequence();
            if ( memory.getCurrentSequence() != null) {
                next(memory);
            } else {
                terminate(memory);
            }
        }
    }

    public void terminate(SequencerMemory memory) {
        System.out.println("Terminated");
    }

    public void fail(SequencerMemory memory) {
        node.fail(memory);
        // TODO reset all memory
    }


    public  interface Repeater {

    }


    public void complete() {

    }

    public static class SequenceMemory {
        private Sequence sequence;
        private int step;

        public SequenceMemory(Sequence sequence) {
            this.sequence = sequence;
        }

        public Sequence getSequence() {
            return sequence;
        }

        public int incrementStep() {
            return ++step;
        }

        public int getStep() {
            return step;
        }
    }

    public static class SequencerMemory {
        private SignalAdapter[] signalAdapters;

        private SignalAdapter[] activeSignalAdapters;

        private long[] gateMemory;

        private long[] counterMemory;

        private SignalStatus[] signalStatuses;

        private MultiInputNode node;

        private MultiInputNodeMemory nodeMemory;

        private ArrayList<SequenceMemory> sequenceStack = new ArrayList<>();

        public SequencerMemory(MultiInputNode node, MultiInputNodeMemory nodeMemory, long[] gateMemory, long[] counterMemory,
                               SignalAdapter[] signalAdapters, SignalAdapter[] activeSignalAdapters) {
            this.node = node;
            this.gateMemory = gateMemory;
            this.counterMemory = counterMemory;
            this.nodeMemory = nodeMemory;
            this.signalAdapters = signalAdapters;
            this.activeSignalAdapters = activeSignalAdapters;
            this.signalStatuses       = new SignalStatus[gateMemory.length + counterMemory.length];
        }

        public SignalAdapter activateSignalAdapter(int filterIndex, LogicGate gate, int signalAdapterIndex, int signalBitIndex) {
            if (activeSignalAdapters[signalAdapterIndex] != null) {
                throw new RuntimeException("Defensive coding, this should not be re-entrant");
            }

            SignalAdapter signalAdapter = signalAdapters[signalAdapterIndex];

            if (signalAdapter == null) {
                ConditionalSignalCounter counter = null;
                for ( ConditionalSignalCounter c : gate.getInputSignalCounters()) {
                    if ( c.getSignalIndex() == signalAdapterIndex) {
                        counter = c;
                        break;
                    }
                }
                signalAdapter = new SignalAdapter(counter == null ? gate : counter, signalBitIndex, this);
                signalAdapters[signalAdapterIndex] = signalAdapter;
            }

            activeSignalAdapters[signalAdapterIndex] = signalAdapter;

            DynamicFilter filter = nodeMemory.getActiveDynamicFilter(filterIndex);
            filter.addSignalAdapter(signalAdapter);

            return signalAdapter;
        }

        public void deactivateSignalAdapter(int filterIndex, LogicGate gate, int signalAdapterIndex) {
            SignalAdapter signalAdapter = activeSignalAdapters[signalAdapterIndex];
            activeSignalAdapters[signalAdapterIndex] = null;

            DynamicFilter filter = nodeMemory.getActiveDynamicFilter(filterIndex);
            filter.removeSignalAdapter(signalAdapter);

            if (filter.getSignalAdapters().isEmpty()) {
                nodeMemory.removeActiveFilter(filter);
            }
        }

        public long[] getLogicGateMemory() {
            return gateMemory;
        }

        public SignalStatus getLogicGateSignalStatus(int index) {
            return signalStatuses[index];
        }

        public void setLogicGateSignalStatus(int index, SignalStatus status) {
            signalStatuses[index] = status;
        }

        public SignalStatus getCounterSignalStatus(int index) {
            return signalStatuses[gateMemory.length + index];
        }

        public void setCounterSignalStatus(int index, SignalStatus status) {
            signalStatuses[gateMemory.length + index] = status;
        }

        public SignalStatus[] getLogicGateSignalStatus() {
            return signalStatuses;
        }

        public void setLogicGateMemory(long[] logicGateMemory) {
            this.gateMemory = logicGateMemory;
        }

        public long[] getCounterMemory() {
            return counterMemory;
        }

        public MultiInputNode getNode() {
            return node;
        }

        public void resetLogicGateMemory(int gateIndex) {
            gateMemory[gateIndex]     = 0;
            signalStatuses[gateIndex] = null;
        }

        public void resetSignalCounterMemory(int counterIndex) {
            signalStatuses[gateMemory.length + counterIndex] = null;
            counterMemory[counterIndex] = 0;
        }


        public SignalAdapter[] getSignalAdapters() {
            return signalAdapters;
        }

        public SignalAdapter[] getActiveSignalAdapters() {
            return activeSignalAdapters;
        }

        public long[] getGateMemory() {
            return gateMemory;
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

        public SequenceMemory popCurrentSequence() {
            return sequenceStack.remove(sequenceStack.size()-1);
        }

        public void pushCurrentSequence(SequenceMemory sequenceMemory) {
            sequenceStack.add(sequenceMemory);
        }

        public int getCurrentStep() {
            SequenceMemory seq = getCurrentSequence();
            return seq != null ? seq.getStep() : -1;
        }
    }
}
