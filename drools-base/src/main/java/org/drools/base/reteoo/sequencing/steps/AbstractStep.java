package org.drools.base.reteoo.sequencing.steps;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.base.reteoo.sequencing.SequencerMemory;

public abstract class AbstractStep implements Step {

    protected final int                index;
    protected final Sequence           sequence; // the sequence the step is in
    protected       StepFailureHandler failureHandler = FailStackFailureHandler.getInstance();
    protected final StepType           type;

    public AbstractStep(StepType type, int index, Sequence sequence) {
        this.type = type;
        this.index    = index;
        this.sequence = sequence;
    }

    public StepType getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public Sequence getSequence() {
        return sequence;
    }

    @Override
    public void onFail(SequenceMemory memory, ValueResolver valueResolver) {
        failureHandler.onFail(this, memory, valueResolver);
    }

    public interface StepFailureHandler {
        void onFail(Step step, SequenceMemory memory, ValueResolver valueResolver);
    }

    public static class FailStackFailureHandler implements StepFailureHandler {

        public static final FailStackFailureHandler INSTANCE = new FailStackFailureHandler();

        public static FailStackFailureHandler getInstance() {
            return INSTANCE;
        }

        @Override
        public void onFail(Step step, SequenceMemory sequenceMemory, ValueResolver valueResolver) {
            SequencerMemory sequencerMemory = sequenceMemory.getSequencerMemory();
            step.deactivate(sequenceMemory, valueResolver);
            sequencerMemory.getSequencer().stop(sequenceMemory, valueResolver);
        }
    }
}
