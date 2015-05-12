package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;

import java.util.Iterator;

public class SynchronizedPropagationList implements PropagationList {
    private final InternalWorkingMemory workingMemory;

    private volatile PropagationEntry head;
    private volatile PropagationEntry tail;

    public SynchronizedPropagationList(InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    @Override
    public void addEntry(final PropagationEntry entry) {
        if (entry.requiresImmediateFlushingIfNotFiring()) {
            boolean executed = workingMemory.getAgenda().executeIfNotFiring( new Runnable() {
                @Override
                public void run() {
                    ( (PhreakTimerNode.TimerAction) entry ).execute( workingMemory, true );
                }
            } );
            if (executed) {
                return;
            }
        }

        synchronized (this) {
            boolean wasEmpty = head == null;
            if ( wasEmpty ) {
                head = entry;
            } else {
                tail.setNext( entry );
            }
            tail = entry;

            if ( wasEmpty ) {
                workingMemory.getAgenda().notifyHalt();
            }
        }
    }

    @Override
    public void flush() {
        while (head != null) {
            internalFlush();
        }
    }

    private void internalFlush() {
        PropagationEntry currentHead;
        synchronized (this) {
            currentHead = head;
            head = null;
            tail = null;
        }
        for (PropagationEntry entry = currentHead; entry != null; entry = entry.getNext()) {
            entry.execute(workingMemory);
        }
    }

    @Override
    public synchronized void flushNonMarshallable() {
        PropagationEntry newHead = null;
        PropagationEntry newTail = null;
        for (PropagationEntry entry = head; entry != null; entry = entry.getNext()) {
            if (entry.isMarshallable()) {
                if (newHead == null) {
                    newHead = entry;
                } else {
                    newTail.setNext(entry);
                }
                newTail = entry;
            } else {
                entry.execute(workingMemory);
            }
        }
        head = newHead;
        tail = newTail;
    }

    @Override
    public synchronized void reset() {
        head = null;
        tail = null;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    public synchronized Iterator<PropagationEntry> iterator() {
        return new PropagationEntryIterator(head);
    }

    public static class PropagationEntryIterator implements Iterator<PropagationEntry> {

        private PropagationEntry next;

        public PropagationEntryIterator(PropagationEntry head) {
            this.next = head;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public PropagationEntry next() {
            PropagationEntry current = next;
            next = current.getNext();
            return current;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
