package org.drools.core.phreak;

import java.util.Iterator;

public interface PropagationList {
    void addEntry(PropagationEntry propagationEntry);

    PropagationEntry takeAll();

    void flush();
    void flush( PropagationEntry currentHead );

    void reset();

    boolean isEmpty();

    boolean hasEntriesDeferringExpiration();

    Iterator<PropagationEntry> iterator();

    void waitOnRest();

    void notifyWaitOnRest();

    void onEngineInactive();

    void dispose();

    void setFiringUntilHalt( boolean firingUntilHalt );
}
