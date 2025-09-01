package org.drools.base.reteoo.sequencing;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.reteoo.DynamicFilter;
import org.drools.base.reteoo.Sink;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.base.util.CircularArrayList;

public interface SequencerMemory {
    BaseTuple getLeftTuple();

    CircularArrayList<Object> getData();

    Sink getSink();

    Sequencer getSequencer();

    SequenceMemory getChildSequenceMemory();

    void setChildSequenceMemory(SequenceMemory childSequenceMemory);

    SequenceMemory getOrCreateSequenceMemory(SequenceMemory parent, Sequence sequence, CircularArrayList<Object> newData);

    SequenceMemory getSequenceMemory(Sequence sequence);

    void match(ValueResolver valueResolver);

    DynamicFilter getActiveDynamicFilter(int filterIndex);

    void removeActiveFilter(DynamicFilter filter);
}
