package org.optaplanner.core.impl.heuristic.selector.common.iterator;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class SingletonIterator<T> implements ListIterator<T> {

    private final T singleton;

    private boolean hasNext;
    private boolean hasPrevious;

    public SingletonIterator(T singleton) {
        this.singleton = singleton;
        hasNext = true;
        hasPrevious = true;
    }

    public SingletonIterator(T singleton, int index) {
        this.singleton = singleton;
        if (index < 0 || index > 1) {
            throw new IllegalArgumentException("The index (" + index + ") is invalid.");
        }
        hasNext = (index == 0);
        hasPrevious = !hasNext;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public T next() {
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        hasNext = false;
        hasPrevious = true;
        return singleton;
    }

    @Override
    public boolean hasPrevious() {
        return hasPrevious;
    }

    @Override
    public T previous() {
        if (!hasPrevious) {
            throw new NoSuchElementException();
        }
        hasNext = true;
        hasPrevious = false;
        return singleton;
    }

    @Override
    public int nextIndex() {
        return hasNext ? 0 : 1;
    }

    @Override
    public int previousIndex() {
        return hasPrevious ? 0 : -1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(T t) {
        throw new UnsupportedOperationException();
    }

}
