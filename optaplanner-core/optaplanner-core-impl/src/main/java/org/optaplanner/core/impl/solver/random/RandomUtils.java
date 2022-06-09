package org.optaplanner.core.impl.solver.random;

import java.util.Random;

public class RandomUtils {

    /**
     * Mimics {@link Random#nextInt(int)} for longs.
     *
     * @param random never null
     * @param n {@code > 0L}
     * @return like {@link Random#nextInt(int)} but for a long
     * @see Random#nextInt(int)
     */
    public static long nextLong(Random random, long n) {
        // This code is based on java.util.Random#nextInt(int)'s javadoc.
        if (n <= 0L) {
            throw new IllegalArgumentException("n must be positive");
        }
        if (n < Integer.MAX_VALUE) {
            return random.nextInt((int) n);
        }

        long bits;
        long val;
        do {
            bits = (random.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1L) < 0L);
        return val;
    }

    /**
     * Mimics {@link Random#nextInt(int)} for doubles.
     *
     * @param random never null
     * @param n {@code > 0.0}
     * @return like {@link Random#nextInt(int)} but for a double
     * @see Random#nextInt(int)
     */
    public static double nextDouble(Random random, double n) {
        // This code is based on java.util.Random#nextInt(int)'s javadoc.
        if (n <= 0.0) {
            throw new IllegalArgumentException("n must be positive");
        }
        return random.nextDouble() * n;
    }

    private RandomUtils() {
    }

}
