package org.drools.base.reteoo.sequencing.steps;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;

import java.util.function.Consumer;

public class ActionStep extends AbstractStep implements Step {
    private final Consumer<SequenceMemory> consumer;

    public ActionStep(int index, Sequence parentSequence, Consumer<SequenceMemory> consumer) {
        super(index, parentSequence);
        this.consumer       = consumer;
    }

    @Override
    public void activate(SequenceMemory memory, ValueResolver valueResolver) {
        consumer.accept(memory);
        memory.getSequence().next(memory, valueResolver); // transitions as soon as the action is fired
    }

    @Override
    public void deactivate(SequenceMemory memory, ValueResolver valueResolver) {
    }
}
