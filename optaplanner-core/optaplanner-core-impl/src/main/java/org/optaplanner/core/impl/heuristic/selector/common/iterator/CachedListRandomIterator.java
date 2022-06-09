package org.optaplanner.core.impl.heuristic.selector.common.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.optaplanner.core.impl.heuristic.move.Move;

/**
 * This {@link Iterator} does not shuffle and is never ending.
 *
 * @param <S> Selection type, for example a {@link Move} class, an entity class or a value class.
 */
public class CachedListRandomIterator<S> extends SelectionIterator<S> {

    protected final List<S> cachedList;
    protected final Random workingRandom;
    protected final boolean empty;

    public CachedListRandomIterator(List<S> cachedList, Random workingRandom) {
        this.cachedList = cachedList;
        this.workingRandom = workingRandom;
        empty = cachedList.isEmpty();
    }

    @Override
    public boolean hasNext() {
        return !empty;
    }

    @Override
    public S next() {
        if (empty) {
            throw new NoSuchElementException();
        }
        int index = workingRandom.nextInt(cachedList.size());
        return cachedList.get(index);
    }

}
