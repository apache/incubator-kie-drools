package org.drools.core.util.bitmask;

import java.io.Serializable;

public interface BitMask extends Serializable, Cloneable {

    BitMask set(int index);
    BitMask setAll(BitMask mask);

    BitMask reset(int index);
    BitMask resetAll(BitMask mask);

    boolean isSet(int index);
    boolean isAllSet();

    boolean isEmpty();

    boolean intersects(BitMask mask);

    BitMask clone();

    String getInstancingStatement();

    public class Factory {
        public static BitMask getEmpty(int numBits) {
            return numBits <= 64 ? new LongBitMask() : new OpenBitSet(numBits);
        }

        public static BitMask getFull(int numBits) {
            if (numBits <= 64) {
                return new LongBitMask(-1L);
            }
            int nWords = (numBits / 64) + 1;
            long[] bits = new long[nWords];
            for (int i = 0; i < bits.length; i++) {
                bits[i] = -1L;
            }
            return new OpenBitSet(bits, nWords);
        }
    }

}
