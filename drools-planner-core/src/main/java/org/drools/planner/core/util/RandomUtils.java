/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.util;

import java.util.Random;

public class RandomUtils {

    /**
     * Mimics {@link Random#nextInt(int)} for longs.
     * @param random never null
     * @param n > 0L
     * @return like {@link Random#nextInt(int)} but for a long
     * @see Random#nextInt(int)
     */
    public static long nextLong(Random random, long n) {
        // This code is based on java.util.Random#nextInt(int)'s javadoc.
        if (n <= 0L) {
            throw new IllegalArgumentException("n must be positive");
        }
        if (n < Integer.MAX_VALUE) {
            return (long) random.nextInt((int) n);
        }

        long bits;
        long val;
        do {
            bits = (random.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1L) < 0L);
        return val;
    }

    private RandomUtils() {
    }

}
