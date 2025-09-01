package org.drools.base.reteoo.sequencing.steps;

import org.drools.base.base.ValueResolver;
import org.drools.base.util.CircularArrayList;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;

public class SubsequenceStep extends AbstractStep implements Step {
    protected Sequence subsequence;

    public SubsequenceStep(int index, Sequence sequence, Sequence subsequence) {
        super(StepType.SUB_SEQUENCE, index, sequence);
        this.subsequence = subsequence;
    }

    public Sequence getSubsequence() {
        return subsequence;
    }

    @Override
    public void activate(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
        if (sequence != null) {
            // reserved for any context or return data
            // Also in the future it could be used to optional collect and hold nested array of subevents for later reference.
            CircularArrayList<Object> data = sequenceMemory.getData();

            SequenceMemory subSequenceMemory = sequenceMemory.getSequencerMemory().getOrCreateSequenceMemory(sequenceMemory, subsequence, data);
            data.addEmpty(subSequenceMemory.getSequence().getOutputSize());
            subSequenceMemory.setEventsStartPosition(sequenceMemory.getData().size());
            data.add(subSequenceMemory);
        }
        SequenceMemory subSequenceMemory = sequenceMemory.getSequencerMemory().getSequenceMemory(subsequence);

        subsequence.start(subSequenceMemory, valueResolver);
    }

    @Override
    public void deactivate(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
        if (sequence != null) {
            SequenceMemory            subSequenceMemory = sequenceMemory.getSequencerMemory().getSequenceMemory(subsequence);
            CircularArrayList<Object> events            = sequenceMemory.getData();
            events.resetHeadByOffset(events.size() - subSequenceMemory.getEventsStartPosition());
        }
    }
}
