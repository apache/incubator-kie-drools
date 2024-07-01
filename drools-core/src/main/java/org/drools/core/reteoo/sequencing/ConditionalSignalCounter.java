package org.drools.core.reteoo.sequencing;

import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;

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

    @Override
    public void consume(SignalStatus incommingSignalStatus, SequenceMemory memory, ReteEvaluator reteEvaluator) {
        consume(incommingSignalStatus, memory,
                (SignalStatus status) -> output.consume(status, memory, reteEvaluator));
    }

    @Override
    public void consume(int signalBitIndex, SignalStatus incommingSignalStatus, SequenceMemory memory, ReteEvaluator reteEvaluator) {
        consume(incommingSignalStatus, memory,
                (SignalStatus status) -> output.consume(signalBitIndex, incommingSignalStatus, memory, reteEvaluator));
    }

    private void consume(SignalStatus inputSignalStatus, SequenceMemory memory, Consumer<SignalStatus> propagator) {
        SignalStatus status = memory.getCounterSignalStatus(counterIndex);

        SignalStatus priorStatus   = status;
        long         originalCount = memory.getCounterMemories()[counterIndex];
        long         newCount      = ++originalCount;
        memory.getCounterMemories()[counterIndex] = newCount;

        boolean matched = constraint.test(newCount);
        if (matched) {
            status = SignalStatus.MATCHED;
        } else if (priorStatus == SignalStatus.MATCHED) {
            // was matched, now unmatched, so it has failed.
            status = SignalStatus.FAILED;
        }

        memory.setCounterSignalStatus(counterIndex, status);

        if (status == SignalStatus.FAILED) {
            memory.getSequencerMemory().getNode().getSequencer().fail(memory);
        } else if (priorStatus != status) {
            propagator.accept(status);
        }
    }

    LongPredicate ONE = c -> c == 1;

    LongPredicate NONE = c -> c == 0;

    LongPredicate ANY = c -> true;

    public void reset(SequenceMemory memory, ReteEvaluator reteEvaluator) {
        memory.resetSignalCounterMemory(counterIndex);
    }
}
