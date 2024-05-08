package org.drools.core.reteoo.sequencing;

import org.drools.core.reteoo.sequencing.Sequencer.SequencerMemory;

public abstract class SignalProcessor {

    public abstract void propagate(SignalStatus signalStatus, SequencerMemory memory);

    public abstract void receive(int signalBitIndex, SignalStatus signalStatus, SequencerMemory memory);

    protected abstract void reset(SequencerMemory memory);
}
