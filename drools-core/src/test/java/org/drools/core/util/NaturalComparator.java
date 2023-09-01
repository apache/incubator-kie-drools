package org.drools.core.util;

import java.util.Comparator;

public final class NaturalComparator<T>
    implements
    Comparator<T> {
    public static final NaturalComparator INSTANCE = new NaturalComparator();

    private NaturalComparator() {
    }

    public int compare(final T a,
                       final T b) {
        return ((Comparable) a).compareTo( b );
    }
}
