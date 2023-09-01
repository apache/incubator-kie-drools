package org.drools.core.util;

import java.util.NoSuchElementException;

public class CompositeIterator<T> implements java.util.Iterator<T> {

    private final java.util.Iterator<T>[] iterators;
    private int counter = 0;
    private T currentNext;

    public CompositeIterator( java.util.Iterator<T>... iterators ) {
        this.iterators = iterators;
        this.currentNext = internalNext();
    }

    @Override
    public boolean hasNext() {
        return currentNext != null;
    }

    @Override
    public T next() {
        if (currentNext == null) {
            throw new NoSuchElementException();
        }
        T result = currentNext;
        currentNext = internalNext();
        return result;
    }

    private T internalNext() {
        while (counter < iterators.length) {
            if (iterators[counter].hasNext()) {
                return iterators[counter].next();
            } else {
                counter++;
            }
        }
        return null;
    }
}
