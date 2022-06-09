package org.optaplanner.core.impl.domain.valuerange.buildin.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.optaplanner.core.impl.domain.valuerange.AbstractCountableValueRange;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.CachedListRandomIterator;

public class ListValueRange<T> extends AbstractCountableValueRange<T> {

    private final List<T> list;

    public ListValueRange(List<T> list) {
        this.list = list;
    }

    @Override
    public long getSize() {
        return list.size();
    }

    @Override
    public T get(long index) {
        if (index > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("The index (" + index + ") must fit in an int.");
        }
        return list.get((int) index);
    }

    @Override
    public boolean contains(T value) {
        return list.contains(value);
    }

    @Override
    public Iterator<T> createOriginalIterator() {
        return list.iterator();
    }

    @Override
    public Iterator<T> createRandomIterator(Random workingRandom) {
        return new CachedListRandomIterator<>(list, workingRandom);
    }

    @Override
    public String toString() {
        // Formatting: interval (mathematics) ISO 31-11
        return list.isEmpty() ? "[]" : "[" + list.get(0) + "-" + list.get(list.size() - 1) + "]";
    }

}
