package org.drools.core.util;

public class BitMaskUtil {

    public static boolean intersect(long mask1, long mask2) {
        return (mask1 & mask2) != 0;
    }

    public static long set(long mask, int pos) {
        if (pos < 0) throw new IllegalArgumentException("negative position");
        return mask | 1L << pos;
    }
}
