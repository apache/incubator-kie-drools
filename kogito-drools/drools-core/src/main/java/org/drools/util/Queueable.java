package org.drools.util;

public interface Queueable {
    public void enqueued(Queue queue,
                         int index);

    public void dequeue();
}
