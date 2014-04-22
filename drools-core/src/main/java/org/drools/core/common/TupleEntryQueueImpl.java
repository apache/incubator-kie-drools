package org.drools.core.common;

import org.drools.core.phreak.TupleEntry;

public class TupleEntryQueueImpl implements TupleEntryQueue {

    private volatile int size = 0;

    private TupleEntry head;
    private TupleEntry tail;

    public synchronized boolean add(TupleEntry entry) {
        if (head == null) {
            head = entry;
            tail = entry;
        } else {
            tail.setNext(entry);
            tail = entry;
        }
        return (size++ == 0);
    }

    public TupleEntry peek() {
        return head;
    }

    public synchronized TupleEntry remove() {
        TupleEntry entry = head;
        head = head.getNext();
        size--;
        return entry;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public synchronized TupleEntryQueueImpl takeAll() {
        TupleEntryQueueImpl clone = new TupleEntryQueueImpl();
        clone.head = this.head;
        clone.tail = this.tail;
        clone.size = this.size;

        this.head = null;
        this.tail = null;
        this.size = 0;
        return clone;
    }
}