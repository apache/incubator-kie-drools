package org.drools.core.factmodel.traits;

import java.util.BitSet;

public interface TraitUtils {

    static boolean supersetOrEqualset(BitSet n1, BitSet n2 ) {
        BitSet x;
        int l1 = n1.length();
        int l2 = n2.length();

        if ( l1 > l2 ) {
            x = new BitSet( l2 );
            x.or( n2 );
            x.and( n1 );
        } else {
            x = new BitSet( l1 );
            x.or( n1 );
            x.and( n2 );
        }
        return x.equals( n2 );
    }

}
