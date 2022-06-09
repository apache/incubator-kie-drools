package org.optaplanner.core.impl.heuristic.selector.common.iterator;

import java.util.List;
import java.util.ListIterator;

/**
 * An extension on the {@link Iterable} interface that supports {@link #listIterator()} and {@link #listIterator(int)}.
 *
 * @param <T> the element type
 */
public interface ListIterable<T> extends Iterable<T> {

    /**
     * @see List#listIterator()
     * @return never null, see {@link List#listIterator()}.
     */
    ListIterator<T> listIterator();

    /**
     * @see List#listIterator()
     * @param index lower than the size of this {@link ListIterable}, see {@link List#listIterator(int)}.
     * @return never null, see {@link List#listIterator(int)}.
     */
    ListIterator<T> listIterator(int index);

}
