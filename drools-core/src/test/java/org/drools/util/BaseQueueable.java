package org.drools.util;

public class BaseQueueable
    implements
    Queueable {
    private Queue queue;
    private int   index;

    public void enqueued(Queue queue,
                         int index) {
        this.queue = queue;
        this.index = index;
    }

    public void dequeue() {
        this.queue.dequeue( this.index );
    }
}
