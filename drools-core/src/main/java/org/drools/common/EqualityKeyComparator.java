package org.drools.common;

import org.drools.util.FastComparator;

public class EqualityKeyComparator extends FastComparator {
    /**
     * 
     */
    private static final long            serialVersionUID = -1368242567003294311L;
    private static EqualityKeyComparator instance;

    public static EqualityKeyComparator getInstance() {
        if ( EqualityKeyComparator.instance == null ) {
            EqualityKeyComparator.instance = new EqualityKeyComparator();
        }

        return EqualityKeyComparator.instance;
    }

    public int hashCodeOf(final Object obj) {
        return obj.hashCode();
    }

    /**
     * Equality key  reverses the compare, so  that  the  key  controls the  comparison
     */
    public boolean areEqual(final Object o1,
                            final Object o2) {
        return (o1 == null) ? (o2 == null) : (o1 == o2) || o2.equals( o1 );
    }

    public int compare(final Object o1,
                       final Object o2) {
        return ((Comparable) o1).compareTo( o2 );
    }

    public String toString() {
        return "direct";
    }
}
