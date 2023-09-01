package org.kie.dmn.validation.dtanalysis.model;

import java.util.Comparator;

public class BoundValueComparator implements Comparator<Bound<?>> {

    @Override
    public int compare(Bound<?> o1, Bound<?> o2) {
        return compareValueDispatchingToInf(o1, o2);
    }

    public static int compareValueDispatchingToInf(Bound<?> o1, Bound<?> o2) {
        if (o1.getValue() != Interval.NEG_INF && o1.getValue() != Interval.POS_INF && (o2.getValue() == Interval.NEG_INF || o2.getValue() == Interval.POS_INF)) {
            return 0 - ((Comparable) o2.getValue()).compareTo(o1.getValue());
        }
        return ((Comparable) o1.getValue()).compareTo(o2.getValue());
    }

}
