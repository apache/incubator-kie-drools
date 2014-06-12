package org.drools.core.common;

import org.drools.core.phreak.TupleEntry;

import java.util.concurrent.atomic.AtomicInteger;

public class StreamTupleEntryQueue {
    private final TupleEntryQueue queue = new TupleEntryQueueImpl();

    private final AtomicInteger insertCounter = new AtomicInteger(0);
    private final AtomicInteger updateCounter = new AtomicInteger(0);
    private final AtomicInteger deleteCounter = new AtomicInteger(0);

    public TupleEntry peek() {
        return queue.peek();
    }

    public boolean addInsert(TupleEntry entry) {
        queue.add(entry);
        return insertCounter.getAndIncrement() == 0;
    }

    public boolean addUpdate(TupleEntry entry) {
        queue.add(entry);
        return updateCounter.getAndIncrement() == 0;
    }

    public boolean addDelete(TupleEntry entry) {
        queue.add(entry);
        return deleteCounter.getAndIncrement() == 0;
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized TupleEntryQueue takeAllForFlushing() {
        insertCounter.set(0);
        updateCounter.set(0);
        deleteCounter.set(0);
        return queue.takeAll();
    }
}