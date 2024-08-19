package org.drools.base.reteoo.sequencing.signalprocessors;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;

public class TerminatingSignalProcessor extends SignalProcessor {
    private static final TerminatingSignalProcessor MATCH = new TerminatingSignalProcessor(true);
    private static final TerminatingSignalProcessor UNMATCH = new TerminatingSignalProcessor(false);

    private boolean match;

    public TerminatingSignalProcessor(boolean match) {
        this.match  = match;
    }

    public static TerminatingSignalProcessor get() {
        return MATCH;
    }

    public static TerminatingSignalProcessor getMatch() {
        return MATCH;
    }

    public static TerminatingSignalProcessor getUnmatch() {
        return UNMATCH;
    }


    @Override
    public void consume(SignalStatus signalStatus, SequenceMemory memory, ValueResolver valueResolver) {
        if (match) {
            memory.getSequence().next(memory, valueResolver);
        } else {
            memory.getSequence().fail(memory, valueResolver);
        }
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
