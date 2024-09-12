package org.drools.base.reteoo.sequencing.steps;

import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;

import java.util.function.Consumer;

public class AggregatorStep extends SubsequenceStep implements Step {
    public AggregatorStep(int index, Sequence sequence, Sequence subsequence, Consumer<SequenceMemory> consumer) {
        super(index, sequence, subsequence);
        subsequence.setOnEnd(consumer);
    }
}
