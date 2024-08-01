package org.drools.core.reteoo.sequencing.steps;

import org.drools.base.base.ValueResolver;
import org.drools.core.reteoo.sequencing.Sequence;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.core.reteoo.sequencing.SequencerMemory;

public abstract class AbstractStep implements Step {

    protected final int index;
    protected final Sequence parentSequence;
    protected StepFailureHandler failureHandler = FailStackFailureHandler.getInstance();

    public AbstractStep(int index, Sequence parentSequence) {
        this.index          = index;
        this.parentSequence = parentSequence;
    }

    public int getIndex() {
        return index;
    }

    public Sequence getParentSequence() {
        return parentSequence;
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
            sequencerMemory.getSequencer().stop(sequencerMemory, valueResolver);
        }
    }
}
