package org.drools.core.reteoo.sequencing.signalprocessors;

import org.drools.base.base.ValueResolver;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;

public abstract class SignalProcessor {

    public abstract void consume(SignalStatus signalStatus, SequenceMemory memory, ValueResolver valueResolver);

    public abstract void consume(int signalBitIndex, SignalStatus signalStatus, SequenceMemory memory, ValueResolver valueResolver);

    protected abstract void reset(SequenceMemory memory, ValueResolver valueResolver);
}
