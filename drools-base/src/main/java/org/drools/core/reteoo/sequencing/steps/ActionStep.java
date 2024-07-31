package org.drools.core.reteoo.sequencing.steps;

import org.drools.base.base.ValueResolver;
import org.drools.core.reteoo.sequencing.Sequence;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;

import java.util.function.Consumer;

public class ActionStep implements Step {
    protected     Sequence                 parentSequence;
    private final Consumer<SequenceMemory> consumer;

    public ActionStep(Sequence parentSequence, Consumer<SequenceMemory> consumer) {
        this.parentSequence = parentSequence;
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

    @Override
    public Sequence getParentSequence() {
        return parentSequence;
    }
}
