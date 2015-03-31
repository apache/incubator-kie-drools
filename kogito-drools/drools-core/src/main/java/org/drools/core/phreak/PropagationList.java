package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;

import java.util.Iterator;

public interface PropagationList {
    boolean addEntry(PropagationEntry propagationEntry);

    void flush(InternalWorkingMemory workingMemory);
    void flushNonMarshallable(InternalWorkingMemory workingMemory);

    void reset();

    boolean isEmpty();

    Iterator<PropagationEntry> iterator();
}
