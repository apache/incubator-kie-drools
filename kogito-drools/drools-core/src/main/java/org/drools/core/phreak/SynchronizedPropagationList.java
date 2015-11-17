/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;

import java.util.Iterator;

public class SynchronizedPropagationList implements PropagationList {

    protected final InternalWorkingMemory workingMemory;

    protected volatile PropagationEntry head;
    protected volatile PropagationEntry tail;

    public SynchronizedPropagationList(InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    @Override
    public void addEntry(final PropagationEntry entry) {
        if (entry.requiresImmediateFlushing()) {
            workingMemory.getAgenda().executeTask( new ExecutableEntry() {
                @Override
                public void execute() {
                    ( (PhreakTimerNode.TimerAction) entry ).execute( workingMemory, true );
                }

                @Override
                public void enqueue() {
                    internalAddEntry( entry );
                }
            } );
        } else {
            internalAddEntry( entry );
        }
    }

    @Override
    public synchronized void addEntryToTop(final PropagationEntry entry) {
        boolean wasEmpty = head == null;
        if ( !wasEmpty ) {
            entry.setNext( head );
            tail = head;
        }
        head = entry;

        if ( wasEmpty ) {
            notifyHalt();
        }
    }

    protected synchronized void internalAddEntry( PropagationEntry entry ) {
        boolean wasEmpty = head == null;
        if ( wasEmpty ) {
            head = entry;
        } else {
            tail.setNext( entry );
        }
        tail = entry;

        if ( wasEmpty ) {
            notifyHalt();
        }
    }

    @Override
    public void flush() {
        for ( PropagationEntry currentHead = takeAll(); currentHead != null; currentHead = takeAll() ) {
            flush( workingMemory, currentHead );
        }
    }

    @Override
    public void flushOnFireUntilHalt(boolean fired) {
        flushOnFireUntilHalt( fired, takeAll() );
    }

    @Override
    public void flushOnFireUntilHalt( boolean fired, PropagationEntry currentHead ) {
        if ( !fired && currentHead == null) {
            synchronized (this) {
                if (head == null) {
                    halt();
                }
                currentHead = takeAll();
            }
        }
        while ( currentHead != null ) {
            flush( workingMemory, currentHead );
            currentHead = takeAll();
        }
    }

    public static void flush( InternalWorkingMemory workingMemory, PropagationEntry currentHead ) {
        for (PropagationEntry entry = currentHead; entry != null; entry = entry.getNext()) {
            entry.execute(workingMemory);
        }
    }

    @Override
    public synchronized PropagationEntry takeAll() {
        PropagationEntry currentHead = head;
        head = null;
        tail = null;
        return currentHead;
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

    private synchronized void halt() {
        try {
            wait();
        } catch (InterruptedException e) {
            // nothing to do
        }
    }

    @Override
    public synchronized void reset() {
        head = null;
        tail = null;
    }

    @Override
    public synchronized boolean isEmpty() {
        return head == null;
    }

    @Override
    public synchronized void notifyHalt() {
        notifyAll();
    }

    @Override
    public synchronized Iterator<PropagationEntry> iterator() {
        return new PropagationEntryIterator(head);
    }

    @Override
    public void onEngineInactive() { }

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
