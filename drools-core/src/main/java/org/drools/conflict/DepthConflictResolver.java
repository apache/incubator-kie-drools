package org.drools.conflict;

import org.drools.spi.Activation;
import org.drools.spi.ConflictResolver;

public class DepthConflictResolver implements ConflictResolver {
    public static final DepthConflictResolver INSTANCE = new DepthConflictResolver();
    
    public static DepthConflictResolver getInstance() {
        return INSTANCE;
    }
    
    /**
     * @see ConflictResolver
     */
    public final int compare(Object existing,
                             Object adding) {
        return compare( (Activation) existing,
                        (Activation) adding );
    }
    
    public int compare(Activation lhs,
                       Activation rhs) {
        int s1 = lhs.getRule().getSalience();
        int s2 = rhs.getRule().getSalience();
        
        if (s1 != s2) {
            return s2 - s1;
        }
        
        long p1 = lhs.getPropagationContext().getPropagationNumber();
        long p2 = rhs.getPropagationContext().getPropagationNumber();
        if  ( p1 != p2 ) {
            return (int) (p2 - p1);
        }
        
        long r1 = lhs.getTuple().getRecency();
        long r2 = rhs.getTuple().getRecency();

        if ( r1 != r2 ) {
            return (int) (r2 - r1);
        }
       
       long  l1 = lhs.getRule().getLoadOrder();
       long  l2 = rhs.getRule().getLoadOrder();
       
       return (int) (l2 - l1);
    }

}
