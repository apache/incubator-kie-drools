package org.drools.core.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Collection;

import org.drools.core.util.Queue.QueueEntry;

public class ArrayQueue<T extends QueueEntry> implements Queue<T>, Externalizable {

    private java.util.Queue<T> queue = new ArrayDeque<>();

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeObject( queue );
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        this.queue = (java.util.Queue<T>) in.readObject();
    }

    @Override
    public void enqueue( T activation ) {
        queue.add( activation );
    }

    @Override
    public T dequeue() {
        return queue.poll();
    }

    @Override
    public void dequeue( T activation ) {
        queue.remove( activation );
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public Collection<T> getAll() {
        return queue;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public T peek() {
        return queue.peek();
    }
}
