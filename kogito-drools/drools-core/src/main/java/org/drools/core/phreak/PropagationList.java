package org.drools.core.phreak;

import java.util.Iterator;

public interface PropagationList {
    void addEntry(PropagationEntry propagationEntry);

    PropagationEntry takeAll();

    void flush();

    void flushNonMarshallable();

    void flushOnFireUntilHalt( boolean fired );
    void flushOnFireUntilHalt( boolean fired, PropagationEntry currentHead );

    void reset();

    boolean isEmpty();

    Iterator<PropagationEntry> iterator();

    void notifyHalt();
}
