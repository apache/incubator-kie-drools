package org.drools.base.reteoo.sequencing.steps;

import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;

import java.util.function.Consumer;

public class AggregatorStep extends SequenceStep implements Step {
    public AggregatorStep(int index, Sequence parentSequence, Sequence sequence, Consumer<SequenceMemory> consumer) {
        super(index, parentSequence, sequence);
        sequence.setOnEnd(consumer);
    }
}
