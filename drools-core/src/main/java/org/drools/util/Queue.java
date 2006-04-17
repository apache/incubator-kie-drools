package org.drools.util;

public interface Queue {
    public void enqueue(Queueable queueable);

    public Queueable dequeue();

    public Queueable dequeue(int index);

    public boolean isEmpty();
}
