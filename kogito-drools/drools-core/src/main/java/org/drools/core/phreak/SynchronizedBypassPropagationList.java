package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;

import java.util.Collections;
import java.util.Iterator;

public class SynchronizedBypassPropagationList implements PropagationList {

    private final InternalWorkingMemory workingMemory;

    public SynchronizedBypassPropagationList(InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    @Override
    public synchronized boolean addEntry(PropagationEntry propagationEntry) {
        propagationEntry.execute(workingMemory);
        return true;
    }

    @Override
    public void flush(InternalWorkingMemory workingMemory) { }

    @Override
    public void flushNonMarshallable(InternalWorkingMemory workingMemory) { }

    @Override
    public void reset() { }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Iterator<PropagationEntry> iterator() {
        return Collections.<PropagationEntry>emptyList().iterator();
    }
}
