package org.optaplanner.core.impl.solver.random;

import java.util.Random;

import org.junit.Test;
import org.optaplanner.core.impl.solver.random.RandomUtils;

public class RandomUtilsTest {

    @Test
    public void testNextLong() throws Exception {
        Random random = new Random(37);
        RandomUtils.nextLong(random, 10L + (long) Integer.MAX_VALUE);
    }

}
