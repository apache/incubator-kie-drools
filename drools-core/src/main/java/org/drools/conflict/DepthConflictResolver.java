package org.drools.conflict;

import org.drools.spi.Activation;
import org.drools.spi.ConflictResolver;

public class DepthConflictResolver
    implements
    ConflictResolver {
    /**
     * 
     */
    private static final long                 serialVersionUID = 7967558633422820901L;
    public static final DepthConflictResolver INSTANCE         = new DepthConflictResolver();

    public static DepthConflictResolver getInstance() {
        return DepthConflictResolver.INSTANCE;
    }

    /**
     * @see ConflictResolver
     */
    public final int compare(final Object existing,
                             final Object adding) {
        return compare( (Activation) existing,
                        (Activation) adding );
    }

    public int compare(final Activation lhs,
                       final Activation rhs) {
        final int s1 = lhs.getRule().getSalience();
        final int s2 = rhs.getRule().getSalience();

        if ( s1 != s2 ) {
            return s2 - s1;
        }

        final long p1 = lhs.getPropagationContext().getPropagationNumber();
        final long p2 = rhs.getPropagationContext().getPropagationNumber();
        if ( p1 != p2 ) {
            return (int) (p2 - p1);
        }

        final long r1 = lhs.getTuple().getRecency();
        final long r2 = rhs.getTuple().getRecency();

        if ( r1 != r2 ) {
            return (int) (r2 - r1);
        }

        final long l1 = lhs.getRule().getLoadOrder();
        final long l2 = rhs.getRule().getLoadOrder();

        return (int) (l2 - l1);
    }

}
