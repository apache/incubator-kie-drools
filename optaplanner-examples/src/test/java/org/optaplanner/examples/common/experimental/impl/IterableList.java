package org.optaplanner.examples.common.experimental.impl;

import java.util.Iterator;
import java.util.Objects;

public class IterableList<T> implements Iterable<T> {
    final Iterable<T> iterable;

    public IterableList(Iterable<T> iterable) {
        this.iterable = iterable;
    }

    public int size() {
        int size = 0;
        for (T item : this) {
            size++;
        }
        return size;
    }

    public T get(int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
        Iterator<T> itemIterator = iterator();
        T item = null;
        while (i >= 0) {
            if (!itemIterator.hasNext()) {
                throw new IndexOutOfBoundsException();
            }
            item = itemIterator.next();
            i--;
        }
        return item;
    }

    @Override
    public Iterator<T> iterator() {
        return iterable.iterator();
    }

    @Override
    public String toString() {
        return iterable.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IterableList<?> that = (IterableList<?>) o;
        return iterable.equals(that.iterable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iterable);
    }
}
