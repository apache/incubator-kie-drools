package org.drools.core.reteoo.sequencing;

import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.core.reteoo.sequencing.LogicCircuit.LongBiPredicate;
import org.drools.core.reteoo.sequencing.LogicCircuit.Loop;
import org.drools.core.reteoo.sequencing.Sequencer.SequencerMemory;

import java.util.function.Consumer;
import java.util.function.LongPredicate;

public class LogicGate extends SignalProcessor {
    protected long allMatched;

    private Loop repetition;

    private SignalProcessor output;

    private LongBiPredicate predicate;

    private int gateIndex;

    private LogicGate[] inputGates = EMPTY_INPUT_GATES;

    private int[] filterIndexes;

    private int[] signalAdapterIndexes;

    private static final LogicGate[] EMPTY_INPUT_GATES = new LogicGate[0];

    private static final ConditionalSignalCounter[] EMPTY_CONDITIONAL_SIGNAL_COUNTERS = new ConditionalSignalCounter[0];

    private ConditionalSignalCounter[] inputSignalCounters = EMPTY_CONDITIONAL_SIGNAL_COUNTERS;

    public LogicGate(LongBiPredicate predicate, int gateIndex, int[] filterIndexes, int[] signalAdapterIndexes, int nbrOfInputGates) {
        this.predicate = predicate;

        this.filterIndexes        = filterIndexes;
        this.signalAdapterIndexes = signalAdapterIndexes;

        for (int i = 0; i < (signalAdapterIndexes.length + nbrOfInputGates); i++) {
            allMatched = allMatched | (1 << i);
        }

        this.gateIndex = gateIndex;
    }

    public int getGateIndex() {
        return gateIndex;
    }

    public int[] getFilterIndexes() {
        return filterIndexes;
    }

    public int[] getSignalAdapterIndexes() {
        return signalAdapterIndexes;
    }

    public void setSignalAdapterIndexes(int[] signalAdapterIndexes) {
        this.signalAdapterIndexes = signalAdapterIndexes;
    }

    public LogicGate[] getInputGates() {
        return inputGates;
    }

    public void setInputGates(LogicGate... inputGates) {
        this.inputGates = inputGates;
    }

    public ConditionalSignalCounter[] getInputSignalCounters() {
        return inputSignalCounters;
    }

    public void setInputSignalCounters(ConditionalSignalCounter[] inputSignalCounters) {
        this.inputSignalCounters = inputSignalCounters;
    }

    public SignalProcessor getOutput() {
        return output;
    }

    public void setOutput(SignalProcessor output) {
        this.output = output;
    }

    @Override
    public void propagate(SignalStatus signalStatus, SequencerMemory memory) {
        throw new UnsupportedOperationException();
    }

    public void receive(int signalBitIndex, SignalStatus signalStatus, SequencerMemory memory) {
        SignalStatus status = memory.getLogicGateSignalStatus(gateIndex);

        if (status == SignalStatus.FAILED) {
            // may be placed in FAILED state due to repetition
            return;
        }

        SignalStatus priorStatus = status;

        long currentMatched = memory.getLogicGateMemory()[gateIndex];

        switch (signalStatus) {
            case MATCHED:
                currentMatched = currentMatched | (1 << (signalBitIndex - 1)); // ensures position is on, if it wasn't before. If it was on before, it remains on.
                break;
            case UNMATCHED:
                currentMatched = currentMatched & ~(1 << (signalBitIndex - 1)); // ensures position is off, if it wasn't before. If it was off before, it remains off.
                break;
        }

        memory.getLogicGateMemory()[gateIndex] = currentMatched;

        boolean matched = predicate.test(currentMatched, allMatched);

        if (matched) {
            status = SignalStatus.MATCHED;//repetition.newMatch();
        }
//            else {
//                status = SignalStatus.UNMATCHED;
//            }

        memory.setLogicGateSignalStatus(gateIndex, status);
        if (priorStatus != status) {
            resetPrior(memory);
            output.propagate(status, memory);
        }
    }

    public void resetPrior(SequencerMemory memory) {
        for (LogicGate gate : inputGates) {
            gate.reset(memory);
        }

        memory.resetLogicGateMemory(gateIndex);

        for (ConditionalSignalCounter counter : inputSignalCounters) {
            counter.reset(memory);
        }
    }

    public void reset(SequencerMemory memory) {
        resetPrior(memory);
        output.reset(memory);
    }

    public void activate(SequencerMemory memory) {
        if (memory.getLogicGateSignalStatus()[gateIndex] == null) {
            memory.getLogicGateSignalStatus()[gateIndex] = SignalStatus.UNMATCHED;
        }
        for (int i = 0; i < filterIndexes.length; i++) {
            memory.activateSignalAdapter(filterIndexes[i], this, signalAdapterIndexes[i], i + 1); // bit indexes start at 1
        }
    }

    public void deactivate(SequencerMemory memory) {
        for (int i = 0; i < filterIndexes.length; i++) {
            memory.deactivateSignalAdapter(filterIndexes[i], this, signalAdapterIndexes[i]);
        }

        memory.resetLogicGateMemory(gateIndex);
    }

    public static class ConditionalCounterBase {
        protected int           counterIndex;
        protected LongPredicate constraint;

