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
    public void addEntry(PropagationEntry propagationEntry) {
        propagationEntry.execute(workingMemory);
    }

    @Override
    public PropagationEntry takeAll() {
        return null;
    }

    @Override
    public void flush() { }

    @Override
    public void flushNonMarshallable() { }

    @Override
    public void flushOnFireUntilHalt(boolean fired) {
        flushOnFireUntilHalt( fired, null );
    }

    @Override
    public void flushOnFireUntilHalt( boolean fired, PropagationEntry currentHead ) {
        if ( !fired ) {
            synchronized ( this ) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    // nothing to do
                }
            }
        }
        flush();
    }

    @Override
    public void notifyHalt() {
        synchronized ( this ) {
            this.notifyAll();
        }
    }

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
