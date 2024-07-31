package org.drools.core.reteoo.sequencing.steps;

import org.drools.core.reteoo.sequencing.Sequence;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;

import java.util.function.Consumer;

public class AggregatorStep extends SequenceStep implements Step {
    public AggregatorStep(Sequence parentSequence, Sequence sequence, Consumer<SequenceMemory> consumer) {
        super(parentSequence, sequence);
        sequence.setOnEnd(consumer);
    }
}