        public ConditionalCounterBase(int counterIndex, LongPredicate constraint) {
            this.counterIndex = counterIndex;
            this.constraint   = constraint;
        }

        public ConditionalCounterBase(int counterIndex, ConstraintTypeOperator operator, long cardinal) {
            this.counterIndex = counterIndex;
            constraint        = c -> {
                switch (operator) {
                    case EQUAL:
                        return c == cardinal;
                    case NOT_EQUAL:
                        return c != cardinal;
                    case GREATER_THAN:
                        return c > cardinal;
                    case GREATER_OR_EQUAL:
                        return c >= cardinal;
                    case LESS_THAN:
                        return c < cardinal;
                    case LESS_OR_EQUAL:
                        return c <= cardinal;

                }
                throw new IllegalStateException("Unknown operator: " + operator);
            };
        }

        LongPredicate ONE = c -> c == 1;

        LongPredicate NONE = c -> c == 0;

        LongPredicate ANY = c -> true;
    }

    public static class ConditionalInputCounter extends ConditionalCounterBase {
        public ConditionalInputCounter(int counterIndex, LongPredicate constraint) {
            super(counterIndex, constraint);
        }

        public ConditionalInputCounter(int counterIndex, ConstraintTypeOperator operator, long cardinal) {
            super(counterIndex, operator, cardinal);
        }

        private SignalStatus receive(SignalStatus priorStatus, SequencerMemory memory, Consumer<SignalStatus> propagator) {
            //SignalStatus status = memory.getCounterSignalStatus(counterIndex);

            SignalStatus status   = priorStatus;
            long         originalCount = memory.getCounterMemory()[counterIndex];
            long         newCount      = ++originalCount;
            memory.getCounterMemory()[counterIndex] = newCount;

            boolean matched = constraint.test(newCount);
            if (matched) {
                status = SignalStatus.MATCHED; //repetition.newMatch();
            }

            return status;
        }
    }

    public static class ConditionalOutputCounter extends ConditionalCounterBase {

        public ConditionalOutputCounter(int counterIndex, LongPredicate constraint) {
            super(counterIndex, constraint);
        }

        public ConditionalOutputCounter(int counterIndex, ConstraintTypeOperator operator, long cardinal) {
            super(counterIndex, operator, cardinal);
        }
    }

//    public class ConditionalSignalCounter {
//        private LogicGate     gate;
//        private int           counterIndex;
//        private LongPredicate constraint;
//
//        private Output output;
//
//        public ConditionalSignalCounter(int counterIndex, LongPredicate constraint) {
//            this.counterIndex = counterIndex;
//            this.constraint   = constraint;
//        }
//
//        public ConditionalSignalCounter(int counterIndex, ConstraintTypeOperator operator, long cardinal) {
//            this.counterIndex = counterIndex;
//            constraint        = c -> {
//                switch (operator) {
//                    case EQUAL:
//                        return c == cardinal;
//                    case NOT_EQUAL:
//                        return c != cardinal;
//                    case GREATER_THAN:
//                        return c > cardinal;
//                    case GREATER_OR_EQUAL:
//                        return c >= cardinal;
//                    case LESS_THAN:
//                        return c < cardinal;
//                    case LESS_OR_EQUAL:
//                        return c <= cardinal;
//
//                }
//                throw new IllegalStateException("Unknown operator: " + operator);
//            };
//        }
//
//        public LogicGate getGate() {
//            return gate;
//        }
//
//        public void setGate(LogicGate gate) {
//            this.gate = gate;
//        }
//
//        public Output getOutput() {
//            return output;
//        }
//
//        public void setOutput(Output output) {
//            this.output = output;
//        }
//
//        public void receive(SignalStatus incommingSignalStatus, SequencerMemory memory) {
//            receive(incommingSignalStatus, memory,
//                    (SignalStatus status) -> output.propagate(status, memory));
//        }
//
//        private void receive(SignalStatus incommingSignalStatus, SequencerMemory memory, Consumer<SignalStatus> propagator) {
//            SignalStatus status = memory.getCounterSignalStatus(counterIndex);
//
//            SignalStatus priorStatus   = status;
//            long         originalCount = memory.getCounterMemory()[counterIndex];
//            long         newCount      = ++originalCount;
//            memory.getCounterMemory()[counterIndex] = newCount;
//
//            boolean matched = constraint.test(newCount);
//            if (matched) {
//                status = SignalStatus.MATCHED;//repetition.newMatch();
//            }
//
//            if (priorStatus != status) {
//                propagator.accept(status);
//            }
//        }
//
//        public void receive(int signalBitIndex, SignalStatus incommingSignalStatus, SequencerMemory memory) {
//            receive(incommingSignalStatus, memory,
//                    (SignalStatus status) -> gate.receive(signalBitIndex, incommingSignalStatus, memory));
//        }
//
//        LongPredicate ONE = c -> c == 1;
//
//        LongPredicate NONE = c -> c == 0;
//
//        LongPredicate ANY = c -> true;
//
//        public void activate(SequencerMemory memory) {
//
//        }
//
//        public void deactivate(SequencerMemory memory) {
//
//        }
//    }
}
