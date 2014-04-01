package org.drools.core.common;

import org.drools.core.phreak.TupleEntry;

public interface TupleEntryQueue {
    boolean add(TupleEntry entry);

    TupleEntry peek();

    TupleEntry remove();

    int size();

    boolean isEmpty();

    TupleEntryQueueImpl takeAll();
}
