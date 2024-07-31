package org.drools.core.reteoo.sequencing.signalprocessors;

import org.drools.base.base.ValueResolver;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;

public class TerminatingSignalProcessor extends SignalProcessor {
    private static final TerminatingSignalProcessor INSTANCE = new TerminatingSignalProcessor();

    public static TerminatingSignalProcessor get() {
        return INSTANCE;
    }

    @Override
    public void consume(SignalStatus signalStatus, SequenceMemory memory, ValueResolver valueResolver) {
        memory.getSequence().next(memory, valueResolver);
    }

    @Override
    public void consume(int signalBitIndex, SignalStatus signalStatus, SequenceMemory memory, ValueResolver valueResolver) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void reset(SequenceMemory memory, ValueResolver valueResolver) {
        throw new UnsupportedOperationException();
    }
}
