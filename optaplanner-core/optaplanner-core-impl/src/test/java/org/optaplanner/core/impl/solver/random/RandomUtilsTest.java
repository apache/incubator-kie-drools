package org.optaplanner.core.impl.solver.random;

import java.util.Random;

import org.junit.jupiter.api.Test;

class RandomUtilsTest {

    @Test
    void testNextLong() {
        Random random = new Random(37);
        RandomUtils.nextLong(random, 10L + Integer.MAX_VALUE);
    }

}
