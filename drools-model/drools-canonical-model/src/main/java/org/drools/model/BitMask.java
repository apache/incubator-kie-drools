package org.drools.model;

import java.io.Serializable;
import java.util.Arrays;

import org.drools.model.bitmask.BitMaskUtil;
import org.drools.model.bitmask.LongBitMask;
import org.drools.model.bitmask.OpenBitSet;

public interface BitMask extends Serializable, Cloneable {

    BitMask set( int index );
    BitMask setAll( BitMask mask );

    BitMask reset( int index );
    BitMask resetAll( BitMask mask );

    boolean isSet( int index );
    boolean isAllSet();

    boolean isEmpty();

    boolean intersects( BitMask mask );

    BitMask clone();

    String getInstancingStatement();

    Class<?> getPatternClass();
    void setPatternClass( Class<?> patternClass );

    static BitMask getPatternMask( DomainClassMetadata metadata, String... listenedProperties ) {
        BitMask bitMask = BitMaskUtil.calculatePatternMask( metadata, true, listenedProperties );
        bitMask.setPatternClass( metadata.getDomainClass() );
        return bitMask;
    }

    static BitMask getEmpty(int numBits) {
        return numBits <= 64 ? new LongBitMask() : new OpenBitSet( numBits);
    }

    static BitMask getFull(int numBits) {
        if (numBits <= 64) {
            return new LongBitMask(-1L);
        }
        int nWords = (numBits / 64) + 1;
        long[] bits = new long[nWords];
        Arrays.fill(bits, -1L);
        return new OpenBitSet(bits, nWords);
    }
}
