package org.drools.core.util;
import java.util.Collection;

import org.drools.core.util.Queue.QueueEntry;

public interface Queue<T extends QueueEntry> {
    void enqueue(T queueable);

    T dequeue();
    void dequeue(T activation);

    boolean isEmpty();

    void clear();

    Collection<T> getAll();

    int size();

    T peek();

    interface QueueEntry {

        void setQueued(boolean b);

        int getQueueIndex();

        void setQueueIndex(int index);

        void dequeue();
        boolean isQueued();
    }
}
