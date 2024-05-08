package org.drools.core.reteoo.sequencing.steps;

import org.drools.base.base.ValueResolver;
import org.drools.core.reteoo.sequencing.Sequence;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.core.util.CircularArrayList;

public class SequenceStep extends AbstractStep implements Step {
    protected Sequence sequence;

    public SequenceStep(int index, Sequence parentSequence, Sequence sequence) {
        super(index, parentSequence);
        this.sequence       = sequence;
    }

    public Sequence getSequence() {
        return sequence;
    }

    @Override
    public void activate(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
        if (parentSequence != null) {
            // reserved for any context or return data
            // Also in the future it could be used to optional collect and hold nested array of subevents for later reference.
            CircularArrayList<Object> events = sequenceMemory.getSequencerMemory().getEvents();

            SequenceMemory subSequenceMemory = sequenceMemory.getSequencerMemory().getSequenceMemory(sequence);
            events.addEmpty(subSequenceMemory.getSequence().getOutputSize());
            subSequenceMemory.setEventsStartPosition(sequenceMemory.getSequencerMemory().getEvents().size());
        }
        sequence.start(sequenceMemory.getSequencerMemory(), valueResolver);
    }

    @Override
    public void deactivate(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
        if (parentSequence != null) {
            SequenceMemory            subSequenceMemory = sequenceMemory.getSequencerMemory().getSequenceMemory(sequence);
            CircularArrayList<Object> events            = sequenceMemory.getSequencerMemory().getEvents();
            events.resetHeadByOffset(events.size() - subSequenceMemory.getEventsStartPosition());
        }
    }
}
