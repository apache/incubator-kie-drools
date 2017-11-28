package org.drools.modelcompiler.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class InsertionOrderSet<T> extends LinkedHashSet<T> implements SortedSet<T> {

    private static final long serialVersionUID = 8832757261917538212L;

    public InsertionOrderSet() {
        super();
    }

    public InsertionOrderSet(Collection<? extends T> c) {
        super(c);
    }

    @Override
    public Comparator<? super T> comparator() {
        throw new UnsupportedOperationException("will never implement");
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public T first() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return iterator().next();
        }
    }

    @Override
    public T last() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return this.stream().skip(this.size() - 1).findFirst().get();
        }
    }

}
