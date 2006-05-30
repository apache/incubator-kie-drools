package org.drools.common;

import org.drools.util.FastComparator;

public class IdentityAssertMapComparator extends FastComparator {
    /**
     * 
     */
    private static final long serialVersionUID = -4008762903660457691L;

    private final Class       factHandleClass;

    private final boolean     _rehash          = FastComparator.REHASH_SYSTEM_HASHCODE;

    public IdentityAssertMapComparator(final Class factHandleClass) {
        this.factHandleClass = factHandleClass;
    }

    public int hashCodeOf(final Object obj) {
        if (obj.getClass() == this.factHandleClass ) {
            return ( ( InternalFactHandle ) obj).getObjectHashCode();
        }
        return obj.hashCode();
    }

    /**
     * Special comparator that allows FactHandles to be keys, but always  checks
     * like for like.
     */
    public boolean areEqual(final Object o1,
                            final Object o2) {
        if ( o1.getClass() == this.factHandleClass ) {
            return  ( ( InternalFactHandle ) o1).getObject() == ( ( InternalFactHandle ) o2).getObject();
        }
        
        return o1 == ((InternalFactHandle) o2).getObject();
    }

    public int compare(final Object o1,
                       final Object o2) {
        return ((Comparable) o1).compareTo( o2 );
    }

    public String toString() {
        return "identity";
    }
}
