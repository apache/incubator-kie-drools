package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;

import java.util.Iterator;

public class SynchronizedPropagationList implements PropagationList {
    private volatile PropagationEntry head;
    private volatile PropagationEntry tail;

    @Override
    public synchronized boolean addEntry(PropagationEntry entry) {
        boolean wasEmpty = head == null;
        if (wasEmpty) {
            head = entry;
        } else {
            tail.setNext(entry);
        }
        tail = entry;
        return wasEmpty;
    }

    @Override
    public void flush(InternalWorkingMemory workingMemory) {
        while (head != null) {
            internalFlush(workingMemory);
        }
    }

    private void internalFlush(InternalWorkingMemory workingMemory) {
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
    public synchronized void flushNonMarshallable(InternalWorkingMemory workingMemory) {
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
