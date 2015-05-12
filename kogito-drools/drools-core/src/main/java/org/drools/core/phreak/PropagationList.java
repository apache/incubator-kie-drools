package org.drools.core.phreak;

import java.util.Iterator;

public interface PropagationList {
    void addEntry(PropagationEntry propagationEntry);

    void flush();
    void flushNonMarshallable();

    void reset();

    boolean isEmpty();

    Iterator<PropagationEntry> iterator();
}
