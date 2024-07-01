package org.drools.core.reteoo.sequencing;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;

public class TerminatingSignalProcessor extends SignalProcessor {
    private static TerminatingSignalProcessor INSTANCE = new TerminatingSignalProcessor();

    public static TerminatingSignalProcessor get() {
        return INSTANCE;
    }

    @Override
    public void consume(SignalStatus signalStatus, SequenceMemory memory, ReteEvaluator reteEvaluator) {
        memory.getSequence().next(memory, reteEvaluator);
    }

    @Override
    public void consume(int signalBitIndex, SignalStatus signalStatus, SequenceMemory memory, ReteEvaluator reteEvaluator) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void reset(SequenceMemory memory, ReteEvaluator reteEvaluator) {
        throw new UnsupportedOperationException();
    }
}
