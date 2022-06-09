package org.optaplanner.core.impl.domain.valuerange.util;

import java.util.Iterator;

public abstract class ValueRangeIterator<S> implements Iterator<S> {

    @Override
    public void remove() {
        throw new UnsupportedOperationException("The optional operation remove() is not supported.");
    }

}
