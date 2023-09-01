package org.kie.dmn.feel.lang.types.impl;

import java.time.Period;
import java.util.Comparator;

public class InterceptNotComparableComparator implements Comparator<Object> {

    @Override
    public int compare(Object o1, Object o2) {
        Comparable c1 = coerceComparablePeriod(o1);
        Comparable c2 = coerceComparablePeriod(o2);
        return c1.compareTo(c2);
    }

    private Comparable<?> coerceComparablePeriod(Object o1) {
        Comparable<?> c1;
        try {
            c1 = (Comparable<?>) o1;
        } catch (ClassCastException e) {
            if (o1 instanceof Period) {
                c1 = new ComparablePeriod((Period) o1);
            } else {
                throw e;
            }
        }
        return c1;
    }

}