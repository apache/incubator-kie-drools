package org.optaplanner.core.impl.heuristic.selector.common.iterator;

import java.util.Iterator;

public abstract class SelectionIterator<S> implements Iterator<S> {

    @Override
    public void remove() {
        throw new UnsupportedOperationException("The optional operation remove() is not supported.");
    }

}
