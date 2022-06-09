package org.optaplanner.core.impl.heuristic.selector.common.iterator;

import java.util.ListIterator;

public abstract class SelectionListIterator<S> implements ListIterator<S> {

    @Override
    public void remove() {
        throw new UnsupportedOperationException("The optional operation remove() is not supported.");
    }

    @Override
    public void set(Object o) {
        throw new UnsupportedOperationException("The optional operation set(...) is not supported.");
    }

    @Override
    public void add(Object o) {
        throw new UnsupportedOperationException("The optional operation add(...) is not supported.");
    }

}
