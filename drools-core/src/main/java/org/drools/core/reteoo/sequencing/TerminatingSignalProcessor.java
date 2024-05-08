package org.drools.core.reteoo.sequencing;

import org.drools.core.reteoo.sequencing.Sequencer.SequencerMemory;

public class TerminatingSignalProcessor extends SignalProcessor {
    private static TerminatingSignalProcessor INSTANCE = new TerminatingSignalProcessor();

    public static TerminatingSignalProcessor get() {
        return INSTANCE;
    }

    @Override
    public void propagate(SignalStatus signalStatus, SequencerMemory memory) {
        memory.getNode().getSequencer().next(memory);
    }

    @Override
    public void receive(int signalBitIndex, SignalStatus signalStatus, SequencerMemory memory) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void reset(SequencerMemory memory) {
        throw new UnsupportedOperationException();
    }
}
