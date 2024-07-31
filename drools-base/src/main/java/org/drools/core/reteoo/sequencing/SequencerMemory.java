package org.drools.core.reteoo.sequencing;

import org.drools.base.reteoo.BaseTuple;
import org.drools.core.reteoo.DynamicFilter;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.core.util.CircularArrayList;

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
