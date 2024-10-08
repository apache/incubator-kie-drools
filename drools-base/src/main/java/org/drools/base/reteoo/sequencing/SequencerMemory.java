package org.drools.base.reteoo.sequencing;

import org.drools.base.reteoo.BaseTuple;
import org.drools.base.reteoo.DynamicFilter;
import org.drools.base.reteoo.Sink;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.base.util.CircularArrayList;

import java.util.ArrayList;

public interface SequencerMemory {
    BaseTuple getLeftTuple();

    CircularArrayList<Object> getEvents();

    Sink getSink();

    Sequencer getSequencer();

    SequenceMemory getCurrentSequence();

    SequenceMemory popSequence();

    void pushSequence(SequenceMemory sequenceMemory);

    int getCurrentStep();

    ArrayList<SequenceMemory> getSequenceStack();

    SequenceMemory getSequenceMemory(Sequence sequence);

    void match();

    DynamicFilter getActiveDynamicFilter(int filterIndex);

    void removeActiveFilter(DynamicFilter filter);
}
