package org.drools.util;

import java.util.Comparator;

public final class NaturalComparator
    implements
    Comparator {
    public static final NaturalComparator INSTANCE = new NaturalComparator();

    private NaturalComparator() {
    }

    public int compare(Object a,
                       Object b) {
        return ((Comparable) a).compareTo( b );
    }
}