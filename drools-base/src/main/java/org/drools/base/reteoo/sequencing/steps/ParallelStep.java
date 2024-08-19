package org.drools.base.reteoo.sequencing.steps;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.base.reteoo.sequencing.SequencerMemory;
import org.drools.base.util.CircularArrayList;

import java.util.Arrays;

public class ParallelStep extends AbstractStep implements Step {
    private SequenceStep[] sequences;

    private int outputSize;

    public ParallelStep(int index, int outputSize, Sequence parentSequence, SequenceStep... sequences) {
        super(index, parentSequence);
        this.outputSize = outputSize;
        this.sequences = sequences;
        Arrays.stream(sequences).forEach(s -> {
            if (s.getSequence().getOutputSize() > outputSize) {
                throw new IllegalArgumentException("The subprocess output size must not be more than the parallel output size");
            }
        });
    }

    @Override
    public void activate(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
        CircularArrayList<Object> events = sequenceMemory.getSequencerMemory().getEvents();
        SequencerMemory sequencerMemory = sequenceMemory.getSequencerMemory();

        events.addEmpty(outputSize);

        int eventsStartPosition = events.size();

        // make space for each
        for (int i = 0; i < this.sequences.length; i++) {
            SequenceMemory subSequenceMemory = sequencerMemory.getSequenceMemory(sequences[i].getSequence());
            subSequenceMemory.setEventsStartPosition(eventsStartPosition);
            events.addEmpty(outputSize);
        }

        for (int i = 0; i < this.sequences.length; i++) {
            sequences[i].getSequence().start(sequencerMemory, valueResolver);
        }
    }

    @Override
    public void deactivate(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
        CircularArrayList<Object> events            = sequenceMemory.getSequencerMemory().getEvents();

        // we only need the first sub process to get the start position
        SequenceMemory subSequenceMemory = sequenceMemory.getSequencerMemory().getSequenceMemory(sequences[0].getSequence());
        events.resetHeadByOffset(events.size() - subSequenceMemory.getEventsStartPosition());
    }
}
