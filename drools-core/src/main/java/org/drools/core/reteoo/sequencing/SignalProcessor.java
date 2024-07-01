package org.drools.core.reteoo.sequencing;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;

public abstract class SignalProcessor {

    public abstract void consume(SignalStatus signalStatus, SequenceMemory memory, ReteEvaluator reteEvaluator);

    public abstract void consume(int signalBitIndex, SignalStatus signalStatus, SequenceMemory memory, ReteEvaluator reteEvaluator);

    protected abstract void reset(SequenceMemory memory, ReteEvaluator reteEvaluator);
}
