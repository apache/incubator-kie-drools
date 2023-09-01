package org.drools.util;

public class BitMaskUtil {

    public static boolean intersect(long mask1, long mask2) {
        return (mask1 & mask2) != 0;
    }

    public static long set(long mask, int pos) {
        if (pos < 0) throw new IllegalArgumentException("negative position");
        return mask | (1L << pos);
    }

    public static long reset(long mask, int pos) {
        if (pos < 0) throw new IllegalArgumentException("negative position");
        return mask & (Long.MAX_VALUE - (1L << pos));
    }

    public static boolean isSet(long mask, long bit) {
        return (mask & bit) == bit;
    }

    public static boolean isPositionSet(long mask, int pos) {
        return isSet(mask, 1L << pos);
    }
}
