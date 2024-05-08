package org.drools.core.reteoo.sequencing;

import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.core.reteoo.sequencing.Sequencer.SequencerMemory;

import java.util.function.Consumer;
import java.util.function.LongPredicate;

public class ConditionalSignalCounter extends SignalProcessor {
    private int           signalIndex;
    private int           counterIndex;
    private LongPredicate constraint;

    private SignalProcessor output;

    public ConditionalSignalCounter(int signalIndex, int counterIndex, LongPredicate constraint) {
        this.signalIndex = signalIndex;
        this.counterIndex = counterIndex;
        this.constraint   = constraint;
    }

    public ConditionalSignalCounter(int signalIndex, int counterIndex, ConstraintTypeOperator operator, long cardinal) {
        this.signalIndex = signalIndex;
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

    public int getSignalIndex() {
        return signalIndex;
    }

    public int getCounterIndex() {
        return counterIndex;
    }

    public SignalProcessor getOutput() {
        return output;
    }

    public void setOutput(SignalProcessor output) {
        this.output = output;
    }

    public void propagate(SignalStatus incommingSignalStatus, SequencerMemory memory) {
        receive(incommingSignalStatus, memory,
                (SignalStatus status) -> output.propagate(status, memory));
    }

    public void receive(int signalBitIndex, SignalStatus incommingSignalStatus, SequencerMemory memory) {
        receive(incommingSignalStatus, memory,
                (SignalStatus status) -> output.receive(signalBitIndex, incommingSignalStatus, memory));
    }

    private void receive(SignalStatus inputSignalStatus, SequencerMemory memory, Consumer<SignalStatus> propagator) {
        SignalStatus status = memory.getCounterSignalStatus(counterIndex);

        SignalStatus priorStatus   = status;
        long         originalCount = memory.getCounterMemory()[counterIndex];
        long         newCount      = ++originalCount;
        memory.getCounterMemory()[counterIndex] = newCount;

        boolean matched = constraint.test(newCount);
        if (matched) {
            status = SignalStatus.MATCHED;
        } else if (priorStatus == SignalStatus.MATCHED) {
            // was matched, now unmatched, so it has failed.
            status = SignalStatus.FAILED;
        }

        memory.setCounterSignalStatus(counterIndex, status);

        if (status == SignalStatus.FAILED) {
            memory.getNode().getSequencer().fail(memory);
        } else if (priorStatus != status) {
            propagator.accept(status);
        }
    }

    LongPredicate ONE = c -> c == 1;

    LongPredicate NONE = c -> c == 0;

    LongPredicate ANY = c -> true;

    public void activate(SequencerMemory memory) {

    }

    public void deactivate(SequencerMemory memory) {

    }

    public void reset(SequencerMemory memory) {
        memory.resetSignalCounterMemory(counterIndex);
    }
}
